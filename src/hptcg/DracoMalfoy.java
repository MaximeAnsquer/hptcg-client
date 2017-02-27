package hptcg;

public class DracoMalfoy extends Character {

    protected DracoMalfoy(Game game) {
        super(game);
    }

    @Override
    protected void useCharacterPower() {

    }

    @Override
    protected boolean powerCanBePlayer() {
        return false;
    }
}
