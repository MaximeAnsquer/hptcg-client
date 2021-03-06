package hptcg;

public class EndTurn extends Card {

    protected EndTurn(Game game) {
        super(game);
        this.realCard = false;
        youPlayedMessage = "End of your turn";
        opponentPlayedMessage = "Beginning of your turn";
    }

    @Override
    protected void removeAction() {}

    @Override
    public boolean canBePlayed() {
        return false;
    }

    @Override
    public void applyOpponentPlayed() {
        super.applyOpponentPlayed();
        game.beginYourTurn();
    }

    @Override
    public void applyCardEffect() {
        super.applyCardEffect();
        game.yourTurn = false;
        game.mainMessageLabel.setText("It's your opponent's turn");
        game.refresh();
    }
}
