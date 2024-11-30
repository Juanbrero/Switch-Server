package client.menu;

import actions.Action;
import org.json.JSONObject;

public class ListDBs implements Option {
    @Override
    public JSONObject execute(String db) {

        /* No action*/
        return null;
    }

    @Override
    public JSONObject execute() {
        JSONObject requestJson = new JSONObject();
        requestJson.put("action", Action.LISTDBS);

        return requestJson;

    }
}
