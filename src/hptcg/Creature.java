package hptcg;

import javax.swing.*;

import java.awt.*;

import static hptcg.LessonType.CARE_OF_MAGICAL_CREATURES;

public class Creature extends Card {

    protected int damage;
    protected int damageTaken;
    protected int health;
    protected int lessonsToDiscard;

    protected Creature(Game game, int powerNeeded, int damage, int health, int lessonsToDiscard) {
        super(game);
        this.type = Type.CREATURE;
        this.damage = damage;
        this.health = health;
        this.lessonsToDiscard = lessonsToDiscard;
        this.powerNeeded = powerNeeded;
        this.powerTypeNeeded = CARE_OF_MAGICAL_CREATURES;
        this.damageTaken = 0;
    }

    public void dealDamage(int n) {
        damageTaken += n;
        if (damageTaken >= health) {
            die();
        }
    }

    private void die() {
        boolean yourCreature = false;
        for (Component c: game.yourCreaturesPanel.getComponents()) {
            Card card = (Card) c;
            if (card == this) {
                yourCreature = true;
            }
        }
        JPanel creaturePanel = yourCreature ? game.yourCreaturesPanel : game.opponentCreaturesPanel;
        JPanel discardPile = yourCreature ? game.yourDiscardPile : game.opponentDiscardPile;
        setDisabled(true);
        discardPile.add(this);
        creaturePanel.remove(this);
        game.refresh();
    }

    @Override
    protected void removeAction() {
        game.yourActionsLeft--;
    }

    @Override
    public void applyCardEffect() {
        super.applyCardEffect();
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
