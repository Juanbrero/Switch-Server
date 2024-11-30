package client.menu;

import actions.Action;
import org.json.JSONObject;

import java.util.Scanner;

public class ExecuteQuery implements Option {

    @Override
    public JSONObject execute(String db) {
        Scanner sc = new Scanner(System.in);
        JSONObject execQuery = new JSONObject();

        System.out.println("Write the query below:");
        String query = sc.nextLine();

        execQuery.put("action", Action.EXECQUERY);
        execQuery.put("database", db);
        execQuery.put("query", query);

        return execQuery;
    }

    @Override
    public JSONObject execute() {
        /* No action */
        return null;
    }
}
