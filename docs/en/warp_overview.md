# Overview of Controller and Driver API

## Running

Entry point of framework is object ```ModuleManager```. Run it to start [interactive console] [InteractiveConsole], which, in its turn, allows you manage components and start accepting of incoming connections (command ```start <ip> <port>```).

## Components

Component is a Java class designed according to several rules. For now, there are two main types of components: [factory of message drivers] [MessageDriverFactory] and [module] [Module].

The purpose of the first type is providing a set of message drivers, classes that decode incoming messages (messages from switches to controller) and encode outgoing messages (messages from controller to switches). The reason why there is a set of drivers is that controller is able to serve different protocol versions, each of which can be supported by a different driver.

The second type of components can be used for different purposes, from [handling messages from switches] [MessageHandlers] (it is the main purpose) to executing actions that are not related to SDN at all (that is not a good idea in general). Typically, module reacts on an event occurred in the system via publishing other events.

Commands for components management (in interactive console):
- ```set factory <factory_class_name> -p [<param1>, < param2>, …]``` - loads factory (reloading is not implemented yet)
- ```add module <module_name> of type <module_class_name> -p [<param1>, <param2>, …]``` – loads module
- ```rm module <module_name>``` - removes already loaded module

###### Note
- Classes of factories and modules must be included in class path.
- Loaded factory and modules must be [compatible] [Compatibility] each with other.

## Message Drivers

Message driver is a class which implements MessageDriver interface. These classes are actually implementations of OpenFlow protocol. The protocol implemented by a driver could match an opennetworking specification or could implement a proprietary-extended protocol. Message drivers are used by controller to serialize objects representing OF messages to arrays of bytes and deserialize them and to perform procedure of handshake.

### MessageDriver trait

```trait MessageDriver[T <: OFMessage]``` is a union of traits, listened below:

- ```trait OfpVersionSupport``` - provides information about version supported by driver
- ```trait OfpHandshakeSupport``` - provides methods used in the handshake procedure
- ```trait OfpFeaturesExtractor[-T <: OFMessage]``` - provides methods which extract values of some common fields from raw data or already deserialized object.
- ```trait OfpMessageEncoder[-T <: OFMessage]``` - serializes object of type T to array of bytes
- ```trait OfpMessageDecoder[+T <: OFMessage]``` - deserializes object of type T from array of bytes

###### Note
The tree last traits are parametrized over type of messages they deal with. Driver developers define it and it must be a subclass of ```OFMessage```.

### Dynamic drivers and dynamic API
One of the main goals of warp is to provide a way of definition of user-defined versions of OpenFlow protocols which can be reloaded without restart of the entire controller and to develop end-user API to operate with such protocols.

Package [org.flowforwarding.warp.driver_api.dynamic] [PackageDynamic] provides a set of classes to achieve the above goal.

```trait DynamicStructure``` represents messages, structure of which is not defined in compile-time but depends on the loaded driver definition. User is able to get fields of structures using names of that fields.

```trait DynamicStructureBuilder[+E <: DynamicStructure]``` builds dynamic structures from instances of ```DynamicBuilderInput```s and makes ```DynamicBuilderInput```s which user fills.

```trait DynamicBuilderInput``` represents a structure which should be built as its name and a set of named members.

```trait DynamicDriver[E <: DynamicStructure]``` provides functionality of MessageDriver and DynamicStructureBuilder for a specific subtype of DynamicStructure.

Consider an example of these traits usage:

```java
DynamicStructureBuilder driver = // e.g. we got it as a parameter of a method
DynamicStructure incomingMessage = // ...
if(incomingMessage.isTypeOf("ofp_switch_features_reply")){
    System.out.println("Connected to: " + incomingMessage.primitiveField("datapath_id"));

    DynamicBuilderInput reqHeader =
        driver.newBuilderInput("ofp_header")
              .setMember("xid", new DynamicPrimitive(XidGenerators.nextXid())))
              .setMember("length", new DynamicPrimitive(8L + 4));

    DynamicBuilderInput request =
        driver.newBuilderInput("echo_request")
              .setMember("header", new DynamicStructureInput(reqHeader))
              .setMember("elements", new DynamicPrimitives(new long[]{ 2, 2, 2, 2});

    DynamicStructure result = driver.build(request).get()
    // send result
}
```

