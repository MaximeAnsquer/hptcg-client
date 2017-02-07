package hptcg;

public class EndTurn extends Card {

    protected EndTurn(Game game) {
        super(game);
    }

    @Override
    public boolean canBePlayed() {
        return false;
    }

    @Override
    public void applyOpponentPlayed() {
        super.applyOpponentPlayed();
        game.beginYourTurn();
    }
}
