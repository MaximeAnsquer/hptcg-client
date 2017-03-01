package hptcg;

public class CardEffect extends Card {

    protected CardEffect(Game game, String youPlayedMessage, String opponentPlayedMessage) {
        super(game);
        realCard = false;
        this.youPlayedMessage = youPlayedMessage;
        this.opponentPlayedMessage = opponentPlayedMessage;
    }

    @Override
    public boolean canBePlayed() {
        return false;
    }

    @Override
    protected void removeAction() {

    }
}
