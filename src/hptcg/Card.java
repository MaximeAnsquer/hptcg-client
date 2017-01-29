package hptcg;

import javax.swing.*;
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
    this.imageIcon = this.game.getImageIcon(cardName);
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

}
