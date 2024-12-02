package switchServer;

import actions.Action;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler extends Thread {

    private final Socket clientSocket;

    public ClientHandler (Socket clientSocket) {
        this.clientSocket = clientSocket;

    }

    @Override
    public void run() {
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true) )
        {
            while (!clientSocket.isClosed()) {
                // Read client request
                StringBuilder requestBuilder = new StringBuilder();
                String line;

                while ((line = in.readLine()) != null && !line.isEmpty()) {
                    requestBuilder.append(line);
                }

                if (!requestBuilder.isEmpty()) {
                    JSONObject requestJson = new JSONObject(requestBuilder.toString());
                    JSONObject responseJson = handleRequest(requestJson);

                    // Send response
                    out.println(responseJson);
                    out.println();
                    System.out.println("Response sent: " + responseJson);
                } else {
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private JSONObject handleRequest(JSONObject request) {
        JSONObject response = new JSONObject();

        try {
            // Extraer base de datos
            String action = request.getString("action");
            if (action.equals(Action.LISTDBS.toString())) {
                response = listDatabases();
            } else {
                String dbName = request.getString("database");
                if (firebirdConnector.getDatabaseUrls().containsKey(dbName)) {
                    response = requestToFirebird(dbName, request);
                } else if (postgreSQLConnector.getDatabaseUrls().containsKey(dbName)) {
                    response = requestToPostgreSQL(dbName, request);
                } else System.err.println("Base de datos no registrada: " + dbName);
            }
        } catch (Exception e) {
            response.put("status", "error");
            response.put("error", e.getMessage());
        }
        return response;
    }
}
