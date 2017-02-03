package hptcg;

public abstract class Spell extends Card {

    protected Spell(Game game, String cardName) {
        super(game, cardName);
    }

    public void playCard() {
        super.playCard();
        game.lastSpellPlayedLabel.setIcon(game.resizeImage(imageIcon, 3));
        game.yourDiscardPileFrame.getContentPane().add(this);
        this.removeMouseListener(this.getMouseListeners()[0]);
    }

    public void applyOpponentPlayed() {
        super.applyOpponentPlayed();
        game.lastSpellPlayedLabel.setIcon(imageIcon);
        game.opponentDiscardPileFrame.getContentPane().add(this);
    }

    public boolean canBePlayed() {
        boolean hasPowerTypeNeeded = game.totalPower.containsKey(powerTypeNeeded) && game.totalPower.get(powerTypeNeeded) > 0;

        int totalPower = 0;
        for (LessonType lessonType: game.totalPower.keySet()) {
            totalPower += game.totalPower.get(lessonType);
        }
        return hasPowerTypeNeeded && totalPower >= powerNeeded;
    }
}
