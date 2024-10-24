import javax.swing.JFrame;

public class MainClass {
    public static void main(String[] args) {
        // Create the frame (window)
        JFrame frame = new JFrame("Flappy Bird");
        GamePanel game = new GamePanel(); // Create the game panel
        
        // Add the game panel to the frame
        frame.add(game);
        frame.setSize(800, 600);  // Set window size
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Close app on clicking 'X'
        frame.setResizable(false);  // Prevent window resizing
        frame.setVisible(true);  // Show the frame
    }
}

