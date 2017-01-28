package hptcg;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Hashtable;
import java.util.Map;

public class Main {

  public JFrame frame;
  public JPanel handPanel;
  public JPanel boardPanel;
  public JPanel playedCards;
  public JPanel opponentsCards;
  private Map<String, ImageIcon> cardsImageIcons;
  private static int savedOpponentsLastMoveId;
  public static int playerId;
  private static int opponentsId;

  public Main() {
    savedOpponentsLastMoveId = 0;
    cardsImageIcons = new Hashtable<>();
    frame = new JFrame("Harry Potter TCG");
    frame.setPreferredSize(new Dimension(1920, 1050));
    Container contentPane = frame.getContentPane();
    contentPane.setLayout(new BorderLayout());
    contentPane.add(handPanel(), BorderLayout.SOUTH);
    contentPane.add(boardPanel());
    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
//    frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
    frame.pack();
    frame.setVisible(true);
  }

  private JPanel boardPanel() {
    boardPanel = new JPanel();
    boardPanel.setLayout(new BorderLayout());
    boardPanel.setBackground(Color.GREEN);
    boardPanel.add(playedCards(), BorderLayout.SOUTH);
    boardPanel.add(opponentsCards(), BorderLayout.NORTH);
    return boardPanel;
  }

  private JPanel opponentsCards() {
    opponentsCards = new JPanel();
    opponentsCards.setLayout(new BoxLayout(opponentsCards, BoxLayout.X_AXIS));
    return opponentsCards;
  }

  private JPanel playedCards() {
    playedCards = new JPanel();
    playedCards.setLayout(new BoxLayout(playedCards, BoxLayout.X_AXIS));
    return playedCards;
  }

  private JPanel handPanel() {
    handPanel = new JPanel();
    handPanel.setPreferredSize(new Dimension(1920, 250));
    handPanel.setLayout(new BoxLayout(handPanel, BoxLayout.X_AXIS));
    handPanel.add(getCardImage("Charms"));
    handPanel.add(getCardImage("Transfiguration"));
    handPanel.add(getCardImage("Charms"));
    handPanel.add(getCardImage("Transfiguration"));
    handPanel.add(getCardImage("Transfiguration"));
    handPanel.setBackground(Color.RED);
    return handPanel;
  }

  private CardLabel getCardImage(String cardName) {
    return new CardLabel(getImageIcon(cardName),this, cardName);
  }

  private ImageIcon getImageIcon(String cardName) {
    ImageIcon imageIcon;
    if (cardsImageIcons.containsKey(cardName)) {
      imageIcon = cardsImageIcons.get(cardName);
    } else {
      BufferedImage cardImage = null;
      try {
        cardImage = ImageIO.read(new File("src/hptcg/images/" + cardName + ".jpg"));
      } catch (IOException e) {
        e.printStackTrace();
      }
      imageIcon = new ImageIcon(cardImage);
      cardsImageIcons.put(cardName, imageIcon);
    }
    return imageIcon;
  }

//  public void repaintHand() {
//    Component[] cards = handPanel.getComponents();
//    System.out.println("cards.length:" + cards.length);
//    handPanel.removeAll();
//    int i = 0;
//    for (Component card: cards) {
//      System.out.println("lol");
//      handPanel.add(getCardImage("Charms"));
//      i++;
//    }
//    handPanel.repaint();
//    frame.repaint();
//  }

  public static void main(String[] args) {

    Main m = new Main();

    connectToServer();
    waitForOpponent();

    while(true) {
      if(newMoveFromOpponent()) {
        addOpponentCard(m);
      }
      waitFor(1000);
    }
  }

  private static void waitForOpponent() {
    boolean waiting = true;
    System.out.println("Waiting for an opponent to connect to the server...");
    while(waiting) {
      if(get("http://hptcg-server.herokuapp.com/game/player" + opponentsId + "/status").equals("connected")) {
        waiting = false;
        System.out.println("An opponent joined the game!");
      }
    }
  }

  private static void waitFor(int i) {
    try {
      Thread.sleep(i);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  private static void addOpponentCard(Main m) {
    String cardName = get("http://hptcg-server.herokuapp.com/game/player" + opponentsId + "/lastCardPlayed");
    System.out.println("Opponent played: " + cardName);
    m.opponentsCards.add(m.getCardImage(cardName));
    m.frame.repaint();
    m.frame.pack();
  }

  private static void connectToServer() {
    playerId = Integer.parseInt(get("http://hptcg-server.herokuapp.com/game/join"));
    System.out.println("You are player " + playerId);
    opponentsId = playerId == 1 ? 2 : 1;
  }

  private static boolean newMoveFromOpponent() {
    int fetchOpponentsLastMoveId = fetchOpponentsLastMoveId();
    if (fetchOpponentsLastMoveId != savedOpponentsLastMoveId) {
      System.out.println("New move from opponent!");
      savedOpponentsLastMoveId++;
      return true;
    } else {
      return false;
    }
  }

  private static int fetchOpponentsLastMoveId() {
    return Integer.parseInt(get("http://hptcg-server.herokuapp.com/game/player" + opponentsId + "/lastMoveId"));
  }

  public static String get(String url) {
    String result = "";
    try {
      BufferedReader br = new BufferedReader(new InputStreamReader(new URL(url).openStream()));
      result = br.readLine();
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    return result;
  }

}