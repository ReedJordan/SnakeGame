import javax.swing.JFrame;

public class GameFrame extends JFrame {

    GameFrame(){
        JFrame frame = new JFrame("Snake");
        GamePanel gamePanel = new GamePanel();
        frame.add(gamePanel);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

    }

}