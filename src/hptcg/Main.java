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
  public static int availableWidth;
  public static int availableHeight;
  public static boolean youTurn;
  public static JTextArea gameMessagesPanel;
  public static JLabel mainMessageLabel;

  public Main() {
    savedOpponentsLastMoveId = 0;
    cardsImageIcons = new Hashtable<>();
    frame = new JFrame("Harry Potter TCG");
    setSize();
    Container contentPane = frame.getContentPane();
    contentPane.setLayout(new BorderLayout());
    contentPane.add(handAndMainMessagePanels(), BorderLayout.SOUTH);
    contentPane.add(boardPanel(), BorderLayout.CENTER);
    contentPane.add(gameMessagesPanel(), BorderLayout.EAST);
    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    frame.pack();
    frame.setVisible(true);
  }

  private JPanel handAndMainMessagePanels() {
    JPanel handAndMainMessagePanels = new JPanel();
    handAndMainMessagePanels.setLayout(new BoxLayout(handAndMainMessagePanels, BoxLayout.Y_AXIS));
    handAndMainMessagePanels.add(mainMessagePanel());
    handAndMainMessagePanels.add(handPanel());
    return handAndMainMessagePanels;
  }

  private JPanel mainMessagePanel() {
    JPanel mainMessagePanel = new JPanel();
    mainMessagePanel.add(mainMessageLabel());
    return mainMessagePanel;
  }

  private JLabel mainMessageLabel() {
    mainMessageLabel = new JLabel("This is the main message");
    return mainMessageLabel;
  }

  private JScrollPane gameMessagesPanel() {
    gameMessagesPanel = new JTextArea();
    gameMessagesPanel.setLayout(new BoxLayout(gameMessagesPanel, BoxLayout.Y_AXIS));
    return new JScrollPane(gameMessagesPanel);
  }

  private void setSize() {
    Rectangle availableSpace = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
    availableWidth = (int) availableSpace.getWidth();
    availableHeight = (int) availableSpace.getHeight();
    frame.setPreferredSize(new Dimension(availableWidth, availableHeight));
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
    playedCards.setBackground(Color.GREEN);
    playedCards.setLayout(new BoxLayout(playedCards, BoxLayout.X_AXIS));
    return playedCards;
  }

  private JPanel handPanel() {
    handPanel = new JPanel();
    handPanel.setPreferredSize(new Dimension(availableWidth, 125));
    handPanel.setLayout(new BoxLayout(handPanel, BoxLayout.X_AXIS));
    handPanel.add(getCardImage("Charms"));
    handPanel.add(getCardImage("Transfiguration"));
    handPanel.add(getCardImage("Charms"));
    handPanel.add(getCardImage("Transfiguration"));
    handPanel.add(getCardImage("Transfiguration"));
    handPanel.add(getCardImage("Charms"));
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
      imageIcon = resizeImage(imageIcon);
      cardsImageIcons.put(cardName, imageIcon);
    }
    return imageIcon;
  }

  private ImageIcon resizeImage(ImageIcon imageIcon) {
    Image image = imageIcon.getImage();
    Image newImage = image.getScaledInstance(175, 125,  java.awt.Image.SCALE_SMOOTH);
    return new ImageIcon(newImage);
  }

  public static void main(String[] args) {

    Main m = new Main();

    connectToServer();
    waitForOpponent();

    while(true) {
      while(!youTurn) {
        if(newMoveFromOpponent()) {
          applyOpponentsMove();
        }
        waitFor(1000);
      }
    }

  }

  private static void waitForOpponent() {
    boolean waiting = true;
    System.out.println("Waiting for an opponent to connect to the server...");
    mainMessageLabel.setText("Waiting for an opponent");
    while(waiting) {
      if(get("http://hptcg-server.herokuapp.com/game/player" + opponentsId + "/status").equals("connected")) {
        waiting = false;
        System.out.println("An opponent joined the game!");
        if(youTurn) {
          mainMessageLabel.setText("It's your turn");
        } else {
          mainMessageLabel.setText("It's your opponent's turn.");
        }
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

  private void applyOpponentsMove() {
    String cardName = get("http://hptcg-server.herokuapp.com/game/player" + opponentsId + "/lastCardPlayed");
    System.out.println("Opponent played: " + cardName);
    addMessage("Your opponent played: " + cardName);
    opponentsCards.add(getCardImage(cardName));
    frame.repaint();
    frame.pack();
    beginYourTurn();
  }

  private void beginYourTurn() {
    youTurn = true;
    mainMessageLabel.setText("This is your turn");
    frame.repaint();
    frame.pack();
  }

  private static void connectToServer() {
    playerId = Integer.parseInt(get("http://hptcg-server.herokuapp.com/game/join"));
    System.out.println("You are player " + playerId);
    opponentsId = playerId == 1 ? 2 : 1;
    youTurn = playerId == 1;
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

  public void endYourTurn() {
    youTurn = false;
    mainMessageLabel.setText("It's your opponent's turn.");
  }

  public void addMessage(String s) {
    gameMessagesPanel.append("\n" + s);
  }
}
