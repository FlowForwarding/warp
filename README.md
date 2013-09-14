Quick Start Guide: blox_OFController
====================================

Based on AVRO library

1. Setup a BLOX_OFCONTROLLER_ROOT environment variable where the project is placed, e.g.:
```bash
 $ cd <blox_of_controller_dir>
 $ export BLOX_OFCONTROLLER_ROOT=`pwd`
```

2. Build of_lib.jar and JavaDoc API:
 ```bash
 $ cd $BLOX_OFCONTROLLER_ROOT
 $ ant
 $ ant javadoc
 ```

3. Running the OpenFlow Switch and blox_OFController
   Follow instructions @ https://github.com/FlowForwarding/LINC-Switch/wiki/Blox-OF-Controller-User's-Guide

   For Developers interested in using the API, use:
   https://github.com/FlowForwarding/LINC-Switch/wiki/OpenFlow-Java-library-API-Cookbook

4. Build Avro library (Optional Step):
 ```bash
 $ cd $BLOX_OFCONTROLLER_ROOT/avro-trunk/lang/java/avro
 $ mvn install -DskipTests
 ```

