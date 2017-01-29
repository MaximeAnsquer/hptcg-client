package hptcg;


import javax.swing.*;
import java.awt.*;

public abstract class Lesson extends Card {

  protected LessonType lessonType;

  protected Lesson(Game game, String cardName) {
    super(game, cardName);
    cost = 0;
    type = Type.LESSON;
    text = "";
  }

  public boolean conditionFulfilled() {
    return true;
  }

  public void playCard() {
    super.playCard();
    Image image = imageIcon.getImage();
    Image newImage = image.getScaledInstance(88, 63,  java.awt.Image.SCALE_SMOOTH);
    setIcon(new ImageIcon(newImage));
    game.addLesson(lessonType);
    game.refresh();
    game.endYourTurn(); //TODO: Remove
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
