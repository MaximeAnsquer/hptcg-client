package hptcg.lesson;

import hptcg.Card;
import hptcg.Type;

public abstract class Lesson extends Card {

  protected LessonType lessonType;

  protected Lesson() {
    cost = 0;
    type = Type.LESSON;
    text = "";
  }

  public boolean conditionFulfilled() {
    return true;
  }

}
