#Quick Start Guide: blox_OFController
====================================

This OF Controller implementation is based on [Apache AVRO] (https://avro.apache.org/) library

>1. Setup a BLOX_OFCONTROLLER_ROOT environment variable where the project is placed, e.g.:
```bash
 $ cd <blox_of_controller_dir>
 $ export BLOX_OFCONTROLLER_ROOT=`pwd`
```

>2. Build of_lib.jar and JavaDoc API:
 ```bash
 $ cd $BLOX_OFCONTROLLER_ROOT
 $ ant
 $ ant javadoc
 ```

>3. Running the OpenFlow Switch and blox_OFController
   Blox OF Controller [User's Guide] (https://github.com/FlowForwarding/LINC-Switch/wiki/Blox-OF-Controller-User's-Guide)
   [OpenFlow Java library testing against OpenFlow 1.3 LINC Switch] (https://github.com/FlowForwarding/LINC-Switch/wiki/OpenFlow-Java-library-testing-against-OpenFlow-1.3-LINC-Switch)

   For Developers, interested in [using the API] (https://github.com/FlowForwarding/LINC-Switch/wiki/OpenFlow-Java-library-API-Cookbook)

>4. Build Avro library (Optional Step):
 ```bash
 $ cd $BLOX_OFCONTROLLER_ROOT/avro-trunk/lang/java/avro
 $ mvn install -DskipTests
 ```

>5. Additional Documentation (docs directory):
     (1) Using Apache Avro to implement an OpenFlow protocol library: UsingApacheAVROtoimplementOFProtocol.pdf
     (2) Details of Architecture and Implementation: Architecture_and_Implementation_Details.txt
     (3) 'ant javadoc' target builds API documentation and is placed in docs/api directory
