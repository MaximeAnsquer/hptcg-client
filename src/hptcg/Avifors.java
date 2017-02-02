package hptcg;

import static hptcg.LessonType.CARE_OF_MAGICAL_CREATURES;

public class Avifors extends Spell {

    public Avifors(Game game) {
        super(game, "Avifors");
    }

    public void playCard() {
        super.playCard();
        game.getOpponentsLessons().put(CARE_OF_MAGICAL_CREATURES, game.getOpponentsLessons().get(CARE_OF_MAGICAL_CREATURES) -1);
        game.getOpponentsLessonsLabels().get(CARE_OF_MAGICAL_CREATURES).setText(String.valueOf(game.getOpponentsLessons().get(CARE_OF_MAGICAL_CREATURES)));
        if (game.getOpponentsLessons().get(CARE_OF_MAGICAL_CREATURES) == 0 ) {
            game.getOpponentsLessonsLabels().get(CARE_OF_MAGICAL_CREATURES).setVisible(false);
        }
    }

    public void opponentPlayedCard() {

    }

    @Override
    public boolean canBePlayed() {
        return game.getOpponentsLessons().get(CARE_OF_MAGICAL_CREATURES) > 0;
    }
}
