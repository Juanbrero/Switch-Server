package client.menu;

import actions.Action;
import org.json.JSONObject;

public class ShowTables implements Option {

    @Override
    public JSONObject execute(String db) {
        JSONObject execQuery = new JSONObject();
        execQuery.put("action", Action.SHOWTABLES);
        execQuery.put("database", db);

        return execQuery;
    }

    @Override
    public JSONObject execute() {
        /* No Action */
        return null;
    }
}
