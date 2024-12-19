package client.menu.options;

import actions.Action;
import org.json.JSONObject;

public class ListDBs implements Option {

    @Override
    public JSONObject execute() {
        JSONObject requestJson = new JSONObject();
        requestJson.put("action", Action.LISTDBS);

        return requestJson;

    }
}
