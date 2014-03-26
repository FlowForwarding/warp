# Warp User's Guide
_(work in progress)_

## Build
Go to the directory where have Controller unpacked or cloned from GitHub, let's refer it as _$WARP_ROOT_. Then build Avro and the Controller itself:
```bash
$ cd $WARP_ROOT/src/main/java/avro-trunk/lang/java/avro
$ mvn install -DskipTests
$ cd $WARP_ROOT
$ ant
```
Now Controller's jar file is in _$WARP_ROOT/build/lib_ directory.

## Running the Controller
Currently, you can run a simple REST API service to the controller and a simple learning switch application. They are both are using OF Java API.
To run REST API:
```bash
$ java -jar build/lib/warp.jar
```
To run learning switch application:
```bash
$ java -cp build/lib/warp.jar org.flowforwarding.of.demo.Launcher
```
In both cases controllers are listening tcp port 6633. 

### REST API commands
You can install flows using the `curl` command. Below are some instances:
```bash
$ curl -d '{"switch":"00:0C:29:C9:8E:AE:00:00", "name":"flow-mod-1", "priority":"32768", "ingress-port":"1", "active":"true", "apply-actions":"output=1"}' http://localhost:8080/ff/of/controller/restapi

$ curl -d '{"switch":"00:0C:29:C9:8E:AE:00:00", "name":"flow-mod-1", "priority":"32768", "ingress-port":"2","active":"true"}' http://localhost:8080/ff/of/controller/restapi

$ curl -d '{"switch":"00:0C:29:AC:93:43:00:00", "name":"flow-mod-1", "priority":"32768", "ether-type":"0x0800", "active":"true"}' http://localhost:8080/ff/of/controller/restapi

$ curl -d '{"switch":"00:0C:29:AC:93:43:00:00", "name":"flow-mod-1", "priority":"32768", "ether-type":"0x0800", "protocol": "6", "dst-ip":"10.10.10.10","active":"true"}' http://localhost:8080/ff/of/controller/restapi

$ curl -d '{"switch":"00:90:FB:37:71:6E:00:00", "name":"flow-mod-1", "priority":"10", "ingress-port":"5","active":"true", "write-actions":"output=6"}' http://localhost:8080/ff/of/controller/restapi

$ curl -d '{"switch":"00:90:FB:37:71:6E:00:00", "name":"flow-mod-1", "priority":"10", "ingress-port":"6","active":"true", "apply-actions":"output=5, output=6"}' http://localhost:8080/ff/of/controller/restapi
```

The actions and match criterias are:
####Actions
    Name     |Description  |
    ---------|-------------|
    apply-actions||
    write-actions||
    clear-actions||

####Match Criterias
    Name     |Description  | Prerequisites
    -------- | ----------- | --------
    ingress-port| Ingress port. This may be a physical or switch-dened logical port | 
    in-phy-port | |
    metadata ||
    src-mac |Ethernet source address|
    dst-mac|Ethernet destination address|
    ether-type|Ethernet type of the OpenFlow packet payload|
    vlan-vid||
    vlan-priority||
    ip-dscp||
    ip-ecn||
    protocol|IPv4 or IPv6 protocol number|
    src-ip|IPv4 source address|
    dst-ip|IPv4 destination address|
    src-port|TCP source port|
    dst-port|TCP destination port|
    udp-src|UDP source port|
    udp-dst|UDP destination port|
    sctp-src||
    sctp-dst||
    icmpv4-type||
    icmpv4-code||
    arp-op||
    arp-spa||
    arp-tpa||
    arp-sha||
    arp-tha||
    ipv6-src|IPv6 source address|
    ipv6-dst|IPv6 destination address|
    ipv6-flabel||
    icmpv6-type||
    icmpv6-code||
    ipv6-nd-sll||
    ipv6-nd-tll||
    mpls-label||
    mpls-tc||
    mpls-bos||
    pbb-isid||
    tunnel-id||
    ipv6-exthdr||

To delete flow from a switch with REST API you can use `curl -X DELETE` command

####Installing flow
```bash
curl -d '{"switch":"00:0C:29:BD:37:38:00:00", "name":"flow-mod-1", "priority":"32768", "ingress-port":"2", "active":"true", "apply-actions":"output=3"}' http://localhost:8080/ff/of/controller/restapi
```
####Deleting flow
```bash
curl -X DELETE -d '{"switch":"00:0C:29:BD:37:38:00:00", "name":"flow-mod-1", "priority":"32768", "ingress-port":"2", "active":"true", "apply-actions":"output=3"}' http://localhost:8080/ff/of/controller/restapi
```

## Useful LINC information

The steps needed to use this controller with Linc switch are below.
1. Make and run LINC by doing the following:
```bash
$ sudo make rel
$ sudo ./rel/linc/bin/linc console
```
2. To find out switch Dpid, in linc switch console, type
```erlang
>1 linc_logic:get_datapath_id(SwitchId).
```
The output will be like:
```erlang
"00:0C:29:C9:8E:AE:00:00"
```
To check flow_table:
```erlang
>1 linc_us4_flow:get_flow_table(SwitchId,0).
```
