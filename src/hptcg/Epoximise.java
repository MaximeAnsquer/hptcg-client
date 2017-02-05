package hptcg;

import static hptcg.LessonType.CHARMS;
import static hptcg.LessonType.TRANSFIGURATION;

public class Epoximise extends Spell {

    public Epoximise(Game game) {
        super(game, 2, TRANSFIGURATION);
    }

    public void playCard() {
        super.playCard();
        game.removeLesson(CHARMS, true);
    }

    public void applyOpponentPlayed() {
        super.applyOpponentPlayed();
        game.removeLesson(CHARMS, false);
    }

    @Override
    public boolean canBePlayed() {
        return super.canBePlayed() && game.getOpponentsLessons().get(CHARMS) > 0;
    }
}
