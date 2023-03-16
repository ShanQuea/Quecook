package pres.shanque.quecook.util.socket;

public interface MessageHandler {
    void onMessageReceive(SocketEvent e, String message);
    default void onClientConnected(SocketEvent e) { }
    default void onClientDisconnected(SocketEvent e) { }
}
