package top.ninwoo.rpc.register;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RegisterCenter {
    // 线程池
    private static ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    // 保存对象
    private static final HashMap<String, Class> serviceRegistry = new HashMap<String, Class>();

    private int port;

    public RegisterCenter(int port) {
        this.port = port;
    }

    public void start() throws IOException {
        // 创建socket监听
        ServerSocket server = new ServerSocket();
        // 绑定端口
        server.bind(new InetSocketAddress(port));
        System.out.println("注册中心开始监听");
        try {
            while(true) {
                Socket socket = server.accept();
                executorService.execute(new ServiceTask(socket));
            }
        } finally {
            server.close();
        }
    }

    public void register(Class serviceInterface, Class impl) {
        serviceRegistry.put(serviceInterface.getName(), impl);
    }

    private class ServiceTask implements Runnable {
        Socket client = null;

        public ServiceTask(Socket client) {
            this.client = client;
        }

        @Override
        public void run() {
            System.out.println("开始处理注册事务");
            ObjectInputStream objectInputStream = null;
            ObjectOutputStream objectOutputStream = null;
            try {
                objectInputStream = new ObjectInputStream(client.getInputStream());
                // 按顺序获取值
                // 拿到接口名
                String serviceName = objectInputStream.readUTF();
                // 获取方法名
                String methodName = objectInputStream.readUTF();
                // 获取类型
                Class<?>[] paramTypes = (Class<?>[]) objectInputStream.readObject();
                // 获取参数
                Object[] arguments = (Object[]) objectInputStream.readObject();
                // 使用反射调用方法
                Class serviceClass = serviceRegistry.get(serviceName);
                Method method = serviceClass.getMethod(methodName, paramTypes);
                Object result = method.invoke(serviceClass.newInstance(), arguments);
                objectOutputStream = new ObjectOutputStream(client.getOutputStream());
                objectOutputStream.writeObject(result);

                objectInputStream.close();
                objectOutputStream.close();
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }
}
