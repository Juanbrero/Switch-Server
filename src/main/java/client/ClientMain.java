package client;

import configLoader.ConfigLoader;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ClientMain {

    private String username;
    private String password;
    private String rol;
    private final String serverIp = ConfigLoader.get("switch.ip");
    private final String serverPort = ConfigLoader.get("switch.port");
    private final Scanner sc = new Scanner(System.in);
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    private ClientMain() {

        try {
            this.socket = new Socket(serverIp, Integer.parseInt(serverPort));
            this.out = new PrintWriter(socket.getOutputStream(),true);
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        }catch (IOException e) {
            e.printStackTrace();
            out.close();
        }

    }

    private void interactWithServer() {
        boolean running = true;
        listDatabases();
        String db;

        System.out.println("Select database (0 to exit): ");
        db = sc.nextLine();
        sc.nextLine();

        if (db.equals("0")) {
            running = false;
        }

        while(running) {

            System.out.println("1. Show tables");
            System.out.println("2. Execute query");
            System.out.println("0. Exit");

            String op = sc.nextLine();
            sc.nextLine();
            JSONObject execQuery = new JSONObject();

            switch (op) {
                case "1":
                    execQuery.put("action", "listTables");
                    execQuery.put("database", db);
                    break;
                case "2":
                    System.out.println("Write the query below:");
                    String query = sc.nextLine();
                    sc.nextLine();
                    execQuery.put("action", "executeQuery");
                    execQuery.put("database", db);
                    execQuery.put("query", query);
                    break;
                default:
                    running = false;
                    break;
            }

            JSONObject response = sendRequest(execQuery);
            printFormattedJsonResult(response);
        }

    }

    public void listDatabases() {

        JSONObject requestJson = new JSONObject();
        requestJson.put("action", "listDatabases");

        JSONObject responseJson = sendRequest(requestJson);

        if (responseJson != null && responseJson.has("databases")) {
            System.out.println("Databases:");
            responseJson.getJSONArray("databases").forEach(db -> System.out.println(db.toString()));
        }
        else {
            System.out.println("No Databases available.");
        }
    }

    public JSONObject sendRequest(JSONObject requestJson) {
        JSONObject responseJson = null;
        StringBuilder responseBuilder = new StringBuilder();

        try {
            out.println(requestJson.toString());
            out.println();
            out.flush();
            System.out.println("Request sent: " + requestJson);

            String line;

            while ((line = in.readLine()) != null && !line.isEmpty()) {
                responseBuilder.append(line);
            }


            if (responseBuilder.length() > 0) {
                responseJson = new JSONObject(responseBuilder.toString());
                System.out.println("Response from server: " + responseJson);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return responseJson;
    }

    public void printFormattedJsonResult(JSONObject resultJson) {

        if (resultJson.has("data")) {
            JSONArray rows = resultJson.getJSONArray("data");

            System.out.println("Query result:");

            for (int i = 0; i < rows.length(); i++) {
                JSONObject row = rows.getJSONObject(i);
                System.out.println("Row " + (i + 1) + ":");

                for (String key : row.keySet()) {
                    System.out.println("   " + key + ": " + row.get(key));
                }
                System.out.println();
            }

        } else {
            System.out.println("No results found");
        }
    }

    public static void main (String[] args) {

        ClientMain client = new ClientMain();
        System.out.println("\nConnecting to server...");

        client.interactWithServer();

    }

}
