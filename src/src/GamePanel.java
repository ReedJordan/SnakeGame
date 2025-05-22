import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import javax.swing.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class GamePanel extends JPanel{

    int SCREEN_WIDTH = 600;
    int SCREEN_HEIGHT = 600;
    int UNIT_SIZE = 25;
    int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT) / UNIT_SIZE;
    int DELAY = 90;
    int[] x = new int[GAME_UNITS];
    int[] y = new int[GAME_UNITS];
    int bodyParts = 5;
    int applesEaten;
    int appleX;
    int appleY;
    int shrinkAppleX = -1*UNIT_SIZE;
    int shrinkAppleY = -1*UNIT_SIZE;
    boolean haveShrink = true;
    int deathAppleX = -1*UNIT_SIZE;
    int deathAppleY = -1*UNIT_SIZE;
    boolean haveDeath = true;
    char direction = 'R';
    boolean gameGoing = false;
    boolean speedUp = true;
    int deathReason = -1;
    boolean canMove = true;
    Random random;
    ScheduledExecutorService executorService;

    public GamePanel() {
        random = new Random();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.DARK_GRAY);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
        startGame();
    }

    public void startGame() {
        newApple();
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
            canMove = true;
            if((applesEaten == 5) && haveShrink){
                newShrinkApple();
                haveShrink = false;
            }
            if((applesEaten >= 10) && (applesEaten % 2 == 0) && haveDeath){
                newDeathApple();
                haveDeath = false;
            }
            if((applesEaten >= 15) && speedUp){
                DELAY -=5;
                speedUp = false;
            }
        }
        repaint();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (gameGoing) {
            g.setColor(Color.gray);
            for (int i = 0; i < SCREEN_HEIGHT; i += 2*UNIT_SIZE) {
                for(int j = 0; j < SCREEN_WIDTH; j += 2*UNIT_SIZE){
                    g.fillRect(j, i, UNIT_SIZE, UNIT_SIZE);
                }
            }
            for (int h = UNIT_SIZE; h < SCREEN_HEIGHT; h += 2*UNIT_SIZE){
                for(int k = UNIT_SIZE; k < SCREEN_WIDTH; k += 2*UNIT_SIZE){
                    g.fillRect(k, h, UNIT_SIZE, UNIT_SIZE);
                }
            }
            g.setColor(Color.white);
            g.fillOval(deathAppleX, deathAppleY, UNIT_SIZE, UNIT_SIZE);
            g.setColor(Color.cyan);
            g.fillOval(shrinkAppleX, shrinkAppleY, UNIT_SIZE, UNIT_SIZE);
            g.setColor(Color.red);
            g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);

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
            g.setFont(new Font("American Typewriter", Font.BOLD, 40));
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString("Score: " + applesEaten, (SCREEN_WIDTH - metrics.stringWidth("Score: " + applesEaten)) / 2, g.getFont().getSize());
            if(applesEaten == 5 || applesEaten == 6){
                g.setFont(new Font("Times", Font.BOLD, 20));
                g.setColor(Color.cyan);
                String message = "Blue apple SHRINKS the snake!";
                g.drawString(message, (SCREEN_WIDTH / 4), 80);
            }
            if(applesEaten == 10 || applesEaten == 11){
                g.setFont(new Font("Times", Font.BOLD, 20));
                g.setColor(Color.white);
                g.drawString("White apple KILLS the snake!", (SCREEN_WIDTH/4), 80);
            }
        }else{
            gameOver(g);
        }
    }

    public void newApple() {
        appleX = random.nextInt((int) SCREEN_WIDTH / UNIT_SIZE) * UNIT_SIZE;
        appleY = random.nextInt((int) SCREEN_HEIGHT / UNIT_SIZE) * UNIT_SIZE;
    }

    public void newShrinkApple() {
        shrinkAppleX = random.nextInt((int) SCREEN_WIDTH / UNIT_SIZE) * UNIT_SIZE;
        shrinkAppleY = random.nextInt((int) SCREEN_HEIGHT / UNIT_SIZE) * UNIT_SIZE;
    }

    public void newDeathApple() {
        deathAppleX = random.nextInt((int) SCREEN_WIDTH / UNIT_SIZE) * UNIT_SIZE;
        while(Math.abs(deathAppleX - x[0]) < 5*UNIT_SIZE){
            deathAppleX = random.nextInt((int) SCREEN_WIDTH / UNIT_SIZE) * UNIT_SIZE;
        }
        deathAppleY = random.nextInt((int) SCREEN_HEIGHT / UNIT_SIZE) * UNIT_SIZE;
        while(Math.abs(deathAppleY - y[0]) < 5*UNIT_SIZE){
            deathAppleY = random.nextInt((int) SCREEN_WIDTH / UNIT_SIZE) * UNIT_SIZE;
        }
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
        if (((x[0] == appleX) && (y[0] == appleY))) {
            bodyParts++;
            applesEaten++;
            haveDeath = true;
            speedUp = true;
            newApple();
        }
        if ((x[0] == shrinkAppleX) && (y[0] == shrinkAppleY)) {
            bodyParts--;
            applesEaten++;
            haveDeath = true;
            speedUp = true;
            if (bodyParts == 0) {
                gameGoing = false;
                deathReason = 1;
            }
            newShrinkApple();
        }
        if ((x[0] == deathAppleX) && (y[0] == deathAppleY)){
            gameGoing = false;
            deathReason = 2;
        }
    }

    public void checkCollisions() {
        //checks if head collides with body
        for (int i = bodyParts; i > 0; i--) {
            if ((x[0] == x[i]) && (y[0] == y[i])) {
                gameGoing = false;
                deathReason = 3;
            }
        }
        //checks if head collides with wall
        if (x[0] < 0 || x[0] > (SCREEN_WIDTH - UNIT_SIZE) || y[0] < 0 || y[0] > (SCREEN_HEIGHT-UNIT_SIZE)){
            gameGoing = false;
            deathReason = 4;
        }
    }

    public void gameOver(Graphics g) {
        //Score
        g.setColor(Color.red);
        g.setFont(new Font("American Typewriter", Font.BOLD, 40));
        FontMetrics metrics1 = getFontMetrics(g.getFont());
        g.drawString("Score: " + applesEaten, (SCREEN_WIDTH - metrics1.stringWidth("Score: " + applesEaten)) / 2, g.getFont().getSize());

        //Game Over text
        g.setColor(Color.red);
        g.setFont(new Font("American Typewriter", Font.BOLD, 75));
        FontMetrics metrics2 = getFontMetrics(g.getFont());
        g.drawString("Game Over", (SCREEN_WIDTH - metrics2.stringWidth("Game Over")) / 2, SCREEN_HEIGHT / 2);

        //death reason
        g.setFont(new Font("American Typewriter", Font.BOLD, 30));
        String message = "";
        if(deathReason == 1){
            message = "You got too small!";
            g.drawString(message, (int)(SCREEN_HEIGHT/3.75), 350);
        }else if(deathReason == 2){
            message = "You ate something rotten!";
            g.drawString(message, (int)(SCREEN_HEIGHT/5.3571428571), 350);
        }else if(deathReason == 3){
            message = "Stop hitting yourself!";
            g.drawString(message, (SCREEN_HEIGHT/4), 350);
        }else if(deathReason == 4){
            message = "There's a wall there!";
            g.drawString(message, (SCREEN_HEIGHT/4), 350);
        }



    }

    public class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    if ((direction != 'R') && canMove) {
                        direction = 'L';
                        canMove = false;
                    }
                    break;
                case KeyEvent.VK_RIGHT:
                    if ((direction != 'L') && canMove) {
                        direction = 'R';
                        canMove = false;
                    }
                    break;
                case KeyEvent.VK_UP:
                    if ((direction != 'D') && canMove) {
                        direction = 'U';
                        canMove = false;
                    }
                    break;
                case KeyEvent.VK_DOWN:
                    if ((direction != 'U') && canMove) {
                        direction = 'D';
                        canMove = false;
                    }
                    break;
            }
        }
    }
}
