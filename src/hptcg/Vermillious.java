package hptcg;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class Vermillious extends Spell {

    protected Vermillious(Game game) {
        super(game, 4, LessonType.CHARMS);
        removeActionAfterPlay = false;
    }

    @Override
    public void applyCardEffect() {
        super.applyCardEffect();
        String previousMainMessage = game.mainMessageLabel.getText();
        game.mainMessageLabel.setText("Please choose a target.");
        game.put("player" + game.yourId + "/target1", "");
        List<Card> possibleTargets = new ArrayList<>();
        possibleTargets.add(game.opponentStartingCharacter);
        possibleTargets.addAll(game.getOpponentCreatures());
        for (Card card: game.getAllCards()) {
            card.setWasDisabled(card.isDisabled());
            card.setDisabled(true);
        }
        for (Card card: possibleTargets) {
            card.addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    for (Card c: game.getAllCards()) {
                        c.removeMouseListener(this);
                        c.setDisabled(c.getWasDisabled());
                    }
                    game.put("player" + game.yourId + "/target1", card.getCardName());
                    game.addMessage("You targeted: " + card.getCardName());
                    if (card == game.opponentStartingCharacter) {
                        game.damageOpponent(3);
                    } else {
                        Creature targetCreature = (Creature) card;
                        targetCreature.dealDamage(3);
                    }
                    game.mainMessageLabel.setText(previousMainMessage);
                    removeAction();
                    game.refresh();
                }
                @Override
                public void mousePressed(MouseEvent e) {

                }
                @Override
                public void mouseReleased(MouseEvent e) {

                }
                @Override
                public void mouseEntered(MouseEvent e) {

                }
                @Override
                public void mouseExited(MouseEvent e) {

                }
            });
        }
    }

    @Override
    public void applyOpponentPlayed() {
        super.applyOpponentPlayed();
        String target = game.getOpponentTarget(1);
        if (target.equals(game.yourStartingCharacter.getCardName())) {
            game.addMessage("Opponent targeted you.");
            game.takeDamage(3);
        } else {
            //TODO: Pas terrible, vire la première créature qui a le nom ciblé, mais pas forcément celle qui a été ciblée (s'il il y en plusieurs avec le même nom).
            Optional<Component> component = Arrays.stream(game.yourCreaturesPanel.getComponents())
                    .filter(card -> card.getClass().getSimpleName().equals(target))
                    .findFirst();
            Creature creature = (Creature) component.get();
            game.addMessage("Opponent targeted: " + creature.getCardName());
            creature.dealDamage(3);
        }
    }

    @Override
    public boolean canBePlayed() {
        return super.canBePlayed();
    }
}
