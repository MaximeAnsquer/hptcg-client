package hptcg;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class CardLabel extends JLabel {


  public CardLabel(ImageIcon imageIcon, Main main, String cardName) {
    super(imageIcon);
    this.addMouseListener(new MouseListener() {
      @Override
      public void mouseClicked(MouseEvent e) {
        Main.get("http://hptcg-server.herokuapp.com/game/player" + Main.playerId + "/play/" + cardName);
        main.handPanel.remove(CardLabel.this);
        CardLabel.this.removeMouseListener(this);
        main.playedCards.add(CardLabel.this);
        main.frame.repaint();
        main.frame.pack();
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

}
