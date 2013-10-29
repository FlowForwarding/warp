##REST API
###Starting the Controller
####Controller
```bash
$ java -jar build/lib/warp.jar
Starting the internal [HTTP/1.1] server on port 8080
```
####LINC Switch
```bash
(linc@localhost)1> 23:30:23.416 [info] Connected to controller localhost:6633/0 using OFP v4
23:30:23.419 [debug] Received message from the controller: {ofp_message,4,features_request,0,{ofp_features_request}}
23:30:23.419 [debug] Sent message to controller: {ofp_message,4,features_request,0,{ofp_features_reply,<<0,12,41,189,55,56>>,0,0,255,0,[flow_stats,table_stats,port_stats,group_stats,queue_stats]}}
23:30:23.423 [debug] Received message from the controller: {ofp_message,4,set_config,0,{ofp_set_config,[],no_buffer}}
23:30:23.463 [debug] Received message from the controller: {ofp_message,4,get_config_request,0,{ofp_get_config_request}}
23:30:23.463 [debug] Sent message to controller: {ofp_message,4,get_config_request,0,{ofp_get_config_reply,[],no_buffer}}
23:30:43.444 [debug] Received message from the controller: {ofp_message,4,echo_request,0,{ofp_echo_request,<<>>}}
23:30:43.444 [debug] Sent message to controller: {ofp_message,4,echo_request,0,{ofp_echo_reply,<<>>}}
23:31:03.542 [debug] Received message from the controller: {ofp_message,4,echo_request,0,{ofp_echo_request,<<>>}}
23:31:03.542 [debug] Sent message to controller: {ofp_message,4,echo_request,0,{ofp_echo_reply,<<>>}}

```
###Installing Flow
#### Command
```bash
$ curl -d '{"switch":"00:0C:29:BD:37:38:00:00", "name":"flow-mod-1", "priority":"32768", "ingress-port":"2", "active":"true", "apply-actions":"output=3"}' http://localhost:8080/ff/of/controller/restapi
OK
```
#### Controller
```bash
Outgoing flow_mod
2013-09-15	23:33:20	127.0.0.1	-	-	8080	POST	/ff/of/controller/restapi	-	200	2	142	74	http://localhost:8080	curl/7.19.7 (x86_64-redhat-linux-gnu) libcurl/7.19.7 NSS/3.13.1.0 zlib/1.2.3 libidn/1.18 libssh2/1.2.2	-
```
#### LINC Switch
```bash
[debug] Received message from the controller: {ofp_message,4,flow_mod,0,{ofp_flow_mod,<<0,0,0,0,0,0,0,0>>,<<0,0,0,0,0,0,0,0>>,0,add,0,0,32768,0,0,0,[],{ofp_match,[{ofp_field,openflow_basic,in_port,false,<<0,0,0,2>>,undefined}]},[{ofp_instruction_apply_actions,2,[{ofp_action_output,16,3,no_buffer}]}]}}

(linc@localhost)1> linc_us4_flow:get_flow_table(0,0).
[{flow_entry,{32768,#Ref<0.0.0.1804>},
             32768,
             {ofp_match,[{ofp_field,openflow_basic,in_port,false,
                                    <<0,0,0,2>>,
                                    undefined}]},
             <<0,0,0,0,0,0,0,0>>,
             [],
             {1379,277200,837219},
             {infinity,0,0},
             {infinity,0,0},
             [{ofp_instruction_apply_actions,2,
                                             [{ofp_action_output,16,3,no_buffer}]}]}]
```
### Deleting Flow
#### Command
```bash
$ curl -X DELETE -d '{"switch":"00:0C:29:BD:37:38:00:00", "name":"flow-mod-1", "priority":"32768", "ingress-port":"2", "active":"true", "apply-actions":"output=3"}' http://localhost:8080/ff/of/controller/restapi
DELETED
```
#### LINC Switch
```bash
Received message from the controller: {ofp_message,4,flow_mod,0,{ofp_flow_mod,<<0,0,0,0,0,0,0,0>>,<<0,0,0,0,0,0,0,0>>,0,delete,0,0,32768,0,any,any,[],{ofp_match,[{ofp_field,openflow_basic,in_port,false,<<0,0,0,2>>,undefined}]},[{ofp_instruction_apply_actions,2,[{ofp_action_output,16,3,no_buffer}]}]}}

(linc@localhost)2> linc_us4_flow:get_flow_table(0,0).
[]
```
##OF Java API Demo
###Starting the Controller
```bash
$ java -cp target/of_lib.jar org.flowforwarding.of.demo.Launcher
[INFO] Getting Switch connection 
[INFO] Getting Switch connection 
[INFO] Getting Switch connection 

[OF-INFO] DPID: 1000c29bd3738 Feature Reply is received from the Switch 
[OF-INFO] Connected to Switch 1000c29bd3738
[OF-INFO] HANDSHAKED 1000c29bd3738
[OF-INFO] DPID: 2000c29bd3738 Feature Reply is received from the Switch 
[OF-INFO] Connected to Switch 2000c29bd3738
[OF-INFO] HANDSHAKED 2000c29bd3738
[OF-INFO] DPID: 2000c29bd3738 Switch Config is received from the Switch 
[OF-INFO] DPID: 1000c29bd3738 Switch Config is received from the Switch 
[OF-INFO] DPID: c29bd3738 Feature Reply is received from the Switch 
[OF-INFO] Connected to Switch c29bd3738
[OF-INFO] HANDSHAKED c29bd3738
[OF-INFO] DPID: c29bd3738 Switch Config is received from the Switch 
[OF-INFO] DPID: c29bd3738 Configuration: Normal
[OF-INFO] DPID: 1000c29bd3738 Configuration: Normal
[OF-INFO] DPID: 2000c29bd3738 Configuration: Normal
```
### LINC Switch
```bash
23:46:39.486 [info] Connected to controller localhost:6633/0 using OFP v4
23:46:39.486 [info] Connected to controller localhost:6633/0 using OFP v4
23:46:39.487 [debug] Received message from the controller: {ofp_message,4,features_request,0,{ofp_features_request}}
23:46:39.487 [debug] Sent message to controller: {ofp_message,4,features_request,0,{ofp_features_reply,<<0,12,41,189,55,56>>,1,0,255,0,[flow_stats,table_stats,port_stats,group_stats,queue_stats]}}
23:46:39.493 [info] Connected to controller localhost:6633/0 using OFP v4
23:46:39.496 [debug] Received message from the controller: {ofp_message,4,features_request,0,{ofp_features_request}}
23:46:39.496 [debug] Sent message to controller: {ofp_message,4,features_request,0,{ofp_features_reply,<<0,12,41,189,55,56>>,2,0,255,0,[flow_stats,table_stats,port_stats,group_stats,queue_stats]}}
23:46:39.499 [debug] Received message from the controller: {ofp_message,4,get_config_request,0,{ofp_get_config_request}}
23:46:39.499 [debug] Sent message to controller: {ofp_message,4,get_config_request,0,{ofp_get_config_reply,[],no_buffer}}
23:46:39.504 [debug] Received message from the controller: {ofp_message,4,get_config_request,0,{ofp_get_config_request}}
23:46:39.504 [debug] Sent message to controller: {ofp_message,4,get_config_request,0,{ofp_get_config_reply,[],no_buffer}}
23:46:39.505 [debug] Received message from the controller: {ofp_message,4,features_request,0,{ofp_features_request}}
23:46:39.505 [debug] Sent message to controller: {ofp_message,4,features_request,0,{ofp_features_reply,<<0,12,41,189,55,56>>,0,0,255,0,[flow_stats,table_stats,port_stats,group_stats,queue_stats]}}
23:46:39.506 [debug] Received message from the controller: {ofp_message,4,get_config_request,0,{ofp_get_config_request}}
23:46:39.507 [debug] Sent message to controller: {ofp_message,4,get_config_request,0,{ofp_get_config_reply,[],no_buffer}}

```
