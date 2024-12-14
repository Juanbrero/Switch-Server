package client.menu;

public class Menu {

    private Option option;

    public Menu() {
        setOp("1");
    }

    public Option getOp() {
        return option;
    }

    public void setOp(String op) {
        switch (op) {
            case "1":
                option = new ListDBs();
                break;
            case "2":
                option = new ShowTables();
                break;
            case "3":
                option = new ExecuteQuery();
                break;
            default:
                option = null;
                break;
        }

    }

    public void show() {
        System.out.println("1. Select Database");
        System.out.println("2. Show tables");
        System.out.println("3. Execute query");
        System.out.println("0. Exit");
    }
}
