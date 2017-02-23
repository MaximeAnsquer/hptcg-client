package hptcg;

public class DrawHand extends Card {


    protected DrawHand(Game game) {
        super(game);
        this.realCard = false;
    }

    @Override
    public boolean canBePlayed() {
        return false;
    }

    @Override
    public void playCard() {
        super.playCard();
        String cards = game.get("game/player" + game.yourId + "/popDeck/" + 7);
        for (String cardNbString: cards.split(",")) {
            Card card = game.yourDeck.get(Integer.parseInt(cardNbString));
            game.yourDeck.remove(card);
            game.handPanel.add(card);
        }
        game.refresh();
    }

    @Override
    public void applyOpponentPlayed() {
        super.applyOpponentPlayed();
        String cards = game.get("game/player" + game.opponentId + "/popDeckCopy/" + 7);
        for (String cardNbString: cards.split(",")) {
            Card card = game.opponentDeck.get(Integer.parseInt(cardNbString));
            game.opponentDeck.remove(card);
            game.opponentHandSize++;
        }
        game.refresh();
    }
}
