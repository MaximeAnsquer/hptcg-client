package hptcg;

public class DrawHand extends Card {

    protected DrawHand(Game game) {
        super(game);
        this.realCard = false;
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
        String cards = game.get("game/player" + game.yourId + "/popDeck/" + 7);
        for (String cardNbString: cards.split(",")) {
            Card card = game.yourDeck.get(Integer.parseInt(cardNbString));
            game.yourDeck.remove(Integer.parseInt(cardNbString));
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
            game.opponentDeck.remove(Integer.parseInt(cardNbString));
            game.opponentHandSize++;
        }
        game.refresh();
    }
}
