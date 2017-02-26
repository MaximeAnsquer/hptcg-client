package hptcg;

public class Draw extends Card {

    protected Draw(Game game) {
        super(game);
        this.realCard = false;
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
            game.handPanel.add(card);
        }
        game.refresh();
    }

    @Override
    protected void removeAction() {
        game.yourActionsLeft--;
        game.refresh();
    }

    @Override
    public void applyOpponentPlayed() {
        super.applyOpponentPlayed();
        String cards = game.get("game/player" + game.opponentId + "/popDeckCopy/" + 1);
        for (String cardNbString: cards.split(",")) {
            Card card = game.opponentDeck.get(Integer.parseInt(cardNbString));
            game.opponentDeck.remove(Integer.parseInt(cardNbString));
            game.opponentHandSize++;
        }
        game.refresh();
    }
}
