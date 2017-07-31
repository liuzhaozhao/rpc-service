# 描述
rpc-service是一个基于netty的远程方法调用框架，也可单独提供http服务，http服务是通过JSR-311规范配置

# 使用方式
假定接口类IService.java代码为：  
`public String test(int arg);`

接口实现类Service.java代码为：  
`public String test(int arg){`  
&ensp;&ensp;`return "arg="+arg;`  
`}`  

## 开启RPC服务
`RpcServer.get().start(8809, Service.class);`

## RPC客户端调用
初始化：`ServiceFactory.get().init(new String[]{"127.0.0.1:8809"});`  
获取类实例：`IService iService = ServiceFactory.get(IService.class);`  
调用方法：`iService.test(123);`  

## 开启HTTP服务
RPC服务方式用于服务提供方和服务使用方都为java语言时使用，如果服务调用方不为java语言，则服务端往往提供的是http调用方式，以下为开启http服务的方式  
对Service类添加http配置注解，添加后的代码为：  
`@GET @Path("/path")`  
`public String test(@QueryParam("param") int arg){`  
&ensp;&ensp;`return "arg="+arg;`  
`}`  
开启服务：`HttpServer.get().start(8080, Service.class);`  
客户端通过url：http://localhost:8080/path?param=123即可获取返回值，默认返回值为json格式  

# 测试
开启服务测试类：rpc.TestServer.java  
开启客户端测试类：rpc.TestClient.java、rpc.TestClient2.java  

已测试点  
>泛型对象传输（Serializable、json）  
异常客户端抛出，包含服务器端异常、客户端异常  
响应超时异常抛出  
多线程数据正确性检测  
中文编码  
通过测试发现，客户端针对一个服务地址建立多个连接跟一个连接在请求并发上没有区别，所以一个服务地址只需要一个连接  
zookeeper服务管理支持  
正在使用中的客户端连接断线重连，重连时获取不同的连接  

