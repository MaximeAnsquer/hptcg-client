package hptcg;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class DiscardThenMakeDiscard extends Card {

    protected DiscardThenMakeDiscard(Game game) {
        super(game);
        realCard = false;
        youPlayedMessage = "You discard a card";
        opponentPlayedMessage = "Opponent discards a card";
    }

    @Override
    public void applyCardEffect() {
        super.applyCardEffect();
        game.put("player" + game.yourId + "/target1", "");
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
                    game.put("player" + game.yourId + "/target1", card.getCardName());
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
        String target = game.getOpponentTarget(1);
        Card cardToRemove = null;
        System.out.println("Opponent hand: ");
        for (Component component: game.opponentHand.getComponents()) {
            Card card = (Card) component;
            System.out.println(card);
            if (card.getCardName().equals(target)) {
                card.setDisabled(true);
                cardToRemove = card;
                break;
            }
        }
        game.opponentHand.remove(cardToRemove);
        game.opponentDiscardPile.add(cardToRemove);
        game.put("player" + game.opponentId + "/target1", "");
        game.refresh();
    }

    @Override
    public boolean canBePlayed() {return false;}

    @Override
    protected void removeAction() {}
}