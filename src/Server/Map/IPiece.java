package Server.Map;

import Server.Map.Piece.Callback;

public interface IPiece {
    public void target(Callback callback);
    public Symbol getSymbol();
    public void setSymbol(Symbol symbol);
}

