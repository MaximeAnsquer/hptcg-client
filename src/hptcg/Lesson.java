package hptcg;


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
    game.get("game/player" + game.getPlayerId() + "/play/" + cardName);
    game.addMessage("You played: " + cardName);
    System.out.println("You played :" + cardName);
    game.removeFromHand(this);
    game.addToPlayedCards(this);
    removeMouseListener(this.getMouseListeners()[0]);
    game.refresh();
    game.endYourTurn(); //TODO: Remove
  }

}
