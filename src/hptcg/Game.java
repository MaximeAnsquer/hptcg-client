package hptcg;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

@SuppressWarnings("Duplicates")
public class Game {

    private final DrawHand drawHandCard;
    public JFrame frame;
    public JPanel handPanel;
    public JPanel boardPanel;
    public JPanel yourPlayedCard;
    public JPanel opponentsCards;
    private Map<String, ImageIcon> cardsImageIcons;
    private static int savedNbCardsPlayedByOpponent;
    public int yourId;
    int opponentId;
    public static int availableWidth;
    public static int availableHeight;
    public boolean yourTurn;
    public static JTextArea gameMessagesPanel;
    JLabel mainMessageLabel;
    Map<LessonType, Integer> yourLessons;
    Map<LessonType, Integer> opponentsLessons;
    private Map<LessonType, JLabel> yourLessonsLabels;
    private Map<LessonType, JLabel> opponentsLessonsLabels;
    Card yourStartingCharacter;
    Card opponentStartingCharacter;
    private JPanel leftPanel;
    //    private String serverUrl = "http://hptcg-server.herokuapp.com/";
    private String serverUrl = "http://localhost:8080/";
    Map<LessonType, Integer> totalPower;
    JLabel lastSpellPlayedLabel;
    JPanel yourDiscardPile;
    JPanel opponentDiscardPile;
    JFrame yourDiscardPileFrame;
    JFrame opponentDiscardPileFrame;
    JPanel yourCreaturesPanel;
    JPanel opponentCreaturesPanel;
    Card endTurnCard;
    Card drawCard;
    JButton yourDiscardPileButton;
    JButton opponentDiscardPileButton;
    JLabel yourHandLabel;
    JLabel opponentHandLabel;
    JLabel yourDeckLabel;
    JLabel opponentDeckLabel;
    int opponentHandSize = 7;
    Map<Integer, Card> yourDeck;
    Map<Integer, Card> opponentDeck;

    public Game() {
        cardsImageIcons = new Hashtable<>();
        endTurnCard  = new EndTurn(this);
        drawCard  = new Draw(this);
        drawHandCard  = new DrawHand(this);
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
        yourCreaturesPanel = creaturePanel();
        opponentCreaturesPanel = creaturePanel();
        opponentDeck = createOpponentDeck();
        yourDeck = createYourDeck();
        savedNbCardsPlayedByOpponent = 0;
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

    private Map<Integer, Card> createYourDeck() {
        //TODO: importer le deck choisi par le joueur
        yourDeck = createFakeDeck();
        return yourDeck;
    }

    private Map<Integer, Card> createOpponentDeck() {
        //TODO: importer le deck choisi par le joueur
        opponentDeck = createFakeDeck();
        return opponentDeck;
    }

    private Map<Integer, Card> createFakeDeck() {
        Map<Integer, Card> fakeDeck = new Hashtable<>();
        for (int i=0; i < 3; i++) {
            fakeDeck.put(fakeDeck.size(), new Charms(this));
            fakeDeck.put(fakeDeck.size(), new Charms(this));
            fakeDeck.put(fakeDeck.size(), new Avifors(this));
            fakeDeck.put(fakeDeck.size(), new CuriousRaven(this));
            fakeDeck.put(fakeDeck.size(), new Stupefy(this));
            fakeDeck.put(fakeDeck.size(), new Charms(this));
            fakeDeck.put(fakeDeck.size(), new HagridAndTheStranger(this));
            fakeDeck.put(fakeDeck.size(), new Accio(this));
            fakeDeck.put(fakeDeck.size(), new Potions(this));
            fakeDeck.put(fakeDeck.size(), new Potions(this));
            fakeDeck.put(fakeDeck.size(), new Epoximise(this));
            fakeDeck.put(fakeDeck.size(), new Vermillious(this));
            fakeDeck.put(fakeDeck.size(), new TakeRoot(this));
            fakeDeck.put(fakeDeck.size(), new ViciousWolf(this));
            fakeDeck.put(fakeDeck.size(), new ForestTroll(this));
            fakeDeck.put(fakeDeck.size(), new BoaConstrictor(this));
            fakeDeck.put(fakeDeck.size(), new Transfiguration(this));
            fakeDeck.put(fakeDeck.size(), new CareOfMagicalCreatures(this));
            fakeDeck.put(fakeDeck.size(), new CareOfMagicalCreatures(this));
            fakeDeck.put(fakeDeck.size(), new MagicalMishap(this));
        }
        return fakeDeck;
    }

    private JPanel creaturePanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        return panel;
    }

