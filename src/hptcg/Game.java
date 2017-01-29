package hptcg;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.Hashtable;
import java.util.Map;

public class Game {

  public JFrame frame;
  public JPanel handPanel;
  public JPanel boardPanel;
  public JPanel playedCards;
  public JPanel opponentsCards;
  private Map<String, ImageIcon> cardsImageIcons;
  private static int savedOpponentsLastMoveId;
  private int playerId;
  private static int opponentsId;
  public static int availableWidth;
  public static int availableHeight;
  public boolean yourTurn;
  public static JTextArea gameMessagesPanel;
  public static JLabel mainMessageLabel;

  public Game() {
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
    handPanel.add(new Charms(this));
    handPanel.add(new Transfiguration(this));
    handPanel.add(new Charms(this));
    handPanel.add(new Transfiguration(this));
    handPanel.add(new Transfiguration(this));
    handPanel.add(new Charms(this));
    return handPanel;
  }

  public ImageIcon getImageIcon(String cardName) {
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

    Game game = new Game();

    game.connectToServer();
    game.waitForOpponent();

    while(true) {
      waitFor(100);  // delay so that the yourTurn variable knows it changed
      while(!game.itsYourTurn()) {
        if(game.newMoveFromOpponent()) {
          System.out.println("newMoveFromOpponent returns true");
          game.applyOpponentsMove();
        }
        waitFor(1000);
      }
    }

  }

  private boolean itsYourTurn() {
    return yourTurn;
  }

  private void waitForOpponent() {
    boolean waiting = true;
    System.out.println("Waiting for an opponent to connect to the server...");
    mainMessageLabel.setText("Waiting for an opponent");
    while(waiting) {
      if(get("game/player" + opponentsId + "/status").equals("connected")) {
        waiting = false;
        System.out.println("An opponent joined the game!");
        yourTurn = playerId == 1;
        if(yourTurn) {
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
    System.out.println("Applying opponent's move");
    String cardName = get("game/player" + opponentsId + "/lastCardPlayed");
    System.out.println("Opponent played: " + cardName);
    addMessage("Your opponent played: " + cardName);
    opponentsCards.add(createCard(cardName));
    refresh();
    beginYourTurn();
  }

  private Card createCard(String cardName) {
    Class cardClass = null;
    try {
      cardClass = Class.forName("hptcg." + cardName);
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
    Card card = null;
    try {
      card = (Card) cardClass.getDeclaredConstructor(Game.class).newInstance(this);
    } catch (InstantiationException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (NoSuchMethodException e) {
      e.printStackTrace();
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    }
    return card;
  }

  private void beginYourTurn() {
    System.out.println("Beginning your turn");
    yourTurn = true;
    mainMessageLabel.setText("This is your turn");
    frame.repaint();
    frame.pack();
  }

  private void connectToServer() {
    playerId = Integer.parseInt(get("game/join"));
    System.out.println("You are player " + playerId);
    opponentsId = playerId == 1 ? 2 : 1;
    yourTurn = false;
  }

  private boolean newMoveFromOpponent() {
    int fetchOpponentsLastMoveId = fetchOpponentsLastMoveId();
    if (fetchOpponentsLastMoveId != savedOpponentsLastMoveId) {
      System.out.println("New move from opponent!");
      savedOpponentsLastMoveId++;
      return true;
    } else {
      return false;
    }
  }

  private int fetchOpponentsLastMoveId() {
    return Integer.parseInt(get("game/player" + opponentsId + "/lastMoveId"));
  }

  public String get(String uri) {
    String result = "";
    String url = "http://hptcg-server.herokuapp.com/" + uri;
    try {
      BufferedReader br = new BufferedReader(new InputStreamReader(new URL(url).openStream()));
      result = br.readLine();
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    return result;
  }

  public void endYourTurn() {
    yourTurn = false;
    mainMessageLabel.setText("It's your opponent's turn.");
    refresh();
  }

  public void addMessage(String s) {
    gameMessagesPanel.append("\n" + s);
  }

  public int getPlayerId() {
    return playerId;
  }

  public void removeFromHand(Card card) {
    handPanel.remove(card);
  }

  public void addToPlayedCards(Card card) {
    playedCards.add(card);
  }

  public void refresh() {
    frame.repaint();
    frame.pack();
  }
}
