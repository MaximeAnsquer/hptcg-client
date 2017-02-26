package hptcg;


import javax.swing.*;
import java.awt.*;

public abstract class Lesson extends Card {

    protected LessonType lessonType;

    protected Lesson(Game game) {
        super(game);
        powerNeeded = 0;
        type = Type.LESSON;
        cardText = "";
    }

    public boolean canBePlayed() {
        return true;
    }

    public void applyCardEffect() {
        super.applyCardEffect();
        Image image = imageIcon.getImage();
        Image newImage = image.getScaledInstance(88, 63,  java.awt.Image.SCALE_SMOOTH);
        setIcon(new ImageIcon(newImage));
        game.addLesson(lessonType);
    }

    @Override
    protected void removeAction() {
        game.yourActionsLeft = game.yourActionsLeft - 1;
        game.refresh();
    }

    public void applyOpponentPlayed() {
        super.applyOpponentPlayed();
        Image image = imageIcon.getImage();
        Image newImage = image.getScaledInstance(88, 63,  java.awt.Image.SCALE_SMOOTH);
        setIcon(new ImageIcon(newImage));
        game.addOpponentLesson(lessonType);
        game.refresh();
    }

}
