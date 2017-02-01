package hptcg;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class BoardPanel extends JPanel {

    public BoardPanel() {
        setLayout(new BorderLayout());
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        try {
            g.drawImage(new ImageIcon(ImageIO.read(new File("src/hptcg/images/castle.jpg"))).getImage(), 0, 0, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
