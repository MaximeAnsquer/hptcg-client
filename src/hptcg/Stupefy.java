package hptcg;

public class Stupefy extends Spell {

    protected Stupefy(Game game) {
        super(game, 6, LessonType.CHARMS);
    }

    @Override
    public void applyCardEffect() {
        super.applyCardEffect();
        game.damageOpponent(5);
    }

    @Override
    public void applyOpponentPlayed() {
        super.applyOpponentPlayed();
        game.takeDamage(5);
    }

    @Override
    public boolean canBePlayed() {
        return super.canBePlayed();
    }
}
