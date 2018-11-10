package top.ninwoo.rpc;

import top.ninwoo.rpc.register.RegisterCenter;
import top.ninwoo.rpc.service.HelloImpl;
import top.ninwoo.rpc.service.HelloInterface;

import java.io.IOException;

/**
 * RPC服务端
 */
public class Server {
    public static void main(String[] args) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                RegisterCenter  registerCenter = new RegisterCenter(8888);
                registerCenter.register(HelloInterface.class, HelloImpl.class);
                try {
                    registerCenter.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
