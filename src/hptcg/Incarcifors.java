package hptcg;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import static hptcg.LessonType.TRANSFIGURATION;

public class Incarcifors extends Spell {

    public Incarcifors(Game game) {
        super(game, 6, TRANSFIGURATION);
    }

    @Override
    public void playCard() {
        super.playCard();
        game.mainMessageLabel.setText("Choose target creature.");
        game.refresh();
        for (Card card: game.getAllCards()) {
            card.setDisabled(true);
        }
        for (Component card: game.opponentCreaturesPanel.getComponents()) {
            Card finalCard = (Card) card;
            card.addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    game.opponentDiscardPile.add(finalCard);
                    game.opponentCreaturesPanel.remove(finalCard);
                    for (Card card: game.getAllCards()) {
                        card.setDisabled(false);
                    }
                    game.mainMessageLabel.setText("It's your turn");
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
        //TODO
    }

    @Override
    public boolean canBePlayed() {
        boolean opponentHasACreature = game.opponentCreaturesPanel.getComponents().length > 0;
        return super.canBePlayed() && opponentHasACreature;
    }
}