###### Note
- Look through the definition of the used protocol to determine which fields are supported by DynamicStructures or DynamicBuilderInputs. It is impossible and actually unnecessary to have this information in runtime.
- If you have changed and reloaded definition of protocol, modules which use Dynamic API could got broken because of changed names of fields. In this case all the modules which used changed fields shoul be fixed and reloaded too.
- This API is quite verbose, so controller provides two wrappers (for both [Scala] [DynamicScalaApi] and [Java] [DynamicJavaApi]) providing more convenient and language-specific APIs for writing [MessageHandlers] [MessageHandlers].


### Fixed API
Warp controller provides class-based APIs for opennetworking-specified versions of OpenFlow protocol (for now for *1.3* only). The advantage of these APIs is type-safety: user operates with compile-time defined structures and handlers, so there is no need to check types of messages and no risk to get a runtime exception because of absence of a required field. Fixed APi is built on top of Dynamic API.

To declare support of Fixed API for version *V*, dynamic driver should be mixed with a trait corresponding to version *V*, e. g. to support version *1.3*, it should be mixed with *Ofp13DriverApi*. Such traits require definition of field ```val namesConfig: Config``` that shows how fields required by this version of Fixed API are named in the underlying driver.

Besides class-based representation, Fixed API supports textual representation of OFP structures:
- each **Structure** is convertible to its text view using method ```def textView: BITextView```
- Fixed API Driver is able to parse **BuilderInputs** using method ```def parseTextView(input: BITextView): Try[BuilderInput]```

Textual representation was developed for interconnection with out-of-controller sources of messages, e. g. it is used in [Send-To-Switch Rest API](send_to_switch_api.md).

Information about definition of new versions of fixed API you can find in [corresponding guide](how_to_define_new_fixed_api.md).

###### Note
The current implementation of Fixed API expects that underlying DynamicDriver is able to determine values of fields based on values of other fields or type of structure (like fields *length* and *type* of structure ofp_header).

### Factories of Message Drivers

Each driver instance supports own version of protocol, but controller is able to serve switches which work with different protocol versions. That is the reason why ```trait MessageDriverFactory``` exists: when switch connects to controller, controller asks the loaded factory about the highest common version between versions of drivers it provides and versions supported by the switch. Then this version is used to get a driver which will serve this concrete switch. 

Factories of Message Drivers are loadable components of warp framework. It should be the first component loaded into controller because some modules may have specific requirements to factories (support of a particular version of Fixed API or common message type, etc) and these requirements must be tested while module loading. 


###### Note
Although further goal of warp is protocol-independency, for now message drivers and factories are designed to support only OpenFlow protocols.

### Implemented message drivers
For now there are three implementations of MessageDriver:
- [Scala driver] [ScalaDriver]
- [Java driver] [JavaDriver]
- [DSL driver] [DSLDriver]


## Event Buses

Event bus in warp is implemented in terms of actors and messages. It is the main way of communication between modules and place where messages from switches emerge, therefore, every module [holds a reference] [ModuleDev] to a needed message bus.

There are two types of buses: MessageBus and ServiceBus.
MessageBus provides a way of subscription and publishing messages (fire-and-forget model), whereas ServiceBus registers services and allows other modules turn to them for information (request-reply model).
ControllerBus unites both ServiceBus and MessageBus; it is created once during initialization of framework and shared between all the modules, so that there is only one EventBus for now.

To grant an actor access to an event bus, mixin trait ```ServiceBusActor```, ```MessageBusActor``` or ```ControllerBusActor``` to your actor class.

### MessageBusActor trait

MessageBusActor provides the following methods:

```scala
val bus: MessageBus
final def publishMessage(msg: MessageEnvelope)
final def subscribe(subscriptionName: String)(subscriptionPredicate: PartialFunction[MessageEnvelope, Boolean]): Unit
final def subscribe(subscriptionName: String, subscriptionPredicate: MessageEnvelopePredicate): Unit /* Java API */
final def unsubscribe(subscriptionName: String): Unit
final def unsubscribe(): Unit
```

```publishMessage``` publishes a message on the message bus.

