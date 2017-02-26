package hptcg;

public class MagicalMishap extends Spell {

    protected MagicalMishap(Game game) {
        super(game, 2, LessonType.CHARMS);
    }

    @Override
    public void applyCardEffect() {
        super.applyCardEffect();
        game.damageOpponent(3);
    }

    @Override
    public void applyOpponentPlayed() {
        super.applyOpponentPlayed();
        game.takeDamage(3);
    }

    @Override
    public boolean canBePlayed() {
        return super.canBePlayed();
    }
}
