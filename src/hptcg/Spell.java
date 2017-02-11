package hptcg;

import java.io.File;

public abstract class Spell extends Card {

    protected Spell(Game game, int powerNeeded, LessonType powerTypeNeeded) {
        super(game);
        this.powerNeeded = powerNeeded;
        this.powerTypeNeeded = powerTypeNeeded;
    }

    public void playCard() {
        super.playCard();
        game.lastSpellPlayedLabel.setIcon(game.createImage(cardName, 1.3));
        game.lastSpellPlayedLabel.setToolTipText("<html><img src=\"file:"+new File("src/hptcg/images/" + cardName + ".jpg").toString()+"\">");
        setDisabled(true);
        game.yourDiscardPile.add(this);
    }

    public void applyOpponentPlayed() {
        super.applyOpponentPlayed();
        game.lastSpellPlayedLabel.setIcon(game.createImage(cardName, 1.3));
        game.lastSpellPlayedLabel.setToolTipText("<html><img src=\"file:"+new File("src/hptcg/images/" + cardName + ".jpg").toString()+"\">");
        setDisabled(true);
        game.opponentDiscardPile.add(this);
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
