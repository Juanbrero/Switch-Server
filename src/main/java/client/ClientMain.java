package client;

import client.menu.Menu;
import client.queryFormatter.JSONQueryFormatter;
import configLoader.ConfigLoader;
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

    private void interactWithServer() {
        boolean running = true;
        String db = selectDatabase();

        while(running) {

            menu.show();

            String op = sc.nextLine();

            menu.setOp(op);

            if (menu.getOp() != null && !op.equals("1")) {

                JSONObject request = menu.getOp().execute(db);
                JSONObject response = sendRequest(request);
                System.out.println(new JSONQueryFormatter().JSONToString(response));
            }
            else if (op.equals("1")) {
                db = selectDatabase();
            }
            else {
                running = false;
                break;
            }

        }

    }

    public String selectDatabase() {

        menu.setOp("1");
        JSONObject response = sendRequest(menu.getOp().execute());
        String db = null;

        if (response != null && response.has("databases")) {

            response.getJSONArray("databases").forEach(database -> System.out.println(database.toString()));
            System.out.println("Select database: ");
            db = sc.nextLine();
        }
        else {
            System.out.println("No databases available.");
        }

        return db;
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

    public static void main (String[] args) {

        ClientMain client = new ClientMain();
        System.out.println("\nConnecting to server...");

        client.interactWithServer();

    }

}
