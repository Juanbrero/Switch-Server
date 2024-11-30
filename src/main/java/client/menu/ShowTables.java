package client.menu;

import org.json.JSONObject;

public class ShowTables implements Option {

    @Override
    public JSONObject execute(String db) {
        JSONObject execQuery = new JSONObject();
        execQuery.put("action", "listTables");
        execQuery.put("database", db);

        return execQuery;
    }
}
