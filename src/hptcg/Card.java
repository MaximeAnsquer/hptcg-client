package hptcg;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public abstract class Card extends JLabel implements ICard {

  protected int cost;
  protected Edition edition;
  protected String name;
  protected String text;
  protected Type type;
  protected ImageIcon imageIcon;
  protected Game game;
  protected String cardName;

  protected Card(Game game, String cardName) {
    super();
    this.game = game;
    this.cardName = cardName;
    this.imageIcon = this.game.getImage(cardName, 1.0);
    setIcon(imageIcon);
    addMouseListener(new MouseListener() {
      @Override
      public void mouseClicked(MouseEvent e) {
        if (game.yourTurn) {
          playCard();
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
    return name;
  }

  public void playCard() {
    game.get("game/player" + game.getPlayerId() + "/play/" + cardName);
    removeMouseListener(this.getMouseListeners()[0]);
    game.removeFromHand(this);
    game.addMessage("You played: " + cardName);
    System.out.println("You played: " + cardName);
  }

  public void applyOpponentPlayed() {
    removeMouseListener(this.getMouseListeners()[0]);
  }

  public void setImageScale(double scale) {
    setIcon(game.createImage(cardName, scale));
  }

}
