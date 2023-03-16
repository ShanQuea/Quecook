package pres.shanque.quecook.util.socket;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ConnectedSocket extends SocketEvent {
    private Socket socket;
    MessageHandler messageHandler;

    public ConnectedSocket(Socket socket, MessageHandler messageHandler) {//构造方法
        this.socket = socket;
        this.messageHandler = messageHandler;
        new Thread(() -> {//lambda表达式创建线程
            messageHandler.onClientConnected(this);
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                while (true) {
                    String message = br.readLine();
                    if (!message.equals("null")) {
                        messageHandler.onMessageReceive(this, message);
                    }
                }
            } catch (IOException e) {
                disConnect();
            }
        }).start();
    }

    public void sendMessage(Object message) {//发送消息
        try {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));//获取输出流
            bw.write(message + "\n");//写入信息
            bw.flush();//刷新流
        } catch (IOException ignored) {
        }
    }

    public void disConnect() {//断开Socket连接
        try {
            socket.close();
        } catch (IOException ignored) {
        }
        messageHandler.onClientDisconnected(this);
    }

    public Socket getSocket() {//获取当前Socekt对象
        if (socket.isConnected()) {
            return this.socket;
        } else {
            throw new RuntimeException("Socket连接断开，无法获取Socket");
        }

    }
}