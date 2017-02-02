package hptcg;

public abstract class Spell extends Card {

    protected Spell(Game game, String cardName) {
        super(game, cardName);
    }

    public void playCard() {
        super.playCard();
        //TODO: discard
    }
}
