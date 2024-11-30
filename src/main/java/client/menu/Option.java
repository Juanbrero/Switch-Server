package client.menu;

import org.json.JSONObject;

public interface Option {

    public JSONObject execute(String db);

    public JSONObject execute();
}
