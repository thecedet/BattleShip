package Server.Map;

public class Piece implements IPiece{
    
    protected Symbol symbol;

    public Piece(Symbol symbol) {
        this.setSymbol(symbol);
    }

    
    public interface Callback {
        public void hit(Symbol symbol, String message);
    }

    public Symbol getSymbol() {
        return this.symbol;
    }

    public void setSymbol(Symbol symbol) {
        this.symbol = symbol;
    }

    public void target(Callback callback) {
        callback.hit(this.symbol, "Plouf !");        
    } 

}
