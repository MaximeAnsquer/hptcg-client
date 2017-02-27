package hptcg;

public abstract class Character extends Card {

    protected boolean inPlay = false;

    protected Character(Game game) {
        super(game);
        removeActionAfterPlay = false;
    }

    @Override
    public void applyCardEffect() {
        super.applyCardEffect();
        if (inPlay) {
            useCharacterPower();
        } else {
            game.handPanel.remove(this);
            game.yourPlayedCard.add(this);
            inPlay = true;
        }
    }

    protected abstract void useCharacterPower();

    @Override
    protected void removeAction() {
        if (!inPlay) {
            game.yourActionsLeft -= 2;
        }
    }

    @Override
    public boolean canBePlayed() {
        if (inPlay) {
            return powerCanBePlayer();
        } else {
            return true;
        }
    }

    protected abstract boolean powerCanBePlayer();

}
