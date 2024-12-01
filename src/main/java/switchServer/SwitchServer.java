package switchServer;

import configLoader.ConfigLoader;
import dbServer.SQLHandler;
import dbServer.engines.Engine;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class SwitchServer {

    private static String ip = ConfigLoader.get("switch.ip");
    private static String port = ConfigLoader.get("switch.port");
    private Map<Engine, SQLHandler> engines = new HashMap<>();

    private static void registerDatabases() {
        String conn
    }

    public static void main(String[] args) {

        registerDatabases();

        try (ServerSocket svSocket = new ServerSocket(Integer.parseInt(port))){
            System.out.println("Switch server running on " + ip + ":" + port);

            while(true) {
                Socket clientSocket = svSocket.accept();
                System.out.println("Connection accepted from: " + clientSocket.getInetAddress() + ":" + clientSocket.getPort());

                new ClientHandler(clientSocket).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
