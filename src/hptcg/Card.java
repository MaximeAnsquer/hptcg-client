package hptcg;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.net.URL;

public abstract class Card extends JLabel implements ICard {

    protected int powerNeeded;
    protected LessonType powerTypeNeeded;
    protected Edition edition;
    protected String cardText = "";
    protected Type type;
    protected ImageIcon imageIcon;
    protected Game game;
    protected String cardName;

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    protected boolean disabled = false;

    @Override
    public void setText(String text) {
        this.cardText = text;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public String getCardName() {
        return cardName;
    }


    protected Card(Game game) {
        super();
        this.game = game;
        this.cardName = this.getClass().getSimpleName();
        this.imageIcon = this.game.getImage(cardName, 1.0);
        setIcon(imageIcon);
//        setToolTipText("<html><img src="+new File("src/hptcg/images/" + cardName + ".jpg").toString()+"\">");
        setToolTipText("<html><img src=\"" + getClass().getResource("images/" + cardName + ".jpg") + "\">");
        addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (game.yourTurn && canBePlayed() && !disabled) {
                    playCard();
                }
            }
            @Override
            public void mousePressed(MouseEvent e) {}
            @Override
            public void mouseReleased(MouseEvent e) {}
            @Override
            public void mouseEntered(MouseEvent e) {}
            @Override
            public void mouseExited(MouseEvent e) {}
        });
    }

    public String toString() {
        return cardName;
    }

    public void playCard() {
        game.get("game/player" + game.getYourId() + "/play/" + cardName);
        setDisabled(true);
        game.removeFromHand(this);
        game.addMessage("You played: " + cardName);
        System.out.println("You played: " + cardName);
        game.refresh();
    }

    public void applyOpponentPlayed() {
        setDisabled(true);
        if(!cardName.equals("EndTurn")) {
            game.opponentHandSize--;
        }
    }

    public void setImageScale(double scale) {
        setIcon(game.createImage(cardName, scale));
    }

    public String cardChosenByOpponent() {
        boolean waiting = true;
        String target = null;
        game.refresh();
        game.waitFor(1000);
        while (waiting) {
            target = game.get("game/player" + game.opponentId + "/target");
            if (target != null && !target.equals("")) {
                waiting = false;
            }
        }
        return target;
    }

}
