package client.menu;

import client.menu.options.*;

public class Menu {

    private Option option;

    public Menu() {
        option = new ListDBs();
    }

    public Option getOp() {
        return option;
    }

    public void setOption(int op) throws Exception{

        if (op != 0) {
            option = (Option) Class.forName("client.menu.options." + MenuOptions.values()[op].toString()).getDeclaredConstructor().newInstance();

        }
        else {
            option = null;
        }

//        switch (op) {
//            case "1":
//                option = new ListDBs();
//                break;
//            case "2":
//                option = new ShowTables();
//                break;
//            case "3":
//                option = new ExecuteQuery();
//                break;
//            default:
//                option = null;
//                break;
//        }

    }

    public void show() {
        System.out.println("1. Select Database");
        System.out.println("2. Show tables");
        System.out.println("3. Execute query");
        System.out.println("0. Exit");
    }
}
