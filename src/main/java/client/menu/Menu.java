package client.menu;

public class Menu {

    private Option option;

    public Menu() {
    }

    public Option getOp() {
        return option;
    }

    public void setOp(String op) {
        switch (op) {
            case "1":
                option = new ShowTables();
                break;
            case "2":
                option = new ExecuteQuery();
                break;
            default:
                option = null;
                break;
        }

    }

    public void show() {
        System.out.println("1. Show tables");
        System.out.println("2. Execute query");
        System.out.println("0. Exit");
    }
}