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
    }

    @Override
    public void playCard() {
        super.playCard();
        String previousMainMessage = game.mainMessageLabel.getText();
        for (Card card: game.getAllCards()) {
            card.setDisabled(true);
        }
        for(Component card: game.yourDiscardPile.getComponents()) {
            card.addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    lessons += ((Card) card).getCardName() + ",";
                    card.removeMouseListener(this);
                    game.yourDiscardPile.remove(card);
                    game.handPanel.add(card);
                    ((Card) card).setDisabled(false);
                    nbLessonSelected++;
                    game.mainMessageLabel.setText("Choose two lessons from your discard pile (" + nbLessonSelected + "/2)");
                    game.yourDiscardPileFrame.repaint();
                    game.yourDiscardPileFrame.pack();
                    game.refresh();
                    if (nbLessonSelected == 2) {
                        game.put("game/player" + game.yourId + "/target", lessons);
                        game.mainMessageLabel.setText(previousMainMessage);
                        game.yourDiscardPileFrame.setVisible(false);
                        for(Component card: game.yourDiscardPile.getComponents()) {
                            card.removeMouseListener(this);
                        }
                        for(Card card: game.getAllCards()) {
                            card.setDisabled(false);
                        }
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
        game.mainMessageLabel.setText("Choose two lessons from your discard pile (0/2)");
        game.yourDiscardPileFrame.setVisible(true);
        game.yourDiscardPileFrame.pack();
    }

    @SuppressWarnings("Duplicates")
    @Override
    public void applyOpponentPlayed() {
        super.applyOpponentPlayed();
        String previousMainMessage = game.mainMessageLabel.getText();
        game.mainMessageLabel.setText("Your opponent is choosing lessons from his discard pile.");
        game.refresh();
        game.put("game/player" + game.opponentId + "/target", "");
        game.waitFor(2000);
        (new Thread(() -> {
            String target = null;
            boolean waiting = true;
            while (waiting) {
                target = game.get("game/player" + game.opponentId + "/target");
                if (target != null && !target.equals("")) {
                    game.refresh();
                    waiting = false;
                }
                game.waitFor(2000);
            }
            String firstLesson = target.split(",")[0];
            String secondLesson = target.split(",")[1];
            game.addMessage("Your opponent targeted: \n" + firstLesson + ", " + secondLesson);
            for (String lesson: new String[]{firstLesson, secondLesson}) {
                Card lessonToRemove = (Card) Arrays.stream(game.opponentDiscardPile.getComponents())
                        .filter(card -> card.getClass().getSimpleName().equals(lesson))
                        .findFirst().get();
                game.opponentDiscardPile.remove(lessonToRemove);
                game.opponentHandSize++;
            }
            game.mainMessageLabel.setText(previousMainMessage);
            game.put("game/player" + game.yourId + "/target", "");
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
