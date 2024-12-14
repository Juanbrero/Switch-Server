package switchServer;

import actions.Action;
import dbServer.connectors.DBHandler;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Statement;
import java.util.Scanner;

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

            String action = request.getString("action");

            if (action.equals(Action.LISTDBS.toString())) {
                response = listDatabases();

            }
            else if (action.equals(Action.AUTH.toString())) {
                String dbName = request.getString("database");
                String user = request.getString("user");
                String password = request.getString("password");
                response = userAuth(dbName, user, password);
            }
            else if (action.equals(Action.SHOWTABLES.toString())){
                String dbName = request.getString("database");
                response = showTables(dbName);
            }
            else {
                String dbName = request.getString("database");
                String query = request.getString("query");
                response = executeQuery(dbName, query);
            }
        } catch (Exception e) {
            response.put("status", "error");
            response.put("error", e.getMessage());
        }
        return response;
    }

    public JSONObject executeQuery(String dbName, String query) throws Exception {

        JSONObject result = new JSONObject();

        boolean isSelect = query.trim().toUpperCase().startsWith("SELECT");

        if (isSelect) {
            result = SwitchServer.getDbHandler().executeQuery(dbName,query);
        }
        else {
            result = SwitchServer.getDbHandler().executeUpdate(dbName, query);
        }

        return result;
    }

    private JSONObject userAuth(String dbName, String user, String password) throws Exception {

        String response = SwitchServer.getDbHandler().connect(dbName, user, password);

        JSONObject JSONResponse = new JSONObject();
        JSONResponse.put("data", response);
        return JSONResponse;
    }

    private JSONObject listDatabases() throws Exception{

        JSONObject response = null;
        JSONArray dbs = new JSONArray();

        if (!SwitchServer.getDbHandler().getDatabases().isEmpty()) {

            for (String db : SwitchServer.getDbHandler().getDbEngine().keySet()) {
                String engine = SwitchServer.getDbHandler().getDbEngine().get(db).toString();
                dbs.put(engine + " --> " + db);
            }
            response = new JSONObject();
            response.put("databases", dbs);
        }
        else {
            throw new Exception("No databases registered.");
        }

        return response;
    }

    private JSONObject showTables(String dbName) throws Exception {
        return SwitchServer.getDbHandler().getTables(dbName);
    }
}
