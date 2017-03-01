package hptcg;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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
    public JPanel yourHand;
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
    Map<LessonType, Integer> totalPower;
    JLabel lastSpellPlayedLabel;
    JPanel yourDiscardPile;
    JPanel opponentDiscardPile;
    JFrame yourDiscardPileFrame;
    JFrame opponentDiscardPileFrame;
    JPanel yourCreaturesPanel;
    JPanel opponentCreaturesPanel;
    Card endTurnCard;
    Draw drawCard;
    JButton yourDiscardPileButton;
    JButton opponentDiscardPileButton;
    JLabel yourHandLabel;
    JLabel opponentHandLabel;
    JLabel yourDeckLabel;
    JLabel opponentDeckLabel;
    Map<Integer, Card> yourDeck;
    Map<Integer, Card> opponentDeck;
    private JScrollPane messageScrollPane;
    public int yourActionsLeft = 0;
    private JLabel yourActionsLeftLabel;
    private boolean handsDrawn;
    Card creatureDamagePhase;
    public JFrame opponentHandFrame;
    public Container opponentHand;
    private JButton opponentHandButton;
    private boolean canSeeOpponentHand = false;
    public int gameId;

    public Game(int gameId) {
        this.gameId = gameId;
        cardsImageIcons = new Hashtable<>();
        endTurnCard  = new EndTurn(this);
        drawCard  = new Draw(this);
        drawHandCard  = new DrawHand(this);
        creatureDamagePhase = new CreatureDamagePhase(this);
        yourStartingCharacter = chooseStartingCharacter();
        opponentStartingCharacter = opponentStartingCharacter();
        yourActionsLeft = 2;
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
        deleteGameOnClose();
        frame.pack();
        frame.setVisible(true);
    }

    private void deleteGameOnClose() {
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                get("player" + getYourId() + "/play/QuitGame");
                waitFor(1000);
                GameManager.get("gameManager/delete/" + gameId);
                super.windowClosing(e);
            }
        });
    }

    private Map<Integer, Card> createYourDeck() {
        if (yourStartingCharacter.getCardName().equals("HermioneGranger")) {
            return DeckCollection.createHermioneStarterBaseSet(this);
        } else {
            return DeckCollection.createDracoStarterBaseSet(this);
        }
    }

    private Map<Integer, Card> createOpponentDeck() {
        if (opponentStartingCharacter.getCardName().equals("HermioneGranger")) {
            return DeckCollection.createHermioneStarterBaseSet(this);
        } else {
            return DeckCollection.createDracoStarterBaseSet(this);
        }
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
        frame.setLocationRelativeTo(null);
        Container contentPane = frame.getContentPane();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.X_AXIS));
        JScrollPane scrollPane = new JScrollPane(discardPile);
        scrollPane.setPreferredSize(new Dimension(700, 200));
        contentPane.add(scrollPane);
        frame.setResizable(false);
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
                "Starting Character",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                new String[]{"Hermione Granger", "Draco Malfoy"},
                "Hermione Granger");
        Character character = choice == 0 ? new HermioneGranger(this) : new DracoMalfoy(this);
        character.inPlay = true;
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
//        yourInfoPanel.add(endYourTurnButton());
        yourInfoPanel.add(createDrawButton());
        yourInfoPanel.add(createYourDeckLabel());
        yourInfoPanel.add(yourHandLabel());
        yourInfoPanel.add(yourActionsLabel());
        yourInfoPanel.add(createOpponentHandButton());
        yourInfoPanel.add(yourStartingCharacter);
        return yourInfoPanel;
    }

    private JButton createOpponentHandButton() {
        opponentHandFrame = new JFrame("Opponent's hand");
        opponentHandFrame.setLocationRelativeTo(null);
        opponentHandFrame.setResizable(false);
        opponentHandFrame.getContentPane();
        opponentHand = new JPanel();
        opponentHand.setLayout(new BoxLayout(opponentHand, BoxLayout.X_AXIS));
        JScrollPane scrollPane = new JScrollPane(opponentHand);
        scrollPane.setPreferredSize(new Dimension(700, 200));
        opponentHandFrame.getContentPane().add(scrollPane);
        opponentHandButton = new JButton("Opponent's hand");
        opponentHandButton.addActionListener(e -> {
            opponentHandFrame.repaint();
            opponentHandFrame.pack();
            opponentHandFrame.setVisible(true);
        });
        opponentHandButton.setVisible(false);
        return opponentHandButton;
    }

    private JLabel yourActionsLabel() {
        yourActionsLeftLabel = new JLabel("Actions left: " + 2);
        return yourActionsLeftLabel;
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
        yourDeckLabel =  new JLabel("Deck: " + 40);
        return yourDeckLabel;
    }

    private JLabel opponentDeckLabel() {
        opponentDeckLabel =  new JLabel("Deck: " + 40);
        return opponentDeckLabel;
    }

    private JLabel yourHandLabel() {
        yourHandLabel =  new JLabel("Hand: " + 0);
        return yourHandLabel;
    }

    private JLabel opponentHandLabel() {
        opponentHandLabel =  new JLabel("Hand: " + 0);
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
        endTurnCard.applyCardEffect();
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
        messageScrollPane = new JScrollPane(gameMessagesPanel);
        return messageScrollPane;
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
        yourHand = new JPanel();
        yourHand.setLayout(new BoxLayout(yourHand, BoxLayout.X_AXIS));
        return new JScrollPane(yourHand, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
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
        catch(IllegalArgumentException e) {
            System.out.println("No image for " + cardName);
            try {
                cardImage = ImageIO.read(getClass().getResource("images/Accio.jpg"));
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
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
            if(get("player" + opponentId + "/status").equals("connected")) {
                waiting = false;
                System.out.println("An opponent joined the game!");
                yourTurn = yourId == 1;
                waitFor(1000);
                String opponentCharacterName = get("player" + opponentId + "/startingCharacter");
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
        opponentStartingCharacter.inPlay = true;
        opponentStartingCharacter.setImageScale(1.25);
        opponentDeck = createOpponentDeck();
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
        String cardName = get("player" + opponentId + "/card" + (savedNbCardsPlayedByOpponent-1));
        Card opponentsCard = createCard(cardName);
        if (opponentsCard.realCard && !opponentsCard.inPlay) {
            addMessage("Opponent played: " + cardName);
        } else {
            addMessage(opponentsCard.opponentPlayedMessage);
        }
        opponentsCard.applyOpponentPlayed();
        refresh();
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
        if (handsDrawn) {
            draw();
        }
        yourActionsLeft = 2;
//        put("player1/resetPlayedCards", "");
//        put("player2/resetPlayedCards", "");
        System.out.println("Beginning your turn");
        yourTurn = true;
        mainMessageLabel.setText("It's your turn");
        if (yourCreaturesPanel.getComponents().length > 0) {
            creatureDamagePhase.playCard();
        }
        refresh();
    }

    private boolean connectToServer() {
        String connectionResult = get("join");
        if (connectionResult.equals("Game is full")) {
            JOptionPane.showMessageDialog(frame, "The game is full");
            frame.dispose();
            return false;
        } else {
            yourId = Integer.parseInt(connectionResult);
            System.out.println("You are player " + yourId);
            put("player" + yourId + "/startingCharacter", yourStartingCharacter.getCardName());
            opponentId = yourId == 1 ? 2 : 1;
            yourTurn = false;
            savedNbCardsPlayedByOpponent = 0;
        }
        return true;
    }

    private boolean newCardFromOpponent() {
        int fetchedNbCardsPlayedByOpponent = fetchedNbCardsPlayedByOpponent();
        if (fetchedNbCardsPlayedByOpponent != savedNbCardsPlayedByOpponent) {
            savedNbCardsPlayedByOpponent++;
            return true;
        } else {
            return false;
        }
    }

    private int fetchedNbCardsPlayedByOpponent() {
        return Integer.parseInt(get("player" + opponentId + "/nbCardsPlayed"));
    }

    public String get(String uri) {
        String result = "";
        String url = GameManager.serverUrl + "game" + gameId + "/" + uri;
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
            URL url = new URL(GameManager.serverUrl + "game" + gameId + "/" + uri);
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
        refresh();
        messageScrollPane.getVerticalScrollBar().setValue(messageScrollPane.getVerticalScrollBar().getMaximum());
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
        yourHand.remove(card);
    }

    public void refresh() {
        yourHandLabel.setText("Hand: " + yourHand.getComponents().length);
        opponentHandLabel.setText("Hand: " + opponentHand.getComponents().length);
        yourDeckLabel.setText("Deck: " + yourDeck.size());
        opponentDeckLabel.setText("Deck: " + opponentDeck.size());
        yourDiscardPileButton.setText("Discard pile " + "(" + yourDiscardPile.getComponents().length + ")");
        opponentDiscardPileButton.setText("Discard pile " + "(" + opponentDiscardPile.getComponents().length + ")");
        yourDiscardPileFrame.repaint();
        yourDiscardPileFrame.pack();
        opponentDiscardPileFrame.repaint();
        opponentDiscardPileFrame.pack();
        yourActionsLeftLabel.setText("Actions left: " + yourActionsLeft);
        if (canSeeOpponentHand) {
            opponentHandButton.setVisible(true);
        }
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
        for(Component card: yourHand.getComponents()) {
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

    public void play() {
        boolean connectionResult = connectToServer();
        if (connectionResult) {
            waitForOpponent();
            drawHands();

            while(true) {
                waitFor(100);  // delay so that the yourTurn variable knows it changed
                checkActionsLeft();
                while(!yourTurn) {
                    if(newCardFromOpponent()) {
                        applyOpponentsCard();
                    }
                    waitFor(1200);
                }
            }
        }
    }

    private void checkActionsLeft() {
        if (yourActionsLeft <= 0) {
            this.endYourTurn();
        }
    }

    private void drawHands() {
        mainMessageLabel.setText("Drawing hands, please wait...");
        while(getYourHandSize() < 7 || opponentHand.getComponents().length < 7) {
            if (yourTurn) {
                drawHand();
                endYourTurn();
            } else {
                if(newCardFromOpponent()) {
                    applyOpponentsCard();
                }
            }
            waitFor(1200);
        }
        handsDrawn = true;
    }

    private void drawHand() {
        drawHandCard.applyCardEffect();
    }

    public String getOpponentTarget(int nb) {
        String target = null;
        boolean waiting = true;
        for (Card card: getAllCards()) {
            card.setDisabled(true);
        }
        while (waiting) {
            target = get("player" + opponentId + "/target" + nb);
            if (target != null && !target.equals("")) {
                refresh();
                waiting = false;
            }
            waitFor(1000);
        }
        for (Card card: getAllCards()) {
            card.setDisabled(false);
        }
        addMessage("Target: " + target);
        return target;
    }

    public void damageOpponent(int n) {
        if (n >= opponentDeck.size()) {
            //TODO: You won
            JOptionPane.showMessageDialog(frame, "You won the game!");
            System.exit(0);
        } else {
            String cards = get("player" + opponentId + "/popDeckCopy/" + n);
            for (String cardNbString: cards.split(",")) {
                Card card = opponentDeck.get(Integer.parseInt(cardNbString));
                card.setDisabled(true);
                opponentDiscardPile.add(card);
                opponentDeck.remove(Integer.parseInt(cardNbString));
            }
        }
        refresh();
    }

    public void takeDamage(int n) {
        if (n >= yourDeck.size()) {
            //TODO: You lost
            JOptionPane.showMessageDialog(frame, "You lost the game.");
            System.exit(0);
        } else {
            String cards = get("player" + yourId + "/popDeck/" + n);
            for (String cardNbString: cards.split(",")) {
                Card card = yourDeck.get(Integer.parseInt(cardNbString));
                card.setDisabled(true);
                yourDiscardPile.add(card);
                yourDeck.remove(Integer.parseInt(cardNbString));
            }
        }
        refresh();
    }

    public int getYourHandSize() {
        return yourHand.getComponentCount();
    }


}
