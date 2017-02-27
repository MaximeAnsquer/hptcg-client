package hptcg;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public abstract class Card extends JLabel implements ICard {

    protected boolean realCard;
    protected int powerNeeded;
    protected LessonType powerTypeNeeded;
    protected Edition edition;
    protected String cardText = "";
    protected Type type;
    protected ImageIcon imageIcon;
    protected Game game;
    protected String cardName;
    protected boolean wasDisabled;
    protected boolean removeActionAfterPlay = true;
    protected boolean disableAfterPlayer = true;

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    protected boolean disabled;

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
        this.realCard = true;
        this.disabled = false;
        setIcon(imageIcon);
        setToolTipText("<html><img src=\"" + getClass().getResource("images/" + cardName + ".jpg") + "\">");
        addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                boolean canBePlayed = canBePlayed();
                if (game.yourTurn && canBePlayed && !disabled) {
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
        applyCardEffect();
        if (removeActionAfterPlay) {
            removeAction();
        }
    }

    protected abstract void removeAction();

    public void applyCardEffect() {
        game.get("game/player" + game.getYourId() + "/play/" + cardName);
        if (disableAfterPlayer) {
            setDisabled(true);
        }
        game.removeFromHand(this);
        game.addMessage("You played: " + cardName);
//        System.out.println("You played: " + cardName);
        game.refresh();
    }

    public void applyOpponentPlayed() {
        setDisabled(true);
    }

    public void setImageScale(double scale) {
        setIcon(game.createImage(cardName, scale));
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setWasDisabled(boolean wasDisabled) {
        this.wasDisabled = wasDisabled;
    }

    public boolean getWasDisabled() {
        return wasDisabled;
    }

    public void removeLastMouseListener() {
        removeMouseListener(getMouseListeners()[getMouseListeners().length - 1]);
    }
}
