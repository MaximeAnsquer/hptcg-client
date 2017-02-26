package hptcg;

public class EmptyCharacter extends Character {

    protected EmptyCharacter(Game game) {
        super(game);
    }

    @Override
    protected void removeAction() {}

    @Override
    public boolean canBePlayed() {
        return false;
    }
}
