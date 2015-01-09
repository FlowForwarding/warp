# Interactive console

## How to use

Warp interactive console is command-line interface for warp modules management. After application start controller doesn't listens to incoming connections.
At first, you should specify [factory of message drivers](warp_overview.md#factories-of-message-drivers), then you may add modules you need and after that start listening.
After start, you are still able to add modules which you forgot to add or modules which should be added to a listening controller.
If you don't need a module anymore or you want to reload a new version, you are able to remove any module. Note that other modules may depend on services provided by module you are going to remove, so sometimes module removing may harm functionality of other modules, but usually doesn't affect controller in general.

## Commands description

| Command                                                                                     | Description |
|:--------------------------------------------------------------------------------------------|:-------- |
| **:help**                                                                                   | Prints short summury of all commands. |
| **:paste**                                                                                  | Enters paste mode. This mode allows you copy-paste instructions (delimited by new line symbols) as well as writing them manually. Print two blank lines to leave paste mode. |
| **:file** *path_to_file*                                                                    | Reads the whole specified file, interprets its content as instructions (one per line) and executes parsed instructions. |
| **set factory** *factory_class_name* **-p** *[param1, param2, ...]*                         | Sets factory of message drivers for controller. This command loads class *factory_class_name* (it should be present in classpath) and created its instance passing specified string parameters *param1, param2, ...* to the constructor. |
| **add module** *module_name* **of type** *module_class_name* **-p** *[param1, param2, ...]* | Runs a module represented by class *module_class_name*: loads this class, creates instance using specified optional parameters, checks compatibility with set driver factory and associates created module with name *module_name*. |
| **rm module** *module_name*                                                                 | Stops and removes module associated with name *module_name*. |
| **start** *ip* *port*                                                                       | Starts listening address *ip:port*. *ip* is usually *0.0.0.0* or host ip. |