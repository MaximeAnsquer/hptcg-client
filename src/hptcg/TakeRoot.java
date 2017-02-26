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

    public void applyCardEffect() {
        super.applyCardEffect();
        game.put("game/player" + game.opponentId + "/target", "");
        String previousMainMessage = game.mainMessageLabel.getText();
        game.mainMessageLabel.setText("Your opponent is choosing a creature to discard.");
        game.refresh();
        (new Thread(() -> {
            String finalTarget = game.getOpponentTarget();
            Optional<Component> creatureToRemove = Arrays.stream(game.opponentCreaturesPanel.getComponents())
                    .filter(card -> card.getClass().getSimpleName().equals(finalTarget))
                    .findFirst();
            Card cardCreatureToRemove = (Card) creatureToRemove.get();
            game.addMessage("Your opponent targeted: " + cardCreatureToRemove.getCardName());
            cardCreatureToRemove.setDisabled(true);
            game.opponentDiscardPile.add(cardCreatureToRemove);
            game.opponentCreaturesPanel.remove(cardCreatureToRemove);
            game.mainMessageLabel.setText(previousMainMessage);
            game.refresh();
        })).start();
    }

    public void applyOpponentPlayed() {
        super.applyOpponentPlayed();
        String previousMainMessage = game.mainMessageLabel.getText();
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
                    game.mainMessageLabel.setText(previousMainMessage);
                    game.refresh();
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
