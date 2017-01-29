package hptcg;

public interface ICard {

  /** Condition of the type: " Play this card only if... "
   * @return Whether the card can be played or its condition is not fulfilled.
   */
  boolean conditionFulfilled();

  void playCard();
}
