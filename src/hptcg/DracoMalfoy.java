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

    public static class ActivationEffect extends CardEffect {

        protected ActivationEffect(Game game) {
            super(game, "You use DracoMalfoy's capacity", "Opponent uses Draco Malfoy's capacity");
        }

        @Override
        public boolean canBePlayed() {
            return false;
        }

        @Override
        protected void removeAction() {

        }
    }

}
