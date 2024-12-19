package client.menu.options;

import actions.Action;
import org.json.JSONObject;

public class ShowTables implements Option {

    @Override
    public JSONObject execute() {
        JSONObject execQuery = new JSONObject();
        execQuery.put("action", Action.SHOWTABLES);

        return execQuery;
    }

}
