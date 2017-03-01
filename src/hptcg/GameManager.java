package hptcg;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

@SuppressWarnings("Duplicates")
public class GameManager {

    //        public static String serverUrl = "http://hptcg-server.herokuapp.com/";
    public static String serverUrl = "http://localhost:8080/";
    private final JFrame frame;

    private JPanel buttonPanel;

    public static void main(String[] args) {
        new GameManager();
    }

    public GameManager() {
        frame = new JFrame("Game manager");
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        Container contentPane = frame.getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(createButtonPanel(), BorderLayout.CENTER);
        contentPane.add(createBottomButtons(), BorderLayout.SOUTH);
        refreshServerGames();
    }

    private JPanel createBottomButtons() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(0, 1));
        panel.add(createRefreshButton());
        panel.add(createCreateGameButton());
        return panel;
    }

    private JButton createCreateGameButton() {
        JButton button = new JButton("Create game");
        button.addActionListener(e -> {
            String name = JOptionPane.showInputDialog(frame, "Please enter a name");
            String id = put("gameManager/games", name);
            frame.dispose();
            new Thread(() -> new Game(Integer.parseInt(id)).play()).start();
        });
        return button;
    }

    private JButton createRefreshButton() {
        JButton button = new JButton("Refresh");
        button.addActionListener(e -> refreshServerGames());
        return button;
    }

    private JScrollPane createButtonPanel() {
        buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        return new JScrollPane(buttonPanel);
    }

    private void refreshServerGames() {
        String rawString = get("gameManager/games");
        buttonPanel.removeAll();
        if (rawString != null) {
            String[] gameStrings = rawString.split("-");
            for (String gameString: gameStrings) {
                String id = gameString.split(":")[0];
                String name = gameString.split(":")[1];
                JButton button = new JButton(name);
                button.addActionListener(e -> {
                    new Thread(() -> new Game(Integer.parseInt(id)).play()).start();
                });
                buttonPanel.add(button);
            }
        }
        frame.repaint();
        frame.pack();
    }

    public static String get(String uri) {
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

    public static String put(String uri, String payload) {
        String result = "";
        try {
            URL url = new URL(serverUrl + uri);
            HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
            httpCon.setDoOutput(true);
            httpCon.setRequestMethod("PUT");
            OutputStreamWriter out = new OutputStreamWriter(httpCon.getOutputStream());
            out.write(payload);
            out.close();
            BufferedReader br = new BufferedReader(new InputStreamReader(httpCon.getInputStream()));
            result = br.readLine();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

}
