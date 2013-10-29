# OpenFlow Java library API Cookbook
_(work in progress)_

The main idea is to create an event-driven application. What develeloper does have to do is to implement his own event handler and launch the Controller listening a tcp port.
When Controller connects with a Switch and gets a version information from the Hello message, it starts an appropriate OpenFlow events handler, supporting a given OpenFlow vesrion which deals with OpenFlow messages and generates events against developer-defined event handler.

## OpenFlow protocol Java API
### OpenFlow Message and Structure handlers
Use Message and Structure handlers to comm

### OpenFlow Message provider
You may operate with OpenFlow protocol messages and structures via MessageProvider class.
```java
import org.flowforwarding.of.protocol.ofmessages.IOFMessageProvider;
import org.flowforwarding.of.protocol.ofmessages.IOFMessageProviderFactory;
import org.flowforwarding.of.protocol.ofmessages.OFMessageProviderFactoryAvroProtocol;

IOFMessageProviderFactory factory = new OFMessageProviderFactoryAvroProtocol();
IOFMessageProvider provider = factory.getMessageProvider("1.3");
```

Now you can use the Provider to operate OpenFlow messages.
Get messages in binary format ready to put into a channel:
```java
byte [] helloMessage = provider.encodeHelloMessage();
/*...*/
byte [] configRequestMessage = provider.encodeSwitchConfigRequest();
/*...*/
byte[] switchFeatureRequestMessage = encodeSwitchFeaturesRequest();
```
To get more complicated messages first you need to build it, and then add fields via Ref classes. Let's consider Flow Modification message:

```java
import org.flowforwarding.of.protocol.ofmessages.OFMessageFlowMod.OFMessageFlowModeRef;

OFMessageFlowModRef fmRef = provider.buildFlowMod();
fmRef.addTableId("1");
fmRef.addPriority("256");
/*...*/
fmRef.addMatchInPort("128");
/*...*/
OFStructureInstructionRef instrRef = provider.buildInstructionApplyActions();
instrRef.addActionOutput("12");
/*...*/
fmRef.addInstuction(instrRef);
byte [] fmBuffer = provider.encodeFlowMod(fmRef);
```
## OpenFlow Controller Java API
The controller infrastructure is started the next way:
```java
import org.flowforwarding.of.controller.Controller;
/*................*/
Controller.launch (SessionHandler.class); // This launches a controller listening tcp port 6633
Controller.launch (SessionHandler.class, configuration); // This launches a controller listening given tcp port 
```
### Configuration
It's a POJO containing some configuration information as tcp port number etc.
```java
import org.flowforwarding.of.controller.Configuration
/*................*/
Configuration config1 = new Configuration();     // tcp port 6633 by default
Configuration config2 = new Configuration(6633);
```

## Controller - Switch interaction
During the Controller-Sessions some events are generated. An application handles it via OF Events handler.

### Switch handler
The SwitchHandler class refers to a Switch connected to the Controller. It's unique, you have to use it to communicate with a Switch, send commands or get a Switch state:

```java
SwitchRef switchRef = SwitchHandler.create();
Long SwitchRef.getDpid();
void SwitchRef.setDpid(Long dpid);
```
Some code examples:
```java
import org.flowforwarding.of.ofswitch.SwitchState.SwitchHandler;

/*.................................................*/
public class SimpleHandler extends OFSessionHandler {
   @Override
   protected void handshaked(SwitchRef switchRef) {
      super.handshaked(switchRef);
      Long dpid = switchRef.getDpid();
   /*.........................................*/
   }
   /*.........................................*/
}
```

