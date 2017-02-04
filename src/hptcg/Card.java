package hptcg;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.net.URL;

public abstract class Card extends JLabel implements ICard {

    protected int powerNeeded;


    public LessonType getPowerTypeNeeded() {
        return powerTypeNeeded;
    }

    public void setPowerTypeNeeded(LessonType powerTypeNeeded) {
        this.powerTypeNeeded = powerTypeNeeded;
    }

    protected LessonType powerTypeNeeded;
    protected Edition edition;
    protected String cardText = "";
    protected Type type;
    protected ImageIcon imageIcon;
    protected Game game;
    protected String cardName;

    public int getPowerNeeded() {
        return powerNeeded;
    }

    public void setPowerNeeded(int powerNeeded) {
        this.powerNeeded = powerNeeded;
    }

    public Edition getEdition() {
        return edition;
    }

    public void setEdition(Edition edition) {
        this.edition = edition;
    }

    @Override
    public void setText(String text) {
        this.cardText = text;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public ImageIcon getImageIcon() {
        return imageIcon;
    }

    public void setImageIcon(ImageIcon imageIcon) {
        this.imageIcon = imageIcon;
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

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    protected Card(Game game, String cardName) {
        super();
        this.game = game;
        this.cardName = cardName;
        this.imageIcon = this.game.getImage(cardName, 1.0);
        setIcon(imageIcon);
        addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (game.yourTurn && canBePlayed()) {
                    playCard();
                    String path = new File("src/hptcg/images/" + cardName + ".jpg").getAbsolutePath();
                    System.out.println("path: " + path);
                    URL url = getClass().getResource(path);
                    setToolTipText("<html><body><img src='" + url + "'></body></html>");
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
    }

    public String toString() {
        return cardName;
    }

    public void playCard() {
        game.get("game/player" + game.getYourId() + "/play/" + cardName);
        removeMouseListener(this.getMouseListeners()[0]);
        game.removeFromHand(this);
        game.addMessage("You played: " + cardName);
        System.out.println("You played: " + cardName);
        game.refresh();
        game.endYourTurn(); //TODO: Remove
    }

    public void applyOpponentPlayed() {
        removeMouseListener(this.getMouseListeners()[0]);
    }

    public void setImageScale(double scale) {
        setIcon(game.createImage(cardName, scale));
    }

}
