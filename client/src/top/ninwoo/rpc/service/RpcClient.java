package top.ninwoo.rpc.service;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.net.Socket;

public class RpcClient {
    public static <T> T getRemoteProxyObj(final Class<?> serviceInterface) {
        InetSocketAddress inetAddress = new InetSocketAddress("127.0.0.1", 8888);
        // 将本地的接口调用转换成JDK代理，在动态代理中实现接口的远程调用
        return (T) Proxy.newProxyInstance(serviceInterface.getClassLoader(),
                new Class<?>[]{serviceInterface}, new DynProxy(serviceInterface, inetAddress));
    }

    public static class DynProxy implements InvocationHandler {
        private final Class<?> serviceInterface;
        private final InetSocketAddress addr;

        public DynProxy(Class<?> serviceInterface, InetSocketAddress addr) {
            this.serviceInterface = serviceInterface;
            this.addr = addr;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                Socket socket = null;
                ObjectOutputStream objectOutputStream = null;
                ObjectInputStream objectInputStream = null;
            try {
                socket = new Socket();
                socket.connect(addr);
                objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                // 发送接口名
                objectOutputStream.writeUTF(serviceInterface.getName());
                // 方法名
                objectOutputStream.writeUTF(method.getName());
                // 参数类型
                objectOutputStream.writeObject(method.getParameterTypes());
                // 参数
                objectOutputStream.writeObject(args);
                objectOutputStream.flush();
                objectInputStream = new ObjectInputStream(socket.getInputStream());
                System.out.println("远程调用成功" + serviceInterface.getName());
                return objectInputStream.readObject();
            } finally {
                socket.close();
                objectInputStream.close();
                objectOutputStream.close();
            }
        }
    }
}
