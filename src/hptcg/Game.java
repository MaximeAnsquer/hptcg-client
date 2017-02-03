package hptcg;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Hashtable;
import java.util.Map;

public class Game {

    public JFrame frame;
    public JPanel handPanel;
    public JPanel boardPanel;
    public JPanel yourPlayedCard;
    public JPanel opponentsCards;
    private Map<String, ImageIcon> cardsImageIcons;
    private static int savedOpponentsLastMoveId;
    private int yourId;
    private static int opponentId;
    public static int availableWidth;
    public static int availableHeight;
    public boolean yourTurn;
    public static JTextArea gameMessagesPanel;
    public static JLabel mainMessageLabel;
    private Map<LessonType, Integer> yourLessons;
    private Map<LessonType, Integer> opponentsLessons;
    private Map<LessonType, JLabel> yourLessonsLabels;
    private Map<LessonType, JLabel> opponentsLessonsLabels;
    private Card yourStartingCharacter;
    private int yourDeckSize;
    private int yourHandSize;
    private Card opponentStartingCharacter;
    private int opponentDeckSize;
    private int opponentHandSize;
    private JPanel leftPanel;
//    private String serverUrl = "http://hptcg-server.herokuapp.com/";
    private String serverUrl = "http://localhost:8080/";
    Map<LessonType, Integer> totalPower;
    JLabel lastSpellPlayedLabel;
    JPanel yourDiscardPile;
    JPanel opponentDiscardPile;
    JFrame yourDiscardPileFrame;
    JFrame opponentDiscardPileFrame;

