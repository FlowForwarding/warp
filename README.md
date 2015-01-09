# Warp

Warp is a component-based software defined networking framework built on top of Akka. It holds connections with SDN switches, supports dynamic (re)loading of predefined or user-defined components and provides event-based way of communication between loaded components and connected switches.

## Building Warp
Warp build and test process is managed with [sbt](http://www.scala-sbt.org/), Simple build tool.

### Prerequisites

>1. Oracle Java SDK, versions 7 and 8 are verified
>2. Simple build tool, sbt, version 0.13. Installation instruction is [here](http://www.scala-sbt.org/0.13/tutorial/Manual-Installation.html).

### How to build

It's very simple. Go to
```bash
 $ git clone https://github.com/FlowForwarding/warp
 $ cd warp
 $ sbt assembly
```

### Build output

* basic driver API and controller
    * ./driver-api/target/scala-2.11/driver-api-assembly-0.5.jar
    * ./controller/target/scala-2.11/controller-assembly-0.5.jar

* driver-independent OpenFlow Protocol 1.3 API
    * ./driver-api-ofp13/target/scala-2.11/driver-api-ofp13-assembly-0.5.jar
    * ./driver-api-ofp13-adapter/target/scala-2.11/driver-api-ofp13-adapter-assembly-0.5.jar

* implementation of OpenFlow Protocol 1.3 driver
    * ./sdriver/target/scala-2.11/sdriver-assembly-0.5.jar
    * ./sdriver-ofp13/target/scala-2.11/sdriver-ofp13-assembly-0.5.jar
    * ./sdriver-ofp13-adapter/target/scala-2.11/sdriver-ofp13-adapter-assembly-0.5.jar

* an all-in-one jar for quick start
    * ./demo/target/scala-2.11/demo-assembly-0.5.jar

More information about subprojects you can find [here](docs/en/warp_subprojects.md).

## Start controller
Warp comes with sample project ```demo```, output jar of which may be used to start controller
```bash
 $ java -cp ./demo/target/scala-2.11/demo-assembly-0.5.jar org.flowforwarding.warp.controller.ModuleManager
```
Then in the interactive console issue the command: ```start <ip> <port>```.

Please note that the way above is the easiest way to start controller, but the main idea of Warp framework is modularity, so the supposed way of controller usage is to pick only jars you really need. For example, if you want to execute instructions provided in demo project, you have to run controller in the following way:
```bash
 $ java -cp ./controller/target/scala-2.11/controller-assembly-0.5.jar:./driver-api-ofp13-adapter/target/scala-2.11/driver-api-ofp13-adapter-assembly-0.5.jar:./sdriver-ofp13/target/scala-2.11/sdriver-ofp13-assembly-0.5.jar org.flowforwarding.warp.controller.ModuleManager ./demo/src/main/resources/instructions.txt
```
Another way to run controller is to build it using command ```sbt package``` instead of ```sbt assembly```. Resulting jars will appear as ```./<project>/target/scala-2.11/<project>_2.11-0.5.jar``` and will not contain any third-party classes, therefore all the warp dependencies must be present in classpath in order to run controller.

More detailed information about commands, controller usage, modularity and framework architecture you can find [here](docs/en/warp_overview.md).
