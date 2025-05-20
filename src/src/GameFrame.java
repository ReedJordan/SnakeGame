public class GameFrame(){
  public static void main(String[] args){
        //added this class in GitHub
    
        JFrame frame = new JFrame("Snake");
        GamePanel gamePanel = new GamePanel();
        frame.add(gamePanel);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
