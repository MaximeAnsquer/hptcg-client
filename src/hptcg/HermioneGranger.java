package hptcg;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class HermioneGranger extends Character {

    private int nbLessonsSelected;

    protected HermioneGranger(Game game) {
        super(game);
    }

    @Override
    protected void useCharacterPower() {
        nbLessonsSelected = 0;
        String previousMainMessage = game.mainMessageLabel.getText();
        game.mainMessageLabel.setText("Choose 2 lessons (0/2)");
        for (Card card: game.getAllCards()) {
            card.setWasDisabled(card.isDisabled());
            card.setDisabled(true);
        }
        List<Component> lessons = Arrays.stream(game.handPanel.getComponents()).filter(component -> {
            Card card = (Card) component;
            return card.type.equals(Type.LESSON);
        }).collect(Collectors.toList());
        for (Component component: lessons) {
            Lesson lesson = (Lesson) component;
            lesson.addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (nbLessonsSelected == 0) {
                        game.mainMessageLabel.setText("Choose 2 lessons (1/2)");
                        game.yourActionsLeft++;
                        lesson.playCard();
                    } else {
                        for (Component component: lessons) {
                            Lesson lesson = (Lesson) component;
                            System.out.println("length before: " + lesson.getMouseListeners().length);
                            lesson.removeMouseListener(lesson.getMouseListeners()[lesson.getMouseListeners().length - 1]);
                            System.out.println("length after: " + lesson.getMouseListeners().length);
                        }
                        for (Card card: game.getAllCards()) {
                            card.setDisabled(card.getWasDisabled());
                        }
                        game.mainMessageLabel.setText(previousMainMessage);
                        lesson.playCard();
                    }
                    nbLessonsSelected++;
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
    protected boolean powerCanBePlayer() {
        int nbLessonsInPlay = 0;
        for (LessonType lessonType: game.yourLessons.keySet()) {
            nbLessonsInPlay += game.yourLessons.get(lessonType);
        }
        return nbLessonsInPlay >= 2;
    }
}
