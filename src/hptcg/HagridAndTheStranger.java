package hptcg;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Arrays;

import static hptcg.LessonType.CARE_OF_MAGICAL_CREATURES;
import static hptcg.Type.CREATURE;

public class HagridAndTheStranger extends Spell {

    private String creatureChosen;

    public HagridAndTheStranger(Game game) {
        super(game, 4, CARE_OF_MAGICAL_CREATURES);
    }

    @Override
    public void applyCardEffect() {
        super.applyCardEffect();
        String previousMainMessage = game.mainMessageLabel.getText();
        for (Card card: game.getAllCards()) {
            card.setDisabled(true);
        }
        for(Component card: game.yourDiscardPile.getComponents()) {
            if (((Card) card).type.equals(CREATURE)) {
                card.addMouseListener(new MouseListener() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        creatureChosen = ((Card) card).getCardName();
                        card.removeMouseListener(this);
                        ((Card) card).setDisabled(false);
                        game.yourDiscardPile.remove(card);
                        ((Card) card).setDisabled(false);
                        game.yourHand.add(card);
                        game.put("player" + game.yourId + "/target1", creatureChosen);
                        game.mainMessageLabel.setText(previousMainMessage);
                        game.yourDiscardPileFrame.setVisible(false);
                        for(Component card: game.yourDiscardPile.getComponents()) {
                            card.removeMouseListener(this);
                        }
                        for(Card card: game.getAllCards()) {
                            card.setDisabled(false);
                        }
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
        game.mainMessageLabel.setText("Choose a creature from your discard pile.");
        game.yourDiscardPileFrame.setVisible(true);
        game.yourDiscardPileFrame.pack();
    }

    @Override
    public void applyOpponentPlayed() {
        super.applyOpponentPlayed();
        String previousMainMessage = game.mainMessageLabel.getText();
        game.mainMessageLabel.setText("Opponent is choosing a creature from his discard pile.");
        game.refresh();
        game.put("player" + game.opponentId + "/target1", "");
        game.waitFor(2000);
        (new Thread(() -> {
            String target = game.getOpponentTarget(1);
            game.addMessage("Opponent targeted: \n" + target);
            Card creatureToRemove = (Card) Arrays.stream(game.opponentDiscardPile.getComponents())
                    .filter(card -> card.getClass().getSimpleName().equals(target))
                    .findFirst().get();
            game.opponentDiscardPile.remove(creatureToRemove);
            game.mainMessageLabel.setText(previousMainMessage);
            game.put("player" + game.yourId + "/target1", "");
            game.refresh();
        })).start();
    }

    @Override
    public boolean canBePlayed() {
        boolean atLeastOneCreatureInDiscardPile = Arrays.stream(game.yourDiscardPile.getComponents())
                .filter(component -> ((Card) component).type.equals(CREATURE))
                .count() > 0;
        return atLeastOneCreatureInDiscardPile && super.canBePlayed();
    }
}
