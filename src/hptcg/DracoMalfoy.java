package hptcg;

public class DracoMalfoy extends Character {

    protected DracoMalfoy(Game game) {
        super(game, "DracoMalfoy");
    }

    @Override
    public boolean canBePlayed() {
        return true;
    }
}