    private JFrame discardPileFrame(JPanel discardPile) {
        String title = discardPile == yourDiscardPile ? "Your discard pile" : "Opponen'ts discard pile";
        JFrame frame = new JFrame(title);
        Container contentPane = frame.getContentPane();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.X_AXIS));
        JScrollPane scrollPane = new JScrollPane(discardPile);
        scrollPane.setMaximumSize(new Dimension(500, 500));
        contentPane.add(scrollPane);
        frame.setMinimumSize(new Dimension(390, 0));
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        return frame;
    }

    private JPanel createDiscardPile() {
        JPanel discardPile = new JPanel();
        discardPile.setLayout(new BoxLayout(discardPile, BoxLayout.X_AXIS));
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
        leftPanel.add(yourInfoPanel(), BorderLayout.SOUTH);
        return leftPanel;
    }

    @SuppressWarnings("Duplicates")
    private JPanel yourInfoPanel() {
        JPanel yourInfoPanel = new JPanel();
        yourInfoPanel.setLayout(new BoxLayout(yourInfoPanel, BoxLayout.Y_AXIS));
        yourInfoPanel.add(yourDiscardPileButton());
        yourInfoPanel.add(endYourTurnButton()); //TODO: remove
        yourInfoPanel.add(createDrawButton());
        yourInfoPanel.add(createYourDeckLabel());
        yourInfoPanel.add(yourHandLabel());
        yourInfoPanel.add(yourStartingCharacter);
        return yourInfoPanel;
    }

    private JButton createDrawButton() {
        //TODO: update hand size, notify opponent... -> handling actions
        JButton drawButton = new JButton("Draw");
        drawButton.addActionListener(e -> {
            if (yourTurn) {
                draw();
                refresh();
            }
        });
        return drawButton;
    }

    private JPanel opponentInfoPanel() {
        JPanel opponentInfoPanel = new JPanel();
        opponentInfoPanel.setLayout(new BoxLayout(opponentInfoPanel, BoxLayout.Y_AXIS));
        opponentInfoPanel.add(opponentDiscardPileButton());
        opponentInfoPanel.add(opponentDeckLabel());
        opponentInfoPanel.add(opponentHandLabel());
        opponentInfoPanel.add(opponentStartingCharacter);
        return opponentInfoPanel;
    }

    private JLabel createYourDeckLabel() {
        yourDeckLabel =  new JLabel("Deck: " + 53);
        return yourDeckLabel;
    }

    private JLabel opponentDeckLabel() {
        opponentDeckLabel =  new JLabel("Deck: " + 53);
        return opponentDeckLabel;
    }

    private JLabel yourHandLabel() {
        yourHandLabel =  new JLabel("Hand: " + 7);
        return yourHandLabel;
    }

    private JLabel opponentHandLabel() {
        opponentHandLabel =  new JLabel("Hand: " + 7);
        return opponentHandLabel;
    }

    private JButton yourDiscardPileButton() {
        yourDiscardPileButton = new JButton("Discard pile");
        yourDiscardPileButton.addActionListener(e -> {
            yourDiscardPileFrame.repaint();
            yourDiscardPileFrame.pack();
            yourDiscardPileFrame.setVisible(true);
        });
        return yourDiscardPileButton;
    }

    private JButton opponentDiscardPileButton() {
        opponentDiscardPileButton = new JButton("Discard pile");
        opponentDiscardPileButton.addActionListener(e -> {
            opponentDiscardPileFrame.repaint();
            opponentDiscardPileFrame.pack();
            opponentDiscardPileFrame.setVisible(true);
        });
        return opponentDiscardPileButton;
    }

    private JLabel lastSpellPlayedLabel() {
        lastSpellPlayedLabel = new JLabel();
        return lastSpellPlayedLabel;
    }

    private JButton endYourTurnButton() {
        JButton button = new JButton("End your turn");
        button.addActionListener(e -> endYourTurn());
        return button;
    }

    private void endYourTurn() {
        endTurnCard.playCard();
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
        mainMessageLabel = new JLabel("Connecting to the server...");
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
        boardPanel.add(createPlayedCardsPanel(yourPlayedCard, yourLessonsLabels, yourCreaturesPanel), BorderLayout.SOUTH);
        boardPanel.add(createPlayedCardsPanel(opponentsCards, opponentsLessonsLabels, opponentCreaturesPanel), BorderLayout.NORTH);
        return boardPanel;
    }

    private JPanel createLessonPanel(Map<LessonType, JLabel> playersLessonsLabels) {
        JPanel opponentsLessonsPanel = new JPanel();
        opponentsLessonsPanel.setLayout(new BoxLayout(opponentsLessonsPanel, BoxLayout.X_AXIS));
        opponentsLessonsPanel.setOpaque(false);
        double scale = 1;
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

    private JPanel createPlayedCardsPanel(JPanel playerPlayedCards, Map<LessonType, JLabel> playerLessonsLabels, JPanel playerCreatures) {
        playerPlayedCards = new JPanel();
        playerPlayedCards.setOpaque(false);
        playerPlayedCards.setLayout(new BoxLayout(playerPlayedCards, BoxLayout.Y_AXIS));
        playerPlayedCards.setAlignmentY(Container.TOP_ALIGNMENT);
        playerPlayedCards.add(createLessonPanel(playerLessonsLabels));
        JPanel centeringPanel = new JPanel(new GridBagLayout());
        centeringPanel.add(playerCreatures);
        centeringPanel.setOpaque(false);
        centeringPanel.setBorder(BorderFactory.createEmptyBorder(50, 0, 50, 0));
        JScrollPane creaturesScrollPane = new JScrollPane(centeringPanel);
        creaturesScrollPane.getViewport().setOpaque(false);
        creaturesScrollPane.setOpaque(false);
        creaturesScrollPane.setAlignmentX(Container.CENTER_ALIGNMENT);
        creaturesScrollPane.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
        if (playerCreatures == yourCreaturesPanel) {
            playerPlayedCards.add(creaturesScrollPane, 0);
        } else {
            playerPlayedCards.add(creaturesScrollPane);
        }
        return playerPlayedCards;
    }

    private JScrollPane handPanel() {
        handPanel = new JPanel();
        handPanel.setLayout(new BoxLayout(handPanel, BoxLayout.X_AXIS));
        return new JScrollPane(handPanel, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
    }

    private void draw() {
        drawCard.playCard();
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
            cardImage = ImageIO.read(getClass().getResource("images/" + cardName + ".jpg"));
        } catch (IOException e) {
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
                    mainMessageLabel.setText("It's your opponent's turn");
                }
            }
        }
    }

    private void createOpponent(String opponentCharacterName) {
        opponentStartingCharacter = createCard(opponentCharacterName);
        opponentStartingCharacter.setImageScale(1.25);
        leftPanel.add(opponentInfoPanel(), BorderLayout.NORTH);
    }

    void waitFor(int i) {
        try {
            Thread.sleep(i);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void applyOpponentsCard() {
        String cardName = get("game/player" + opponentId + "/card" + (savedNbCardsPlayedByOpponent-1));
//        System.out.println("Opponent played: " + cardName);
        addMessage("Your opponent played: " + cardName);
        Card opponentsCard = createCard(cardName);
//        System.out.println("opponentsCard: " + opponentsCard);
        opponentsCard.applyOpponentPlayed();
        refresh();
//        beginYourTurn();
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

    void beginYourTurn() {
        savedNbCardsPlayedByOpponent = 0;
        put("game/player1/resetPlayedCards", "");
        put("game/player2/resetPlayedCards", "");
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
        savedNbCardsPlayedByOpponent = 0;
        put("game/player" + yourId + "/resetPlayedCards", "");
    }

    private boolean newCardFromOpponent() {
        int fetchedNbCardsPlayedByOpponent = fetchedNbCardsPlayedByOpponent();
        if (fetchedNbCardsPlayedByOpponent != savedNbCardsPlayedByOpponent) {
//            System.out.println("New card played by opponent");
            savedNbCardsPlayedByOpponent++;
            return true;
        } else {
            return false;
        }
    }

    private int fetchedNbCardsPlayedByOpponent() {
        return Integer.parseInt(get("game/player" + opponentId + "/nbCardsPlayed"));
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
        yourHandLabel.setText("Hand: " + handPanel.getComponents().length);
        opponentHandLabel.setText("Hand: " + opponentHandSize);
        yourDeckLabel.setText("Deck: " + yourDeck.size());
        opponentDeckLabel.setText("Deck: " + opponentDeck.size());
        yourDiscardPileButton.setText("Discard pile " + "(" + yourDiscardPile.getComponents().length + ")");
        opponentDiscardPileButton.setText("Discard pile " + "(" + opponentDiscardPile.getComponents().length + ")");
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
            Card lessonDiscarded = createCard(lessonType.toString());
            lessonDiscarded.setDisabled(true);
            opponentDiscardPile.add(lessonDiscarded);
        } else {
            totalPower.put(lessonType, totalPower.get(lessonType) -1);
            yourLessons.put(lessonType, yourLessons.get(lessonType) -1);
            getYourLessonsLabels().get(lessonType).setText(String.valueOf(yourLessons.get(lessonType)));
            if (yourLessons.get(lessonType) == 0 ) {
                getYourLessonsLabels().get(lessonType).setVisible(false);
            }
            Card lessonDiscarded = createCard(lessonType.toString());
            lessonDiscarded.setDisabled(true);
            yourDiscardPile.add(lessonDiscarded);
        }
        refresh();
    }

    public void addOpponentLesson(LessonType lessonType) {
        opponentsLessons.put(lessonType, opponentsLessons.get(lessonType) + 1);
        opponentsLessonsLabels.get(lessonType).setText(String.valueOf(opponentsLessons.get(lessonType)));
        opponentsLessonsLabels.get(lessonType).setVisible(true);
    }

    public ArrayList<Card> getAllCards() {
        ArrayList<Card> cards = new ArrayList<>();
        for(Component card: handPanel.getComponents()) {
            cards.add((Card) card);
        }
        for(Component card: yourCreaturesPanel.getComponents()) {
            cards.add((Card) card);
        }
        for(Component card: opponentCreaturesPanel.getComponents()) {
            cards.add((Card) card);
        }
        for(Component card: yourDiscardPile.getComponents()) {
            cards.add((Card) card);
        }
        for(Component card: opponentDiscardPile.getComponents()) {
            cards.add((Card) card);
        }
        cards.add(yourStartingCharacter);
        cards.add(opponentStartingCharacter);
        return cards;
    }

    public ArrayList<Card> getOpponentCreatures() {
        ArrayList<Card> cards = new ArrayList<>();
        for(Component card: opponentCreaturesPanel.getComponents()) {
            cards.add((Card) card);
        }
        return cards;
    }

    public static void main(String[] args) {

        Game game = new Game();

        game.connectToServer();
        game.waitForOpponent();

        game.mainMessageLabel.setText("Drawing hands, please wait...");
        while(game.getYourHandSize() < 7 || game.opponentHandSize < 7) {
            if (game.yourTurn) {
                game.drawHand();
                game.endYourTurn();
            } else {
                if(game.newCardFromOpponent()) {
                    game.applyOpponentsCard();
                }
            }
            game.waitFor(1200);
        }

        while(true) {
            game.waitFor(100);  // delay so that the yourTurn variable knows it changed
            while(!game.yourTurn) {
                if(game.newCardFromOpponent()) {
                    game.applyOpponentsCard();
                }
                game.waitFor(1200);
            }
        }
    }

    private void drawHand() {
        drawHandCard.playCard();
    }

    public String getOpponentTarget() {
        String target = null;
        boolean waiting = true;
        for (Card card: getAllCards()) {
            card.setDisabled(true);
        }
        while (waiting) {
            target = get("game/player" + opponentId + "/target");
            if (target != null && !target.equals("")) {
                refresh();
                waiting = false;
            }
            waitFor(1000);
        }
        for (Card card: getAllCards()) {
            card.setDisabled(false);
        }
        return target;
    }

    public void damageOpponent(int n) {
        if (n >= opponentDeck.size()) {
            //TODO: You won
        } else {
            String cards = get("game/player" + opponentId + "/popDeckCopy/" + n);
            for (String cardNbString: cards.split(",")) {
                Card card = opponentDeck.get(Integer.parseInt(cardNbString));
                card.setDisabled(true);
                opponentDiscardPile.add(card);
                opponentDeck.remove(card);
            }
        }
        refresh();
    }

    public void takeDamage(int n) {
        if (n >= yourDeck.size()) {
            //TODO: You lost
        } else {
            String cards = get("game/player" + yourId + "/popDeck/" + n);
            for (String cardNbString: cards.split(",")) {
                Card card = yourDeck.get(Integer.parseInt(cardNbString));
                card.setDisabled(true);
                yourDiscardPile.add(card);
                yourDeck.remove(card);
            }
        }
        refresh();
    }

    public int getYourHandSize() {
        return handPanel.getComponentCount();
    }
}
