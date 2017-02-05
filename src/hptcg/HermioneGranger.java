package hptcg;

public class HermioneGranger extends Character {

    protected HermioneGranger(Game game) {
        super(game);
    }

    @Override
    public boolean canBePlayed() {
        return true;
    }
}
