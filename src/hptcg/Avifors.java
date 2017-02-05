package hptcg;

import static hptcg.LessonType.CARE_OF_MAGICAL_CREATURES;
import static hptcg.LessonType.TRANSFIGURATION;

public class Avifors extends Spell {

    public Avifors(Game game) {
        super(game, 2, TRANSFIGURATION);
    }

    public void playCard() {
        super.playCard();
        game.removeLesson(CARE_OF_MAGICAL_CREATURES, true);
    }

    public void applyOpponentPlayed() {
        super.applyOpponentPlayed();
        game.removeLesson(CARE_OF_MAGICAL_CREATURES, false);
    }

    @Override
    public boolean canBePlayed() {
        return super.canBePlayed() && game.getOpponentsLessons().get(CARE_OF_MAGICAL_CREATURES) > 0;
    }
}
