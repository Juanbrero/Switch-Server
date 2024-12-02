package switchServer;

import configLoader.ConfigLoader;
import dbServer.connectors.DBHandler;

import java.net.ServerSocket;
import java.net.Socket;

public class SwitchServer {

    private static String ip = ConfigLoader.get("switch.ip");
    private static String port = ConfigLoader.get("switch.port");
    private static DBHandler dbHandler = new DBHandler();

    private static void loadDatabases() {
        dbHandler.registerDatabases();
    }

    public static void main(String[] args) {

//        ConfigLoader.getKeys(ConfigLoader.get("dbConfigFile.path")).forEach(System.out::println);
        loadDatabases();

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
