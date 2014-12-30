# Send-to-switch API

You could directly query switches and send commands using special feature of Warp controller: Send-to-Switch Rest API.
Please note that this API, as well as other Rest APIs, works only with drivers supporting Dynamic API.
To make it available, you have to add two modules: [RestApiServer]() (needed to start any Rest-service) and [SendToSwitchService](). To do it, issue the following comamnds in the interactive console:
```
add module rest_api_server of type org.flowforwarding.warp.controller.modules.rest.RestApiServer -p /controller/nb/v2
add module send_to_switch of type org.flowforwarding.warp.controller.modules.rest.SendToSwitchService -p /controller/nb/v2
```
After that, when controller starts on ip ```0.0.0.0``` and gets connection with switch having ID e.g. ```8796758431676```, you will be able to send echo request in the following way (using [CURL](https://en.wikipedia.org/wiki/CURL)):

```bash
curl -d <body> -X POST http://0.0.0.0:8080/controller/nb/v2/send
```

where body is
```javascript
{
    "dpid": 8796758431676,         // Switch id
    "version": 4,                  // OFP v1.3
    "reply": true,                 // Controller should deliever switch response
    "message": { "EchoRequest": {  // Type of message
        "elements": [5, 5, 5, 5]   // Fields of message
    } }
}
```

If connection with specified switch is still alive, you will get the following reply:
```javascript
{
    "EchoReply":{
        "elements":[5,5,5,5],
        "header":{"Header":{"xid":4}}
    }
}
```

Note that although you have not specify header in requests, it is present in reply. It may be used to identify transaction id, but usually should be ignored.

Another example - sending multipart messages (e.g. SwitchDescription). They slightly differ form other messages: their type is always ```MultipartRequest``` and actual type of message is identified by type of ```message.body``` structure:
```javascript
{
    "dpid": 8796758431676,
    "version": 4,
    "reply": true,
    "message": { "MultipartRequest": { 
        "flags": false, 
        "body":  { "SwitchDescriptionRequestBody": { } } 
    } }
}
```

Response has the following structure:

```javascript
[
    { "MultipartReply": {
        "flags": "false",
        "body": { "SwitchDescriptionReplyBody": { "value": {
            "SwitchDescription": {
                "serialNumber":"Unknown",
                "datapath":"Unknown",
                "hardware":"Unknown",
                "software":"LINC OpenFlow Software Switch 1.1",
                "manufacturer":"FlowForwarding.org"
            }
        }}},
        "header":{"Header":{"xid":5}}}
    } }
]
```