### Session handlers
You have to implement your session handler to interact with Controller. You may manage of incoming messages handling or send messages you want. Session handlers extend the class **OFSessionHandler**:
```java
import org.flowforwarding.of.controller.SwitchState.SwitchRef;
import org.flowforwarding.of.controller.session.OFSessionHandler;
import org.flowforwarding.of.controller.protocol.ofmessages.OFMessagePacketIn.OFMessagePacketInRef;

public class SimpleHandler extends OFSessionHandler {

   /*
    * User-defined Switch event handlers
    */
   @Override
   protected void handshaked(SwitchRef switchRef) {
      super.handshaked(switchRef);
      /*You have to implement your own logic here*/
   }

/*.................................................*/   
   @Override
   protected void connected(SwitchRef switchRef) {
      super.connected(switchRef);
      /*You have to implement your own logic here*/
   }

/*.................................................*/   
   @Override
   protected void packetIn(SwitchRef switchRef, OFMessagePacketInRef packetIn) {
      super.packetIn(switchRef);
      /*You have to implement your own logic here*/
   }
}
```
## Simple Learning Switch application.
You can find a source code in the package `org.flowforwarding.of.demo`

####org.flowforwarding.of.demo.Launcher.java
```java
package org.flowforwarding.of.demo;

import org.flowforwarding.of.controller.Controller;
import org.flowforwarding.of.controller.Controller.ControllerRef;

public class Launcher {
  public static void main(String[] args) {
      ControllerRef cRef = Controller.launch(SimpleHandler.class);
   }
}
```

####org.flowforwarding.of.demo
```java
package org.flowforwarding.of.demo;

import org.flowforwarding.of.controller.session.OFSessionHandler;
import org.flowforwarding.of.ofswitch.SwitchState.SwitchRef;
import org.flowforwarding.of.protocol.ofmessages.IOFMessageProvider;
import org.flowforwarding.of.protocol.ofmessages.OFMessageFlowMod.OFMessageFlowModRef;
import org.flowforwarding.of.protocol.ofmessages.OFMessagePacketIn.OFMessagePacketInRef;
import org.flowforwarding.of.protocol.ofmessages.OFMessageSwitchConfig.OFMessageSwitchConfigRef;
import org.flowforwarding.of.protocol.ofstructures.OFStructureInstruction.OFStructureInstructionRef;

public class SimpleHandler extends OFSessionHandler {

   @Override
   protected void switchConfig(SwitchRef switchRef, OFMessageSwitchConfigRef configRef) {
      super.switchConfig(switchRef, configRef);
      
      System.out.print("[OF-INFO] DPID: " + Long.toHexString(switchRef.getDpid()) + " Configuration: ");

      if (configRef.isFragDrop()) {
         System.out.println("Drop fragments");
      }
      
      if (configRef.isFragMask()) {
         System.out.println("Mask");
      }
      
      if (configRef.isFragNormal()) {
         System.out.println("Normal");
      }

      if (configRef.isFragReasm()) {
         System.out.println("Reassemble");
      }      
      
   }

   @Override
   protected void handshaked(SwitchRef switchRef) {
      super.handshaked(switchRef);
      System.out.println("[OF-INFO] HANDSHAKED " + Long.toHexString(switchRef.getDpid()));
      
      sendSwitchConfigRequest(switchRef);
   }
   
   @Override
   protected void packetIn(SwitchRef switchRef, OFMessagePacketInRef packetInRef) {
      super.packetIn(switchRef, packetIn);
      IOFMessageProvider provider = switchRef.getProvider();
      
      OFMessageFlowModRef flowModRef = provider.buildFlowModMsg();
      
      if (packetInRef.existMatchInPort()) {
         flowModRef.addMatchInPort(packetInRef.getMatchInPort().getMatch());
      } else if (packetIn.existMatchEthDst()) {
         flowModRef.addMatchEthDst(packetInRef.getMatchEthDst().getMatch());
      } else if (packetIn.existMatchEthSrc()) {
         flowModRef.addMatchEthSrc(packetInRef.getMatchEthSrc().getMatch());
      }
      
      OFStructureInstructionRef instructionRef = provider.buildInstructionApplyActions();
      instructionRef.addActionOutput("2");
      flowModRef.addInstruction("apply_actions", instructionRef);

      sendFlowModMessage(switchRef, flowModRef);
   }
}
```
