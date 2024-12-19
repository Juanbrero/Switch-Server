package client.menu.options;

import actions.Action;
import org.json.JSONObject;

import java.util.Scanner;

public class ExecuteQuery implements Option {

    @Override
    public JSONObject execute() {
        Scanner sc = new Scanner(System.in);
        JSONObject execQuery = new JSONObject();

        System.out.println("Write the query below:");
        String query = sc.nextLine();

        execQuery.put("action", Action.EXECQUERY);
        execQuery.put("query", query);

        return execQuery;
    }

}
