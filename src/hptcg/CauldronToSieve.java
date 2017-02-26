package hptcg;

import static hptcg.LessonType.POTIONS;
import static hptcg.LessonType.TRANSFIGURATION;

public class CauldronToSieve extends Spell {

    public CauldronToSieve(Game game) {
        super(game, 2, TRANSFIGURATION);
    }

    public void applyCardEffect() {
        super.applyCardEffect();
        game.removeLesson(POTIONS, true);
    }

    public void applyOpponentPlayed() {
        super.applyOpponentPlayed();
        game.removeLesson(POTIONS, false);
    }

    @Override
    public boolean canBePlayed() {
        return super.canBePlayed() && game.getOpponentsLessons().get(POTIONS) > 0;
    }
}
