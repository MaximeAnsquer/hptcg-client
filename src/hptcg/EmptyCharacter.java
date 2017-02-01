package hptcg;

public class EmptyCharacter extends Character {

    protected EmptyCharacter(Game game) {
        super(game, "character");
    }

    @Override
    public boolean conditionFulfilled() {
        return false;
    }
}
