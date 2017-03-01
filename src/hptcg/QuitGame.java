package hptcg;

import javax.swing.*;

public class QuitGame extends Card {

    protected QuitGame(Game game) {
        super(game);
        realCard = false;
        opponentPlayedMessage = "Your opponent quit the game!";
    }

    @Override
    public void applyOpponentPlayed() {
        JOptionPane.showMessageDialog(game.frame, "Your opponent quit the game!");
        GameManager.get("gameManager/delete/" + game.gameId);
        System.exit(0);
    }

    @Override
    public boolean canBePlayed() {
        return false;
    }

    @Override
    protected void removeAction() {

    }
}
