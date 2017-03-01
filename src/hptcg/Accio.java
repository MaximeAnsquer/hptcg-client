package hptcg;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Arrays;

import static hptcg.LessonType.CHARMS;

public class Accio extends Spell {

    private int nbLessonSelected;
    private String lessons = "";

    public Accio(Game game) {
        super(game, 2, CHARMS);
        this.nbLessonSelected = 0;
        this.removeActionAfterPlay = false;
    }

    @Override
    public void applyCardEffect() {
        super.applyCardEffect();
        String previousMainMessage = game.mainMessageLabel.getText();
        for (Card card: game.getAllCards()) {
            card.setDisabled(true);
        }
        for(Component card: game.yourDiscardPile.getComponents()) {
            if (((Card) card).type.equals(Type.LESSON)) {
                card.addMouseListener(new MouseListener() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        lessons += ((Card) card).getCardName() + ",";
                        card.removeMouseListener(this);
                        game.yourDiscardPile.remove(card);
                        game.yourHand.add(card);
                        ((Card) card).setDisabled(false);
                        nbLessonSelected++;
                        game.mainMessageLabel.setText("Choose two lessons from your discard pile (" + nbLessonSelected + "/2)");
                        game.yourDiscardPileFrame.repaint();
                        game.yourDiscardPileFrame.pack();
                        game.refresh();
                        if (nbLessonSelected == 2) {
                            game.put("game/player" + game.yourId + "/target1", lessons);
                            game.mainMessageLabel.setText(previousMainMessage);
                            game.yourDiscardPileFrame.setVisible(false);
                            for(Component card: game.yourDiscardPile.getComponents()) {
                                card.removeMouseListener(this);
                            }
                            for(Card card: game.getAllCards()) {
                                card.setDisabled(false);
                            }
                            game.yourActionsLeft--;
                        }
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
        game.mainMessageLabel.setText("Choose two lessons from your discard pile (0/2)");
        game.yourDiscardPileFrame.setVisible(true);
        game.yourDiscardPileFrame.pack();
    }

    @SuppressWarnings("Duplicates")
    @Override
    public void applyOpponentPlayed() {
        super.applyOpponentPlayed();
        String previousMainMessage = game.mainMessageLabel.getText();
        game.mainMessageLabel.setText("Opponent is choosing lessons from his discard pile.");
        game.refresh();
        game.put("game/player" + game.opponentId + "/target1", "");
        game.waitFor(2000);
        (new Thread(() -> {
            String target = game.getOpponentTarget(1);
            String firstLesson = target.split(",")[0];
            String secondLesson = target.split(",")[1];
            game.addMessage("Opponent targeted: \n" + firstLesson + ", " + secondLesson);
            for (String lesson: new String[]{firstLesson, secondLesson}) {
                Card targetLesson = (Card) Arrays.stream(game.opponentDiscardPile.getComponents())
                        .filter(card -> card.getClass().getSimpleName().equals(lesson))
                        .findFirst().get();
                game.opponentDiscardPile.remove(targetLesson);
                game.opponentHand.add(targetLesson);
            }
            game.mainMessageLabel.setText(previousMainMessage);
            game.put("game/player" + game.yourId + "/target1", "");
            game.refresh();
        })).start();
    }

    @Override
    public boolean canBePlayed() {
        boolean atLeastTwoLessonsInDiscardPile = Arrays.stream(game.yourDiscardPile.getComponents()).filter(component -> {
            Card card = (Card) component;
            return card != null && card.type.equals(Type.LESSON);
        }).count() >= 2;
        return super.canBePlayed() && atLeastTwoLessonsInDiscardPile;
    }
}
