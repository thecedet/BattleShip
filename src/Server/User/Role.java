package Server.User;

public enum Role {
    NEW("nouveau"), PLAYER("joueur"), SPECTATOR("spectateur");

    private final String name;

    Role(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
