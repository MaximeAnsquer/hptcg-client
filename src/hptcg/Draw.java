package hptcg;

public class Draw extends Card {

    protected Draw(Game game) {
        super(game);
        this.realCard = false;
        youPlayedMessage = "You draw a card";
        opponentPlayedMessage = "Opponent draws a card";
    }

    @Override
    public boolean canBePlayed() {
        return false;
    }

    @Override
    public void applyCardEffect() {
        super.applyCardEffect();
        String cards = game.get("game/player" + game.yourId + "/popDeck/" + 1);
        for (String cardNbString: cards.split(",")) {
            Card card = game.yourDeck.get(Integer.parseInt(cardNbString));
            game.yourDeck.remove(Integer.parseInt(cardNbString));
            game.yourHand.add(card);
        }
        game.refresh();
    }

    @Override
    protected void removeAction() {
        game.yourActionsLeft--;
        game.refresh();
    }

    @SuppressWarnings("Duplicates")
    @Override
    public void applyOpponentPlayed() {
        super.applyOpponentPlayed();
        String cards = game.get("game/player" + game.opponentId + "/popDeckCopy/" + 1);
        for (String cardNbString: cards.split(",")) {
            Card card = game.opponentDeck.get(Integer.parseInt(cardNbString));
            card.setDisabled(true);
            game.opponentDeck.remove(Integer.parseInt(cardNbString));
            game.opponentHand.add(card);
        }
        game.refresh();
    }
}
