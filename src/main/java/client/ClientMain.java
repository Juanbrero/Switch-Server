package client;

import configLoader.ConfigLoader;

public class ClientMain {

    private String username;
    private String password;
    private String rol;
    private final String serverIp = ConfigLoader.get("switch.ip");
    private final String serverPort = ConfigLoader.get("switch.port");

    public static void main (String[] args) {

        System.out.println("\nConnecting to server...");



    }
}
