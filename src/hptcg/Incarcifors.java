package hptcg;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Arrays;
import java.util.Optional;

import static hptcg.LessonType.TRANSFIGURATION;

public class Incarcifors extends Spell {

    public Incarcifors(Game game) {
        super(game, 2, TRANSFIGURATION);
        removeActionAfterPlay = false;
    }

    @Override
    public void applyCardEffect() {
        super.applyCardEffect();
        game.put("player" + game.yourId + "/target1", "");
        game.mainMessageLabel.setText("Choose target creature.");
        game.refresh();
        for (Card card: game.getAllCards()) {
            card.setDisabled(true);
        }
        for (Component card: game.opponentCreaturesPanel.getComponents()) {
            Card finalCard = (Card) card;
            finalCard.addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    for (Component card: game.opponentCreaturesPanel.getComponents()) {
                        MouseListener[] mouseListeners = card.getMouseListeners();
                        for (MouseListener mouseListener : mouseListeners) {
                            card.removeMouseListener(mouseListener);
                        }
                    }
                    String target = finalCard.getCardName();
                    game.put("player" + game.yourId + "/target1", target);
                    finalCard.setDisabled(true);
                    game.opponentDiscardPile.add(finalCard);
                    game.opponentCreaturesPanel.remove(finalCard);
                    for (Card card: game.getAllCards()) {
                        card.setDisabled(false);
                    }
                    game.addMessage("You targeted: " + target);
                    game.mainMessageLabel.setText("It's your turn");
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
        //TODO: Pas terrible, vire la première créature qui a le nom ciblé, mais pas forcément celle qui a été ciblée (s'il il y en plusieurs avec le même nom).
        Optional<Component> creatureToRemove = Arrays.stream(game.yourCreaturesPanel.getComponents())
                .filter(card -> card.getClass().getSimpleName().equals(target))
                .findFirst();
        Card cardCreatureToRemove = (Card) creatureToRemove.get();
        game.addMessage("Opponent targeted: " + cardCreatureToRemove.getCardName());
        cardCreatureToRemove.setDisabled(true);
        game.yourDiscardPile.add(cardCreatureToRemove);
        game.yourCreaturesPanel.remove(cardCreatureToRemove);
    }

    @Override
    public boolean canBePlayed() {
        boolean opponentHasACreature = game.opponentCreaturesPanel.getComponents().length > 0;
        return super.canBePlayed() && opponentHasACreature;
    }
}
