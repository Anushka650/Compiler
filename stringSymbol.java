public class stringSymbol {
    private String string;
    private String name;
    public stringSymbol(String n) {
        name = n;
        string = null;
    }
    public stringSymbol(String n, String s) {
        name = n;
        string = s;
    }
    public void setString(String s) {
        string = s;
    }
    public void setName(String n) {
        name = n;
    }
    public String getString() {
        return string;
    }
    public String getName() {
        return name;
    }
}