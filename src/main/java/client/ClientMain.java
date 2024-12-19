package client;

import actions.Action;
import client.menu.Menu;
import client.queryFormatter.JSONQueryFormatter;
import configLoader.ConfigLoader;
import org.json.JSONObject;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ClientMain {

    private String username;
    private String password;
    private final String serverIp = ConfigLoader.get("switch.ip");
    private final String serverPort = ConfigLoader.get("switch.port");
    private final Scanner sc = new Scanner(System.in);
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private Menu menu = new Menu();

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

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    private void interactWithServer() throws Exception {

        String db = selectDatabase();

        while(true) {

            menu.show();

            int op = sc.nextInt();
            sc.nextLine();

            menu.setOption(op);

            if (op != 0 && op != 1) {

                JSONObject request = menu.getOp().execute();
                request.put("database", db);
                JSONObject response = sendRequest(request);
                System.out.println(new JSONQueryFormatter().JSONToString(response));
            }
            else if (op == 1) {
                db = selectDatabase();

            }
            else {
                break;
            }

        }

    }

    public String selectDatabase() {

        JSONObject response = sendRequest(menu.getOp().execute());
        String db = null;

        if (response != null && response.has("databases")) {

            response.getJSONArray("databases").forEach(database -> System.out.println(database.toString()));

            System.out.println("Select database: ");
            db = sc.nextLine();
            userAuth(db);
        }
        else {
            System.out.println("No databases available.");
        }


        return db;
    }

    private void userAuth(String dbName) {

        try {
            menu.setOption(4);
        }catch (Exception e) {
            e.printStackTrace();
        }

        JSONObject request = new JSONObject();;
        request.put("action", Action.AUTH);
        request.put("database", dbName);

        Scanner sc = new Scanner(System.in);

        System.out.println("Username:");
        String user = sc.nextLine();
        System.out.println("Password:");
        String password = sc.nextLine();

        request.put("user", user);
        request.put("password", password);
        sendRequest(request);

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

    public static void main (String[] args) throws Exception {

        ClientMain client = new ClientMain();
        System.out.println("\nConnecting to server...");

        client.interactWithServer();

    }

}
