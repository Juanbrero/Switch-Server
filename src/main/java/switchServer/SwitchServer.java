package switchServer;

import configLoader.ConfigLoader;
import dbServer.connectors.DBHandler;
import java.net.ServerSocket;
import java.net.Socket;

public class SwitchServer {

    private static String ip;
    private static String port;
    private static DBHandler dbHandler;
    private static SwitchServer instance = null;

    private SwitchServer () {
        ip = ConfigLoader.get("switch.ip");
        port = ConfigLoader.get("switch.port");
        dbHandler = new DBHandler();

    }

    public static SwitchServer getInstance() {
        if (SwitchServer.instance == null) {
            SwitchServer.instance = new SwitchServer();
        }
        return SwitchServer.instance;
    }

    public static DBHandler getDbHandler() {
        return dbHandler;
    }

    private static void loadDatabases() {
        dbHandler.registerDatabases();
    }

    public static void main(String[] args) {

        SwitchServer sServer = getInstance();

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
