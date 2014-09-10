#Quick Start Guide: Warp OpenFlow controller
====================================

##Installation
Warp build and test process is managed with [sbt] (http://www.scala-sbt.org/), Scala build tool.

###Prerequisites

>1. Oracle Java SDK, versions 7 and 8 are verified
>2. Scala build tool, sbt, version 0.13. Installation instruction is [here] (http://www.scala-sbt.org/0.13/tutorial/Manual-Installation.html)

##How to build

It's very simple. Go to 
```bash
 $ git clone https://github.com/FlowForwarding/warp
 $ cd warp
 $ sbt assembly
```

Jar files will be placed:
```bash
./of_driver/target/scala-2.11/of_driver-assembly-0.5.jar
./controller/target/scala-2.11/controller-assembly-0.5.jar
```

##How to start controller

Start java controller:
```bash
 $ java -jar ./of_driver/target/scala-2.11/of_driver-assembly-0.5.jar
```
More detailed information can be found [here] (https://github.com/FlowForwarding/warp/wiki/1.-Warp-User's-Guide)

Start scala controller executable jar:
```bash
 $ java -jar ./controller/target/scala-2.11/controller-assembly-0.5.jar
```
In the console issue the command: ```start <ip> <port>```

More detailed information about controller usage you can find [here] (https://github)
