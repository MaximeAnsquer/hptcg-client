package hptcg;

public class HermioneGranger extends Character {

    protected HermioneGranger(Game game) {
        super(game, "HermioneGranger");
    }

    @Override
    public boolean canBePlayed() {
        return true;
    }
}
