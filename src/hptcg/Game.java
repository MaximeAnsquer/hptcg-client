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
  private Map<LessonType, Integer> yourLessons;
  private Map<LessonType, Integer> opponentsLessons;
  private JLabel startingCharacter;
  private Map<LessonType, JLabel> yourLessonsLabels;
  private Map<LessonType, JLabel> opponentsLessonsLabels;

  public Game() {
    opponentsLessonsLabels = new Hashtable<>();
    yourLessonsLabels = new Hashtable<>();
    yourLessons = yourLessons();
    opponentsLessons = opponentsLessons();
    savedOpponentsLastMoveId = 0;
    cardsImageIcons = new Hashtable<>();
    frame = new JFrame("Harry Potter TCG");
    setSize();
    Container contentPane = frame.getContentPane();
    contentPane.setLayout(new BorderLayout());
    contentPane.add(handAndMainMessagePanels(), BorderLayout.SOUTH);
    contentPane.add(boardPanel(), BorderLayout.CENTER);
    contentPane.add(gameMessagesPanel(), BorderLayout.EAST);
    contentPane.add(leftPanel(), BorderLayout.WEST);
    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    frame.pack();
    frame.setVisible(true);
  }

  private Map<LessonType, Integer> opponentsLessons() {
    Hashtable<LessonType, Integer> result = new Hashtable<>();
    for(LessonType lessonType: LessonType.values()) {
      result.put(lessonType, 0);
    }
    return result;
  }

  private Map<LessonType, Integer> yourLessons() {
    Hashtable<LessonType, Integer> result = new Hashtable<>();
    for(LessonType lessonType: LessonType.values()) {
      result.put(lessonType, 0);
    }
    return result;
  }

  private JPanel leftPanel() {
    JPanel leftPanel = new JPanel(new BorderLayout());
    JPanel bottom = new JPanel();
    bottom.setLayout(new GridLayout(0, 1, 0, 0));
    bottom.add(new JLabel("Deck:"));
    bottom.add(new JLabel("Hand:"));
    startingCharacter = new JLabel(getImage("character", 1));
    bottom.add(startingCharacter);
    leftPanel.add(bottom, BorderLayout.SOUTH);
    return leftPanel;
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
    opponentsCards.setBackground(Color.GREEN);
    opponentsCards.setLayout(new BoxLayout(opponentsCards, BoxLayout.Y_AXIS));
    opponentsCards.add(opponentsLessonsPanel());
    return opponentsCards;
  }

  private JPanel opponentsLessonsPanel() {
    JPanel opponentsLessonsPanel = new JPanel();
    opponentsLessonsPanel.setLayout(new BoxLayout(opponentsLessonsPanel, BoxLayout.X_AXIS));
    opponentsLessonsPanel.setBackground(Color.GREEN);
    double scale = 0.70;
    for(LessonType lessonType: LessonType.values()) {
      JLabel lessonLabel = new JLabel(createImage(lessonType.toString(), scale));
      lessonLabel.setVisible(false);
      lessonLabel.setText("0");
      lessonLabel.setVerticalTextPosition(JLabel.CENTER);
      lessonLabel.setHorizontalTextPosition(JLabel.LEFT);
      lessonLabel.setFont(new Font(Font.SERIF, Font.BOLD, 40));
      opponentsLessonsPanel.add(lessonLabel);
      opponentsLessonsLabels.put(lessonType, lessonLabel);
    }
    return opponentsLessonsPanel;
  }

  private JPanel playedCards() {
    playedCards = new JPanel();
    playedCards.setBackground(Color.GREEN);
    playedCards.setLayout(new BoxLayout(playedCards, BoxLayout.Y_AXIS));
    playedCards.add(yourLessonsPanel());
    return playedCards;
  }

  private JPanel yourLessonsPanel() {
    JPanel yourLessonsPanel = new JPanel();
    yourLessonsPanel.setLayout(new BoxLayout(yourLessonsPanel, BoxLayout.X_AXIS));
    yourLessonsPanel.setBackground(Color.GREEN);
    double scale = 0.70;
    for(LessonType lessonType: LessonType.values()) {
      JLabel lessonLabel = new JLabel(createImage(lessonType.toString(), scale));
      lessonLabel.setVisible(false);
      lessonLabel.setText("0");
      lessonLabel.setVerticalTextPosition(JLabel.CENTER);
      lessonLabel.setHorizontalTextPosition(JLabel.LEFT);
      lessonLabel.setFont(new Font(Font.SERIF, Font.BOLD, 40));
      yourLessonsPanel.add(lessonLabel);
      yourLessonsLabels.put(lessonType, lessonLabel);
    }
    return yourLessonsPanel;
  }

  private JScrollPane handPanel() {
    handPanel = new JPanel();
    handPanel.setLayout(new BoxLayout(handPanel, BoxLayout.X_AXIS));
    for (int i=0; i < 3; i++) {
      handPanel.add(new Charms(this));
      handPanel.add(new Potions(this));
      handPanel.add(new Transfiguration(this));
      handPanel.add(new CareOfMagicalCreatures(this));
      handPanel.add(new Quidditch(this));
    }
    return new JScrollPane(handPanel, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
  }

  public ImageIcon getImage(String cardName, double scale) {
    ImageIcon imageIcon;
    if (cardsImageIcons.containsKey(cardName)) {
      imageIcon = cardsImageIcons.get(cardName);
    } else {
      imageIcon = createImage(cardName, scale);
      cardsImageIcons.put(cardName, imageIcon);
    }
    return imageIcon;
  }

  private ImageIcon createImage(String cardName, double scale) {
    BufferedImage cardImage = null;
    try {
      cardImage = ImageIO.read(new File("src/hptcg/images/" + cardName + ".jpg"));
    } catch (IOException e) {
      System.out.println("cardName: " + cardName);
      e.printStackTrace();
    }
    ImageIcon imageIcon = new ImageIcon(cardImage);
    imageIcon = resizeImage(imageIcon, scale);
    return imageIcon;
  }

  private ImageIcon resizeImage(ImageIcon imageIcon, double scale) {
    Image image = imageIcon.getImage();
    int width = (int) (scale * 175);
    int height = (int) (scale * 125);
    Image newImage = image.getScaledInstance(width, height,  java.awt.Image.SCALE_SMOOTH);
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
    Card opponentsCard = (Card) createCard(cardName);
    opponentsCard.applyOpponentPlayed();
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

  public void refresh() {
    frame.repaint();
    frame.pack();
  }

  public void addLesson(LessonType lessonType) {
    yourLessons.put(lessonType, yourLessons.get(lessonType) + 1);
    yourLessonsLabels.get(lessonType).setText(String.valueOf(yourLessons.get(lessonType)));
    yourLessonsLabels.get(lessonType).setVisible(true);
  }

  public void addOpponentLesson(LessonType lessonType) {
    opponentsLessons.put(lessonType, opponentsLessons.get(lessonType) + 1);
    opponentsLessonsLabels.get(lessonType).setText(String.valueOf(opponentsLessons.get(lessonType)));
    opponentsLessonsLabels.get(lessonType).setVisible(true);
  }
}
