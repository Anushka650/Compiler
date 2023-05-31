public class intSymbol {
    private int number;
    private String name;
    public intSymbol() {
    }
    public intSymbol(String n) {
        name = n;
    }
    public intSymbol(String n, int num) {
        name = n;
        number = num;
    }
    public void setNumber(int num) { number = num; }
    public void setName(String n) { name = n; }
    public int getNumber() {
        return number;
    }
    public String getName() {
        return name;
    }
}