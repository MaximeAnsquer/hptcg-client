package hptcg;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Arrays;
import java.util.Optional;

import static hptcg.LessonType.TRANSFIGURATION;

public class TakeRoot extends Spell {

    public TakeRoot(Game game) {
        super(game, 2, TRANSFIGURATION); //TODO
    }

    public void playCard() {
        super.playCard();
        game.put("game/player" + game.opponentId + "/target", "");
        game.mainMessageLabel.setText("Your opponent is choosing a creature to discard.");
        game.refresh();
//        String target = cardChosenByOpponent();
        boolean waiting = true;
        String target = null;
        game.mainMessageLabel.setText("Your opponent is choosing a creature to discard.");
        game.refresh();
        while (waiting) {
            target = game.get("game/player" + game.opponentId + "/target");
            if (target != null && !target.equals("")) {
                waiting = false;
            }
            game.waitFor(1000);
        }
        String finalTarget = target;
        Optional<Component> creatureToRemove = Arrays.stream(game.opponentCreaturesPanel.getComponents())
                .filter(card -> card.getClass().getSimpleName().equals(finalTarget))
                .findFirst();
        Card cardCreatureToRemove = (Card) creatureToRemove.get();
        game.addMessage("Your opponent targeted: " + cardCreatureToRemove.getCardName());
        cardCreatureToRemove.setDisabled(true);
        game.opponentDiscardPile.add(cardCreatureToRemove);
        game.opponentCreaturesPanel.remove(cardCreatureToRemove);
    }

    public void applyOpponentPlayed() {
        super.applyOpponentPlayed();
        game.mainMessageLabel.setText("Choose one of your creatures to discard.");
        game.refresh();
        game.put("game/player" + game.yourId + "/target", "");
        for (Card card: game.getAllCards()) {
            card.setDisabled(true);
        }
        for (Component card: game.yourCreaturesPanel.getComponents()) {
            Card finalCard = (Card) card;
            finalCard.addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    for (Component card: game.yourCreaturesPanel.getComponents()) {
                        MouseListener[] mouseListeners = card.getMouseListeners();
                        for (MouseListener mouseListener : mouseListeners) {
                            card.removeMouseListener(mouseListener);
                        }
                    }
                    String target = finalCard.getCardName();
                    game.put("game/player" + game.yourId + "/target", target);
                    finalCard.setDisabled(true);
                    game.yourDiscardPile.add(finalCard);
                    game.yourCreaturesPanel.remove(finalCard);
                    for (Card card: game.getAllCards()) {
                        card.setDisabled(false);
                    }
                    game.addMessage("You discarded: " + target);
                }
                @Override
                public void mousePressed(MouseEvent e) {}
                @Override
                public void mouseReleased(MouseEvent e) {}
                @Override
                public void mouseEntered(MouseEvent e) {}
                @Override
                public void mouseExited(MouseEvent e) {}
            });
        }
    }

    @Override
    public boolean canBePlayed() {
        boolean opponentHasACreature = game.opponentCreaturesPanel.getComponents().length > 0;
        return super.canBePlayed() && opponentHasACreature;
    }
}
