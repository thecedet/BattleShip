package Server.Map;

public enum Symbol {
    UNKNOW(" "),
    HIT("X"),
    SHIP("o"),
    OCEAN("~");

    private final String symbol;

    private Symbol(String symbol)  {
        this.symbol = symbol;
    }

    @Override
    public String toString() {
        return this.symbol;
    }



}