Method ```subscribe``` defines which messages actor would receive. Note that there could be several subscriptions, each of which can be dynamically removed using string-identifier of subscription and method ```unsubscribe(subscriptionName: String)``` or remove them all using ```unsubscribe()```.

### ServiceBusActor trait

ServiceBusActor provides the following methods:

```scala
val bus: ServiceBus
final def askService(request: ServiceRequest): Future[Any]
final def registerService(acceptedRequests: PartialFunction[ServiceRequest, Boolean])
final def unregisterService(): Unit
```

```askService``` publishes request on the bus and returns a Future which is successful and holds response if a service was able to handle request and it is failed if service was not found or failed to handle request.

Actors use ```registerService``` to declare self as a service and define which types of requests they are able to handle and ```unregisterService``` to remove self from the list of services.

```trait MessagesSender[M <: OFMessage]``` is successor of ServiceBusActor and provides methods for sending messages of type ```M``` to switches. It relies on service provided by SwitchConnector.
```trait FixedStructuresSender``` simplifies send routines for FixedApi: it deals with ```BuilderInput```s and ```TextView```s and assumes that message driver supports Fixed API. 

###### Note

Method ```bus``` of BusActors is abstract and is typically implemented as a parameter of constructor of class implementing it (in Scala).

### Bus messages
```ServiceRequest``` and ```MessageEnvelope``` are the basic traits for each request and message, respectively. Their successors are usually defined in a companion object of actor which introduces this kind of message to the system.

*"Actor introduces"* means that this kind of message is not already defined and
 - actor is a service which serves this kind of requests, or
 - actor fires such messages, or
 - actor reacts on this kind of message.

## Modules

Module is the basic abstraction for component of warp controller framework. Since Warp is built on top of Akka, it is naturally to represent each module as an Actor, so trait Module extends Actor and provides additional methods
```scala
protected def started(): Unit
protected def shutdown(): Unit
protected def compatibleWith(factory: MessageDriverFactory[_]): Boolean
```

Method ```started``` is a good place for initialization of module (allocate resources, subscribe to events, register services etc.), ```shutdown``` should hold logic of finalization, and ```compatibleWith``` tests is this module compatible with the loaded driver factory.

### How to develop a module
As mentioned above, components are represented by Java classes. In case of modules, class should be successor of ```trait Module```.

To develop a module loadable by module manager, user should:

1. Inherit from class Module or its successor.
2. Provide constructor which takes ```XxxBus``` as the first parameter and strings as others (variable-size argument is allowed too).

Note that although user can inherit from trait Module directly and mix it with MessageBusActor or ServiceBusActor traits to make the module be able to communicate with other modules (see predefined module [RestApiServer]), typically it is not necessary, because of two traits-successors of Module – [MessageConsumer] and [Service].

### MessageConsumer trait

MessageConsumer is the basic trait for all modules which handle events (like MessageHandlers). It mixes Module with MessageBusActor and provides a method
```scala
protected def handleEvent(e: MessageEnvelope): Unit
```
which should be overridden in successors in order to handle messages on which this Module is subscribed. It is supposed that MessageConsumer subscribes for messages overriding method ```started``` and\or as reaction on an event.

### Service trait

Service is the basic trait for all modules providing services. It mixes Module with ServiceBusActor and provides a method
```scala
protected def handleRequest(e: ServiceRequest): Future[Any]
```
which should be overridden in successors in order to handle requests to a service which this Module provides. It is supposed that Service declares service it provides in overridden method ```started```.

As an example of service implementation, consider
- [RestApiService], a template for all Rest-Api services
- and its successor, a predefined service [SendToSwitchService]

### Compatibility with message driver

Sometimes modules require concrete requirements to driver (e.g. it must be a DynamicDriver), supported versions of protocol etc. Specify this contract in module's method ```compatibleWith```, which is called while controller loads module to make sure it will work correct. Note that this method takes ```MessageDriverFactory``` as a parameter, thus it is possible to check availability of drivers with required properties.

### Message handlers

