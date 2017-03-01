package hptcg;

public class DrawHand extends Card {

    protected DrawHand(Game game) {
        super(game);
        this.realCard = false;
        youPlayedMessage = "You draw 7 cards";
        opponentPlayedMessage = "Opponent draws 7 cards";
    }

    @Override
    protected void removeAction() {}

    @Override
    public boolean canBePlayed() {
        return false;
    }

    @Override
    public void applyCardEffect() {
        super.applyCardEffect();
        String cards = game.get("player" + game.yourId + "/popDeck/" + 7);
        for (String cardNbString: cards.split(",")) {
            Card card = game.yourDeck.get(Integer.parseInt(cardNbString));
            game.yourDeck.remove(Integer.parseInt(cardNbString));
            game.yourHand.add(card);
        }
        game.refresh();
    }

    @SuppressWarnings("Duplicates")
    @Override
    public void applyOpponentPlayed() {
        super.applyOpponentPlayed();
        String cards = game.get("player" + game.opponentId + "/popDeckCopy/" + 7);
        for (String cardNbString: cards.split(",")) {
            Card card = game.opponentDeck.get(Integer.parseInt(cardNbString));
            card.setDisabled(true);
            game.opponentDeck.remove(Integer.parseInt(cardNbString));
            game.opponentHand.add(card);
        }
        game.refresh();
    }
}
