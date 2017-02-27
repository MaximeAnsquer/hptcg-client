package hptcg;

public class DracoMalfoy extends Character {

    private DiscardThenMakeDiscard discardThenMakeDiscard;

    protected DracoMalfoy(Game game) {
        super(game);
        removeActionAfterPlay = false;
        discardThenMakeDiscard = new DiscardThenMakeDiscard(game);
    }

    @Override
    protected void useCharacterPower() {
        discardThenMakeDiscard.playCard();
    }

    @Override
    protected boolean powerCanBePlayer() {
        return game.yourHand.getComponents().length > 0;
    }

}