    public Game() {
        yourDeckSize = 60;
        yourHandSize = 7;
        opponentDeckSize = 60;
        opponentHandSize = 7;
        cardsImageIcons = new Hashtable<>();
        yourStartingCharacter = chooseStartingCharacter();
        opponentStartingCharacter = opponentStartingCharacter();
        opponentsLessonsLabels = new Hashtable<>();
        yourLessonsLabels = new Hashtable<>();
        yourLessons = yourLessons();
        opponentsLessons = opponentsLessons();
        totalPower = totalPower();
        yourDiscardPile = createDiscardPile();
        opponentDiscardPile = createDiscardPile();
        yourDiscardPileFrame = discardPileFrame(yourDiscardPile);
        opponentDiscardPileFrame = discardPileFrame(opponentDiscardPile);
        savedOpponentsLastMoveId = 0;
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

    private JFrame discardPileFrame(JPanel discardPile) {
        JFrame frame = new JFrame("Your discard pile");
        Container contentPane = frame.getContentPane();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.X_AXIS));
        contentPane.add(discardPile);
        frame.setLocationRelativeTo(null);
        return frame;
    }

    private JPanel createDiscardPile() {
        JPanel discardPile = new JPanel();
        discardPile.setLayout(new BoxLayout(discardPile, BoxLayout.Y_AXIS));
        return discardPile;
    }

    private Map<LessonType, Integer> totalPower() {
        Map<LessonType, Integer> result = new Hashtable<>();
        for (LessonType lessonType: LessonType.values()) {
            result.put(lessonType, 0);
        }
        return result;
    }

    private Card opponentStartingCharacter() {
        Character emptyCharacter = new EmptyCharacter(this);
        emptyCharacter.setIcon(null);
        return emptyCharacter;
    }

    private Card chooseStartingCharacter() {
        int choice = JOptionPane.showOptionDialog(frame, "Please choose your starting character",
                "Startig Character",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                new String[]{"Hermione Granger", "Draco Malfoy"},
                "Hermione Granger");
        Card character = choice == 0 ? new HermioneGranger(this) : new DracoMalfoy(this);
        character.setImageScale(1.25);
        return character;
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
        leftPanel = new JPanel(new BorderLayout());
        leftPanel.add(lastSpellPlayedLabel());
        leftPanel.add(genericPlayerInfo(yourDeckSize, yourHandSize, yourStartingCharacter, yourDiscardPileFrame), BorderLayout.SOUTH);
        return leftPanel;
    }

    private JLabel lastSpellPlayedLabel() {
        lastSpellPlayedLabel = new JLabel();
        return lastSpellPlayedLabel;
    }

    private JPanel genericPlayerInfo(int playerDeckSize, int playerHandSize, Card playerStartingCharacter, JFrame playerDiscardPileFrame) {
        JPanel playerInfoPanel = new JPanel();
        playerInfoPanel.setLayout(new BoxLayout(playerInfoPanel, BoxLayout.Y_AXIS));
        playerInfoPanel.add(discardPileButton(playerDiscardPileFrame));
        playerInfoPanel.add(genericPlayerDeckLabel(playerDeckSize));
        playerInfoPanel.add(genericPlayerHandLabel(playerHandSize));
        playerInfoPanel.add(playerStartingCharacter);
        return playerInfoPanel;
    }

    private JButton discardPileButton(JFrame playerDiscardPileFrame) {
        JButton button = new JButton("Discard pile");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                playerDiscardPileFrame.repaint();
                playerDiscardPileFrame.pack();
                playerDiscardPileFrame.setVisible(true);
            }
        });
        return button;
    }

    private JLabel genericPlayerHandLabel(int playerHandSize) {
        return new JLabel("Hand: " + playerHandSize);
    }

    private JLabel genericPlayerDeckLabel(int playerDeckSize) {
        return new JLabel("Deck: " + playerDeckSize);
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
        boardPanel = new BoardPanel();
        boardPanel.add(createGenericPlayedCardsPanel(yourPlayedCard, yourLessonsLabels), BorderLayout.SOUTH);
        boardPanel.add(createGenericPlayedCardsPanel(opponentsCards, opponentsLessonsLabels), BorderLayout.NORTH);
        return boardPanel;
    }

    private JPanel createGenericLessonPanel(Map<LessonType, JLabel> playersLessonsLabels) {
        JPanel opponentsLessonsPanel = new JPanel();
        opponentsLessonsPanel.setLayout(new BoxLayout(opponentsLessonsPanel, BoxLayout.X_AXIS));
        opponentsLessonsPanel.setOpaque(false);
        double scale = 0.70;
        for(LessonType lessonType: LessonType.values()) {
            JLabel lessonLabel = new JLabel(createImage(lessonType.toString(), scale));
            lessonLabel.setVisible(false);
            lessonLabel.setForeground(Color.WHITE);
            lessonLabel.setVerticalTextPosition(JLabel.CENTER);
            lessonLabel.setHorizontalTextPosition(JLabel.LEFT);
            lessonLabel.setFont(new Font(Font.SERIF, Font.BOLD, 40));
            opponentsLessonsPanel.add(lessonLabel);
            playersLessonsLabels.put(lessonType, lessonLabel);
        }
        return opponentsLessonsPanel;
    }

    private JPanel createGenericPlayedCardsPanel(JPanel playerPlayedCards, Map<LessonType, JLabel> playerLessonsLabels) {
        playerPlayedCards = new JPanel();
        playerPlayedCards.setOpaque(false);
        playerPlayedCards.setLayout(new BoxLayout(playerPlayedCards, BoxLayout.Y_AXIS));
        playerPlayedCards.add(createGenericLessonPanel(playerLessonsLabels));
        return playerPlayedCards;
    }

    private JScrollPane handPanel() {
        handPanel = new JPanel();
        handPanel.setLayout(new BoxLayout(handPanel, BoxLayout.X_AXIS));
        for (int i=0; i < 3; i++) {
            handPanel.add(new Charms(this));
            handPanel.add(new Avifors(this));
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

    public Map<LessonType, Integer> getOpponentsLessons() {
        return opponentsLessons;
    }

    public ImageIcon createImage(String cardName, double scale) {
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

    public ImageIcon resizeImage(ImageIcon imageIcon, double scale) {
        scale = scale * 0.5;
        Image image = imageIcon.getImage();
        int width = (int) (image.getWidth(null) * scale);
        int height = (int) (image.getHeight(null) * scale);
        Image newImage = image.getScaledInstance(width, height,  java.awt.Image.SCALE_SMOOTH);
        return new ImageIcon(newImage);
    }

    private boolean itsYourTurn() {
        return yourTurn;
    }

    private void waitForOpponent() {
        boolean waiting = true;
        System.out.println("Waiting for an opponent to connect to the server...");
        mainMessageLabel.setText("Waiting for an opponent");
        while(waiting) {
            if(get("game/player" + opponentId + "/status").equals("connected")) {
                waiting = false;
                System.out.println("An opponent joined the game!");
                yourTurn = yourId == 1;
                waitFor(1000);
                String opponentCharacterName = get("game/player" + opponentId + "/startingCharacter");
                System.out.println("opponentCharacterName: " + opponentCharacterName);
                createOpponent(opponentCharacterName);
                this.refresh();
                if(yourTurn) {
                    mainMessageLabel.setText("It's your turn");
                } else {
                    mainMessageLabel.setText("It's your opponent's turn.");
                }
            }
        }
    }

    private void createOpponent(String opponentCharacterName) {
        opponentStartingCharacter = createCard(opponentCharacterName);
        opponentStartingCharacter.setImageScale(1.25);
        leftPanel.add(genericPlayerInfo(opponentDeckSize, opponentHandSize, opponentStartingCharacter, opponentDiscardPileFrame), BorderLayout.NORTH);
    }

    private static void waitFor(int i) {
        try {
            Thread.sleep(i);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void applyOpponentsMove() {
        String cardName = get("game/player" + opponentId + "/lastCardPlayed");
        System.out.println("Opponent played: " + cardName);
        addMessage("Your opponent played: " + cardName);
        Card opponentsCard = createCard(cardName);
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return card;
    }

    private void beginYourTurn() {
        System.out.println("Beginning your turn");
        yourTurn = true;
        mainMessageLabel.setText("It's your turn");
        frame.repaint();
        frame.pack();
    }

    private void connectToServer() {
        String connectionResult = get("game/join");
        if (connectionResult.equals("Game is full")) {
            get("game/reset");
            System.out.println("Game was reset");
            waitFor(1000);
            connectToServer();
        } else {
            yourId = Integer.parseInt(connectionResult);
            System.out.println("You are player " + yourId);
            put("game/player" + yourId + "/startingCharacter", yourStartingCharacter.getCardName());
            opponentId = yourId == 1 ? 2 : 1;
            yourTurn = false;
        }
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
        return Integer.parseInt(get("game/player" + opponentId + "/lastMoveId"));
    }

    public String get(String uri) {
        String result = "";
        String url = serverUrl + uri;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new URL(url).openStream()));
            result = br.readLine();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }

    public void put(String uri, String payload) {
        try {
            URL url = new URL(serverUrl + uri);
            HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
            httpCon.setDoOutput(true);
            httpCon.setRequestMethod("PUT");
            OutputStreamWriter out = new OutputStreamWriter(httpCon.getOutputStream());
            out.write(payload);
            out.close();
            httpCon.getInputStream();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void endYourTurn() {
        yourTurn = false;
        mainMessageLabel.setText("It's your opponent's turn.");
        refresh();
    }

    public void addMessage(String s) {
        gameMessagesPanel.append("\n" + s);
    }

    public int getYourId() {
        return yourId;
    }

    public Map<LessonType, JLabel> getYourLessonsLabels() {
        return yourLessonsLabels;
    }

    public Map<LessonType, JLabel> getOpponentsLessonsLabels() {
        return opponentsLessonsLabels;
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
        totalPower.put(lessonType, totalPower.get(lessonType) + 1);
        yourLessonsLabels.get(lessonType).setText(String.valueOf(yourLessons.get(lessonType)));
        yourLessonsLabels.get(lessonType).setVisible(true);
    }

    public void removeLesson(LessonType lessonType, boolean opponent) {
        if (opponent) {
            getOpponentsLessons().put(lessonType, getOpponentsLessons().get(lessonType) -1);
            getOpponentsLessonsLabels().get(lessonType).setText(String.valueOf(getOpponentsLessons().get(lessonType)));
            if (getOpponentsLessons().get(lessonType) == 0 ) {
                getOpponentsLessonsLabels().get(lessonType).setVisible(false);
            }
        } else {
            totalPower.put(lessonType, totalPower.get(lessonType) -1);
            getYourLessons().put(lessonType, getYourLessons().get(lessonType) -1);
            getYourLessonsLabels().get(lessonType).setText(String.valueOf(getYourLessons().get(lessonType)));
            if (getYourLessons().get(lessonType) == 0 ) {
                getYourLessonsLabels().get(lessonType).setVisible(false);
            }
        }
    }

    public void addOpponentLesson(LessonType lessonType) {
        opponentsLessons.put(lessonType, opponentsLessons.get(lessonType) + 1);
        opponentsLessonsLabels.get(lessonType).setText(String.valueOf(opponentsLessons.get(lessonType)));
        opponentsLessonsLabels.get(lessonType).setVisible(true);
    }

    public JFrame getFrame() {
        return frame;
    }

    public JPanel getHandPanel() {
        return handPanel;
    }

    public JPanel getBoardPanel() {
        return boardPanel;
    }

    public JPanel getYourPlayedCard() {
        return yourPlayedCard;
    }

    public JPanel getOpponentsCards() {
        return opponentsCards;
    }

    public Map<String, ImageIcon> getCardsImageIcons() {
        return cardsImageIcons;
    }

    public static int getSavedOpponentsLastMoveId() {
        return savedOpponentsLastMoveId;
    }

    public static int getOpponentId() {
        return opponentId;
    }

    public static int getAvailableWidth() {
        return availableWidth;
    }

    public static int getAvailableHeight() {
        return availableHeight;
    }

    public boolean isYourTurn() {
        return yourTurn;
    }

    public static JTextArea getGameMessagesPanel() {
        return gameMessagesPanel;
    }

    public static JLabel getMainMessageLabel() {
        return mainMessageLabel;
    }

    public Map<LessonType, Integer> getYourLessons() {
        return yourLessons;
    }

    public Card getYourStartingCharacter() {
        return yourStartingCharacter;
    }

    public int getYourDeckSize() {
        return yourDeckSize;
    }

    public int getYourHandSize() {
        return yourHandSize;
    }

    public Card getOpponentStartingCharacter() {
        return opponentStartingCharacter;
    }

    public int getOpponentDeckSize() {
        return opponentDeckSize;
    }

    public int getOpponentHandSize() {
        return opponentHandSize;
    }

    public JPanel getLeftPanel() {
        return leftPanel;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public static void main(String[] args) {

        Game game = new Game();

        game.connectToServer();
        game.waitForOpponent();

        while(true) {
            waitFor(100);  // delay so that the yourTurn variable knows it changed
            while(!game.itsYourTurn()) {
                if(game.newMoveFromOpponent()) {
                    game.applyOpponentsMove();
                }
                waitFor(1000);
            }
        }

    }

}
