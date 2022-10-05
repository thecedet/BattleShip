package Server.Map;


public class Ship extends Piece{
    
    private int life;
    private final int size;
    private final String name;

    public Ship(String name, int size) {
        super(Symbol.SHIP);
        this.size = size;
        this.life = size;
        this.name = name;
    }

    @Override
    public void target(Callback callback) {
        this.life--;
        callback.hit(this.symbol, this.life == 0 ? "coulé" : "touché");
    }

    public int getSize() {
        return this.size;
    }
    public int getLife() {
        return this.life;
    }
    public String getName() {
        return this.name;
    }

}
