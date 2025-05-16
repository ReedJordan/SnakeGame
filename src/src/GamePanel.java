import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import javax.swing.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class GamePanel extends JPanel{

    int SCREEN_WIDTH = 800;
    int SCREEN_HEIGHT = 800;
    int UNIT_SIZE = 25;
    int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT) / UNIT_SIZE;
    int DELAY = 90;
    int[] x = new int[GAME_UNITS];
    int[] y = new int[GAME_UNITS];
    int bodyParts = 6;
    int applesEaten;
    int appleOneX;
    int appleOneY;
    int appleTwoX;
    int appleTwoY;
    int shrinkAppleX;
    int shrinkAppleY;
    int deathAppleX;
    int deathAppleY;
    char direction = 'R';
    boolean gameGoing = false;
    Random random;
    ScheduledExecutorService executorService;

    public GamePanel() {
        random = new Random();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.dark_gray);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
        startGame();
    }

    public void startGame() {
        newAppleOne();
        newAppleTwo();
        gameGoing = true;
        //timer = new Timer(DELAY, this);
        //timer.start();

        executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(this::gameLoop, 0, DELAY, TimeUnit.MILLISECONDS);
    }

    private void gameLoop(){
        if(gameGoing){
            move();
            checkApple();
            checkCollisions();
            if(applesEaten > 5){
                newShrinkApple();
            }
            if(applesEaten > 10){
                newDeathApple();
            }
        }
        repaint();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (gameGoing) {
            for (int i = 0; i < SCREEN_HEIGHT / UNIT_SIZE; i++) {
                g.drawLine(i * UNIT_SIZE, 0, i * UNIT_SIZE, SCREEN_HEIGHT);
                g.drawLine(0, i * UNIT_SIZE, SCREEN_WIDTH, i * UNIT_SIZE);
            }
            g.setColor(Color.white);
            g.fillOval(deathAppleX, deathAppleY, Unit_SIZE, UNIT_SIZE);
            g.setColor(Color.cyan);
            g.fillOval(shrinkAppleX, shrinkAppleY, UNIT_SIZE, UNIT_SIZE);
            g.setColor(Color.red);
            g.fillOval(appleOneX, appleOneY, UNIT_SIZE, UNIT_SIZE);
            g.fillOval(appleTwoX, appleTwoY, UNIT_SIZE, UNIT_SIZE);
            
            

            for (int i = 0; i < bodyParts; i++) {
                if (i == 0) {
                    g.setColor(Color.green);
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                } else {
                    g.setColor(new Color(45, 180, 0));
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                }
            }
            g.setColor(Color.red);
            g.setFont(new Font("Ink Free", Font.BOLD, 40));
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString("Score: " + applesEaten, (SCREEN_WIDTH - metrics.stringWidth("Score: " + applesEaten)) / 2, g.getFont().getSize());
        }else{
            gameOver(g);
        }
    }

    /**
     * public void draw(Graphics g) {
     * <p>
     * if (gameGoing) {
     * for (int i = 0; i < SCREEN_HEIGHT / UNIT_SIZE; i++) {
     * g.drawLine(i * UNIT_SIZE, 0, i * UNIT_SIZE, SCREEN_HEIGHT);
     * g.drawLine(0, i * UNIT_SIZE, SCREEN_WIDTH, i * UNIT_SIZE);
     * }
     * g.setColor(Color.red);
     * g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);
     * <p>
     * for (int i = 0; i < bodyParts; i++) {
     * if (i == 0) {
     * g.setColor(Color.green);
     * g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
     * } else {
     * g.setColor(new Color(45, 180, 0));
     * g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
     * }
     * }
     * g.setColor(Color.red);
     * g.setFont(new Font("Ink Free", Font.BOLD, 40));
     * FontMetrics metrics = getFontMetrics(g.getFont());
     * g.drawString("Score: " + applesEaten, (SCREEN_WIDTH - metrics.stringWidth("Score: " + applesEaten)) / 2, g.getFont().getSize());
     * } else {
     * gameOver(g);
     * }
     * }
     **/

    public void newAppleOne() {
        appleOneX = random.nextInt((int) SCREEN_WIDTH / UNIT_SIZE) * UNIT_SIZE;
        appleOneY = random.nextInt((int) SCREEN_HEIGHT / UNIT_SIZE) * UNIT_SIZE;
    }

    public void newAppleTwo() {
        appleTwoX = random.nextInt((int) SCREEN_WIDTH / UNIT_SIZE) * UNIT_SIZE;
        appleTwoY = random.nextInt((int) SCREEN_HEIGHT / UNIT_SIZE) * UNIT_SIZE;
    }

    public void newShrinkApple() {
        shrinkAppleX = random.nextInt((int) SCREEN_WIDTH / UNIT_SIZE) * UNIT_SIZE;
        shrinkAppleY = random.nextInt((int) SCREEN_HEIGHT / UNIT_SIZE) * UNIT_SIZE;
    }

    public void newDeathApple() {
        deathAppleX = random.nextInt((int) SCREEN_WIDTH / UNIT_SIZE) * UNIT_SIZE;
        deathAppleY = random.nextInt((int) SCREEN_HEIGHT / UNIT_SIZE) * UNIT_SIZE;
    }

    public void move() {
        for (int i = bodyParts; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }

        switch (direction) {
            case 'U':
                y[0] = y[0] - UNIT_SIZE;
                break;
            case 'D':
                y[0] = y[0] + UNIT_SIZE;
                break;
            case 'L':
                x[0] = x[0] - UNIT_SIZE;
                break;
            case 'R':
                x[0] = x[0] + UNIT_SIZE;
                break;
        }

    }

    public void checkApple() {
        if (((x[0] == appleOneX) && (y[0] == appleOneY))) {
            bodyParts++;
            applesEaten++;
            newAppleOne();
        } else if ((x[0] == appleTwoX) && (y[0] == appleTwoY)){
            bodyParts++;
            applesEaten++;
            newAppleTwo();
        } else if ((x[0] == shrinkAppleX) && (y[0] == shrinkAppleY)){
            bodyParts--;
            applesEaten++;
            if(bodyParts == 0){
                gameGoing = false;
            }
            newShrinkApple();
        } else if ((x[0] == deathAppleX) && (y[0] == deathAppleY)){
            gameGoing = false;
        }
    }

    public void checkCollisions() {
        //checks if head collides with body
        for (int i = bodyParts; i > 0; i--) {
            if ((x[0] == x[i]) && (y[0] == y[i])) {
                gameGoing = false;
            }
        }
        //checks if head collides with wall
        if (x[0] < 0 || x[0] > SCREEN_WIDTH || y[0] < 0 || y[0] > SCREEN_HEIGHT){
            gameGoing = false;
        }
    }

    public void gameOver(Graphics g) {
        //Score
        g.setColor(Color.red);
        g.setFont(new Font("Ink Free", Font.BOLD, 40));
        FontMetrics metrics1 = getFontMetrics(g.getFont());
        g.drawString("Score: " + applesEaten, (SCREEN_WIDTH - metrics1.stringWidth("Score: " + applesEaten)) / 2, g.getFont().getSize());


        //Game Over text
        g.setColor(Color.red);
        g.setFont(new Font("Ink Free", Font.BOLD, 75));
        FontMetrics metrics2 = getFontMetrics(g.getFont());
        g.drawString("Game Over", (SCREEN_WIDTH - metrics2.stringWidth("Game Over")) / 2, SCREEN_HEIGHT / 2);
    }

    /*
    public void actionPerformed(ActionEvent e) {
        if (gameGoing) {
            move();
            checkApple();
            checkCollisions();
        }
        repaint();
    }
    */

    public class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    if (direction != 'R') {
                        direction = 'L';
                    }
                    break;
                case KeyEvent.VK_RIGHT:
                    if (direction != 'L') {
                        direction = 'R';
                    }
                    break;
                case KeyEvent.VK_UP:
                    if (direction != 'D') {
                        direction = 'U';
                    }
                    break;
                case KeyEvent.VK_DOWN:
                    if (direction != 'U') {
                        direction = 'D';
                    }
                    break;
            }
        }
    }

    /*
    public static void main(String[] args){
        JFrame frame = new JFrame("Snake");
        GamePanel gamePanel = new GamePanel();
        frame.add(gamePanel);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
     */
}
