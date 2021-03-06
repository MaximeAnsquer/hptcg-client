package hptcg;

public abstract class Character extends Card {

    protected Character(Game game) {
        super(game);
        removeActionAfterPlay = false;
        disableAfterPlayer = false;
        youPlayedMessage = "You use " + cardName + "'s capacity";
        cardEffectName = cardName + "$ActivationEffect";
    }

    @Override
    public void applyCardEffect() {
        super.applyCardEffect();
        if (inPlay) {
            useCharacterPower();
        } else {
            game.yourHand.remove(this);
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
