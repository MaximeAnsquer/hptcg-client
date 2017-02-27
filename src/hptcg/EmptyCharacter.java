package hptcg;

public class EmptyCharacter extends Character {

    protected EmptyCharacter(Game game) {
        super(game);
    }

    @Override
    protected void useCharacterPower() {

    }

    @Override
    protected void removeAction() {}

    @Override
    protected boolean powerCanBePlayer() {
        return false;
    }
}
