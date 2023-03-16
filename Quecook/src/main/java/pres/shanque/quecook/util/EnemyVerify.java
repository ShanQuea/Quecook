package pres.shanque.quecook.util;

import org.bukkit.Bukkit;
import pres.shanque.quecook.Quecook;
import pres.shanque.quecook.util.socket.ConnectedSocket;
import pres.shanque.quecook.util.socket.SocketEvent;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;

public class EnemyVerify {
    public static void cheking(){
        Socket socket = null;
        try{
            socket = new Socket("0.0.0.0",500002);
        } catch (IOException e) {
            Bukkit.getPluginManager().disablePlugin(Quecook.getInstance());
            e.printStackTrace();
        }
        ConnectedSocket cs = new ConnectedSocket(){
            public void onMessageReceive(SocketEvent e,Object message) {
                try{
                    if (!message.toString().equalsIgnoreCase("山鹊网络验证系统连接成功!")){
                        Class<?> clazz = NetClass;
                        Object obj1;
                        System.out.println("插件加载中");
                        obj1 = clazz.newInstance();
                        Method method = clazz.getDeclaredMethod("initPlugin");
                        method.setAccessible(false);
                        method.invoke(obj1);
                    }
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException)
            }
        }
    }
}
