blox_OFController
=================

Based on AVRO library

Based on AVRO library

How to build:

1. Setup a BLOX_OFCONTROLLER_ROOT environment variable where the project is placed, e.g.:
```bash
 $ cd <blox_of_controller_dir>
 $ export BLOX_OFCONTROLLER_ROOT=`pwd`
```

2. Build Avro library:
 ```bash
 $ cd $BLOX_OFCONTROLLER_ROOT/avro-trunk/lang/java/avro
 $ mvn install -DskipTests

 ```
2. Build of_lib.jar:
 ```bash
 $ cd $BLOX_OFCONTROLLER_ROOT
 $ ant
 ```

