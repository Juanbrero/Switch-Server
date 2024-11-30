package client.queryFormatter;

import org.json.JSONArray;
import org.json.JSONObject;

public class JSONQueryFormatter {

    public String JSONToString (JSONObject json) {
        String s = new String();

        if (json.has("data")) {
            JSONArray rows = json.getJSONArray("data");

            s = "Query result:\n";

            for (int i = 0; i < rows.length(); i++) {
                JSONObject row = rows.getJSONObject(i);
                s = s + "Row " + (i + 1) + ":\n";

                for (String key : row.keySet()) {
                    s = s + "   " + key + ": " + row.get(key) + "\n";
                }

            }

        } else {
            s = "No results found\n";
        }
        return s;
    }

}
