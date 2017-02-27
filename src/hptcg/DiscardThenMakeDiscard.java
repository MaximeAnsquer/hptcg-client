package hptcg;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class DiscardThenMakeDiscard extends Card {

    protected DiscardThenMakeDiscard(Game game) {
        super(game);
        realCard = false;
    }

    @Override
    public void applyCardEffect() {
        super.applyCardEffect();
        game.put("game/player" + game.yourId + "/target", "");
        String previousMainMessage = game.mainMessageLabel.getText();
        game.mainMessageLabel.setText("Choose a card to discard.");
        for (Card card: game.getAllCards()) {
            card.setWasDisabled(card.isDisabled());
            card.setDisabled(true);
        }
        for (Component component: game.yourHand.getComponents()) {
            Card card = (Card) component;
            card.addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    game.put("game/player" + game.yourId + "/target", card.getCardName());
                    for (Component component: game.yourHand.getComponents()) {
                        ((Card) component).removeLastMouseListener();
                    }
                    for (Card card: game.getAllCards()) {
                        card.setDisabled(card.getWasDisabled());
                    }
                    card.setDisabled(true);
                    game.yourHand.remove(card);
                    game.yourDiscardPile.add(card);
                    game.mainMessageLabel.setText(previousMainMessage);
                    game.refresh();
                    new MakeOpponentDiscard(game).playCard();
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
    public void applyOpponentPlayed() {
        super.applyOpponentPlayed();
        String target = game.getOpponentTarget();
        Card cardToRemove = null;
        for (Component component: game.opponentHand.getComponents()) {
            Card card = (Card) component;
            if (card.getCardName().equals(target)) {
                card.setDisabled(true);
                cardToRemove = card;
                break;
            }
        }
        game.opponentHand.remove(cardToRemove);
        game.opponentDiscardPile.add(cardToRemove);
        game.put("game/player" + game.opponentId + "/target", "");
        game.refresh();
    }

    @Override
    public boolean canBePlayed() {return false;}

    @Override
    protected void removeAction() {}
}