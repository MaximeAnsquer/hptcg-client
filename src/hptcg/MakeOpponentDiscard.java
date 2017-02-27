package hptcg;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MakeOpponentDiscard extends Card {

    protected MakeOpponentDiscard(Game game) {
        super(game);
        realCard = false;
    }

    @Override
    public void applyCardEffect() {
        super.applyCardEffect();
        String previousMainMessage = game.mainMessageLabel.getText();
        game.mainMessageLabel.setText("Choose a card in your opponent's hand.");
        for (Card card: game.getAllCards()) {
            card.setWasDisabled(card.isDisabled());
            card.setDisabled(true);
        }
        for (Component component: game.opponentHand.getComponents()) {
            Card card = (Card) component;
            card.addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    game.opponentHandFrame.setVisible(false);
                    for (Card card: game.getAllCards()) {
                        card.setDisabled(card.getWasDisabled());
                    }
                    for (Component component: game.opponentHand.getComponents()) {
                        Card card = (Card) component;
                        card.removeLastMouseListener();
                    }
                    game.put("game/player" + game.yourId + "/target", card.getCardName());
                    card.setDisabled(true);
                    game.opponentHand.remove(card);
                    game.opponentDiscardPile.add(card);
                    game.yourActionsLeft--;
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
        game.opponentHandFrame.repaint();
        game.opponentHandFrame.pack();
        game.opponentHandFrame.setVisible(true);
        game.opponentHandFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                game.opponentHandFrame.removeWindowListener(this);
                for (Component component: game.opponentHand.getComponents()) {
                    Card card = (Card) component;
                    card.removeLastMouseListener();
                }
                game.put("game/player" + game.yourId + "/target", "nothing");
                game.yourActionsLeft--;
                game.mainMessageLabel.setText(previousMainMessage);
                game.refresh();
                super.windowClosed(e);
            }
        });
    }

    @Override
    public void applyOpponentPlayed() {
        super.applyOpponentPlayed();
        String target = game.getOpponentTarget();
        if (!target.equals("nothing")) {
            Card cardToDiscard = null;
            for (Component component: game.yourHand.getComponents()) {
                Card card = (Card) component;
                if (card.getCardName().equals(target)) {
                    card.setDisabled(true);
                    cardToDiscard = card;
                    break;
                }
            }
            game.yourHand.remove(cardToDiscard);
            game.yourDiscardPile.add(cardToDiscard);
            game.put("game/player" + game.opponentId + "/target", "");
            game.refresh();
        }
    }

    @Override
    public boolean canBePlayed() {return false;}

    @Override
    protected void removeAction() {}
}