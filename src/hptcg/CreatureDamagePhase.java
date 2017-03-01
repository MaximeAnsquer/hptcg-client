package hptcg;

import java.awt.*;

public class CreatureDamagePhase extends Card {

    protected CreatureDamagePhase(Game game) {
        super(game);
        realCard = false;
        youPlayedMessage = "";
        opponentPlayedMessage = "";
    }

    @Override
    public boolean canBePlayed() {return false;}

    @Override
    protected void removeAction() {}

    @Override
    public void applyCardEffect() {
        super.applyCardEffect();
        int total = 0;
        for (Component component: game.yourCreaturesPanel.getComponents()) {
            Creature creature = (Creature) component;
            game.damageOpponent(creature.damage);
            total += creature.damage;
        }
        if (total > 0) {
            game.addMessage("Opponent takes " + total + " creature damage.");
        }
        game.refresh();
    }

    @Override
    public void applyOpponentPlayed() {
        super.applyOpponentPlayed();
        int total = 0;
        for (Component component: game.opponentCreaturesPanel.getComponents()) {
            Creature creature = (Creature) component;
            game.takeDamage(creature.damage);
            total += creature.damage;
        }
        if (total > 0) {
            game.addMessage("You take " + total + " creature damage.");
        }
        game.refresh();
    }
}
