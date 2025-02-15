package client.menu.options;

import actions.Action;
import org.json.JSONObject;

import java.util.Scanner;

public class Auth implements Option {

    @Override
    public JSONObject execute() {
        Scanner sc = new Scanner(System.in);

        JSONObject request = new JSONObject();;
        request.put("action", Action.AUTH);

        System.out.println("Username:");
        String user = sc.nextLine();
        System.out.println("Password:");
        String password = sc.nextLine();

        request.put("user", user);
        request.put("password", password);

        return request;
    }

}
