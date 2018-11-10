package top.ninwoo.rpc.service;

public class Client {
    public static void main(String[] args) {
        HelloInterface helloInterface = RpcClient.getRemoteProxyObj(HelloInterface.class);
        String hello = helloInterface.sayHello("hahah");
        System.out.println(hello);
    }
}
