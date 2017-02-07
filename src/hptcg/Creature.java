package hptcg;

import javax.swing.*;

import static hptcg.LessonType.CARE_OF_MAGICAL_CREATURES;

public class Creature extends Card {

    protected int damage;
    protected int health;
    protected int lessonsToDiscard;

    protected Creature(Game game, int powerNeeded, int damage, int health, int lessonsToDiscard) {
        super(game);
        this.damage = damage;
        this.health = health;
        this.lessonsToDiscard = lessonsToDiscard;
        this.powerNeeded = powerNeeded;
        this.powerTypeNeeded = CARE_OF_MAGICAL_CREATURES;
    }

    @Override
    public void playCard() {
        super.playCard();
        for (int i=0; i<lessonsToDiscard; i++) {
            game.removeLesson(CARE_OF_MAGICAL_CREATURES, false);
        }
        this.setBorder(BorderFactory.createEmptyBorder(0, 7, 0, 7));
        game.yourCreaturesPanel.add(this);
        game.refresh();
    }

    @Override
    public void applyOpponentPlayed() {
        super.applyOpponentPlayed();
        for (int i=0; i<lessonsToDiscard; i++) {
            game.removeLesson(CARE_OF_MAGICAL_CREATURES, true);
        }
        this.setBorder(BorderFactory.createEmptyBorder(0, 7, 0, 7));
        game.opponentCreaturesPanel.add(this);
    }

    @Override
    public boolean canBePlayed() {
        boolean hasEnoughLessonToDiscard = game.yourLessons.get(CARE_OF_MAGICAL_CREATURES) >= lessonsToDiscard;
        boolean hasPowerTypeNeeded = game.totalPower.get(powerTypeNeeded) > 0;
        int totalPower = 0;
        for (LessonType lessonType: game.totalPower.keySet()) {
            totalPower += game.totalPower.get(lessonType);
        }
        return hasPowerTypeNeeded && hasEnoughLessonToDiscard && totalPower >= powerNeeded;
    }
}