Message handler is a module which reacts on messages from switches. Every message handler is a subtype of
```abstract class MessageHandlers[T <: OFMessage, ApiSupport <: OfpVersionSupport]```. This class is parametrized over subtype of OFPMessage which it is able to handle (type ```T```) and API, typically used to create response or extract information from incoming message (type ```ApiSupport```). ```MessageHandlers``` mixes ```trait MessageConsumer``` and ```trait ServiceBusActor``` and provides additional methods

```scala
def supportedVersions: Array[UByte]
def handleMessage(api: ApiSupport, dpid: ULong, msg: T): Try[Array[T]]
def handleDisconnected(api: ApiSupport, dpid: ULong): Unit
def handleHandshake(api: ApiSupport, dpid: ULong): Unit
```

Method ```supportedVersions``` declares versions of OpenFlow protocol this module is able to handle, methods ```handleHandshake``` and ```handleDisconnected``` define reaction on connection and disconnection of a switch, and ```handleMessage``` defines response (a sequence of outgoing messages) to an incoming message.

Each message handler is an actor, it has local state and own messages queue, so it may become a bottleneck. This problem can be solved using programmatic loading of several different handlers of the same type (send service request ```ModuleManager.AddModule```) and distribution of messages between them (override method ```started``` and there subscribe to a messages filtered by id of switch, for example).

###### Note
There are several successors of ```class MessageHandlers```, providing more convenient usage of Dynamic API, Fixed APIs and driver-specific APIs. Those of them which were designed for a specific implementation of API, locate in corresponding ```<specific-implementation-name>-adapter``` projects.

## OpenDaylight-compatible Rest API

Project [OpenDaylight](http://www.opendaylight.org/) provides set of [Rest APIs](https://wiki.opendaylight.org/view/OpenDaylight_Controller:REST_Reference_and_Authentication) for switch management and network state monitoring. Warp implements some of them as part of controller, so many applications which use these API could be used together with warp.

The following APIs are implemented:
- [Connection Manager](https://jenkins.opendaylight.org/controller/job/controlller-merge-hydrogen-stable/lastSuccessfulBuild/artifact/opendaylight/northbound/connectionmanager/target/site/wsdocs/index.html)
- [Switch Manager](https://jenkins.opendaylight.org/controller/job/controlller-merge-hydrogen-stable/lastSuccessfulBuild/artifact/opendaylight/northbound/switchmanager/target/site/wsdocs/index.html)
- [Flow Programmer](https://jenkins.opendaylight.org/controller/job/controlller-merge-hydrogen-stable/lastSuccessfulBuild/artifact/opendaylight/northbound/flowprogrammer/target/site/wsdocs/index.html)
- [Topology](https://jenkins.opendaylight.org/controller/job/controlller-merge-hydrogen-stable/lastSuccessfulBuild/artifact/opendaylight/northbound/topology/target/site/wsdocs/index.html)

Note that controller provides modules implementing protocol-independent [abstrations] [AbstractRestServices], which require modules which provide concrete underlying implementations. These modules should be loaded into controller together with basic modules. You could find them in corresponding ```<specific-implementation-name>-adapter``` projects. For now they exist (but Topology) only for [Fixed OpenFlow 1.3 API] [FixedOFP13RestServices], project ```driver-api-ofp13-adapter```.

More information about development of modules supporting Rest API for your driver you can find [here] [RestServicesDev].

[ScalaDriver]:_
[JavaDriver]:_
[DSLDriver]:_

[PackageDynamic]:_
[DynamicScalaApi]:_
[DynamicJavaApi]:_
[MessageHandlers]:#message-handlers

[ModuleManager]:#module-manager
[InteractiveConsole]:interactive_console.md
[MessageDriverFactory]:#factories-of-message-drivers
[Compatibility]:#compatibility-with-message-driver
[Module]:#modules
[ModuleDev]:#how-to-develop-a-module
[MessageHandlers]:#message-handlers

[RestApiServer]:#link-to-scaladoc
[RestApiService]:#link-to-scaladoc
[SendToSwitchService]:#link-to-scaladoc
[MessageConsumer]:#link-to-scaladoc
[Service]:#link-to-scaladoc

[AbstractRestServices]:#link-to-scaladoc
[FixedOFP13RestServices]:#link-to-scaladoc
[RestServicesDev]:#how-to-provide-rest-services-support