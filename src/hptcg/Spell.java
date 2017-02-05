package hptcg;

public abstract class Spell extends Card {

    protected Spell(Game game, int powerNeeded, LessonType powerTypeNeeded) {
        super(game);
        this.powerNeeded = powerNeeded;
        this.powerTypeNeeded = powerTypeNeeded;
    }

    public void playCard() {
        super.playCard();
        game.lastSpellPlayedLabel.setIcon(game.resizeImage(imageIcon, 3));
        game.yourDiscardPileFrame.getContentPane().add(this);
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
