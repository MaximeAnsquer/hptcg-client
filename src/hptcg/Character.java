package hptcg;

public abstract class Character extends Card {

    protected Character(Game game) {
        super(game);
    }

    @Override
    protected void removeAction() {
        game.yourActionsLeft -= 2;
    }
}
