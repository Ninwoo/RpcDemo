package top.ninwoo.rpc.service;

public class HelloImpl implements HelloInterface {
    @Override
    public String sayHello(String name) {
        return "Hello " + name;
    }
}
