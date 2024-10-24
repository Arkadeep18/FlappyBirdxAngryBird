import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.Timer;
import javax.imageio.ImageIO;
import java.io.File;
import java.awt.Font;
import java.io.IOException;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.Clip;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class GamePanel extends JPanel implements ActionListener, KeyListener {
    // Bird properties
    private int birdY = 200;
    private int birdX = 100;
    private int birdWidth = 50;  // Adjust based on your image size
    private int birdHeight = 50;  // Adjust based on your image size
    private int velocityY = 0;

    private Image birdImage;  // Image for the bird
    private Image backgroundImage;

    // Pipe properties
    private int pipeX = 800;
    private int pipeY; // Bottom pipe's top y-coordinate
    private int pipeWidth = 200;
    private int gap = 50; // Gap between top and bottom pipes
    private Image topPipeImage; // Image for the top pipe
    private Image bottomPipeImage; // Image for the bottom pipe

    // Game properties
    private boolean gameOver = false;
    private Timer timer;

    // Score variable
    private int score = 0;

    public GamePanel() {
        // Initialize game settings
        timer = new Timer(10, this); // Timer calls 'actionPerformed' every 10 ms
        timer.start();

        addKeyListener(this);  // Add keyboard listener
        setFocusable(true);  // Needed for key events to work

        // Load the bird image
        loadImages();

        resetPipePosition(); // Initialize pipe position
    }

    // Method to play sound based on event type
    public void playSound(String eventType) {
        String soundFile = "";

        // Determine which sound file to use based on the event type
        switch (eventType) {
            case "jump":
                soundFile = "sounds/jump.wav"; // Path to jump sound file
                break;
            case "gameOver":
                soundFile = "sounds/game over.wav"; // Path to game over sound file
                break;
            default:
                System.out.println("Invalid event type: " + eventType);
                return; // Exit if the event type is invalid
        }

        try {
            File file = new File(soundFile);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start(); // Play the sound
        } catch (UnsupportedAudioFileException e) {
            System.out.println("Unsupported audio file format: " + soundFile);
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Error reading the audio file: " + soundFile);
            e.printStackTrace();
        } catch (LineUnavailableException e) {
            System.out.println("Audio line unavailable for file: " + soundFile);
            e.printStackTrace();
        }
    }

    // Load images
    private void loadImages() {
        try {
            birdImage = ImageIO.read(new File("images/AngryBird.png"));  // Bird image
            backgroundImage = ImageIO.read(new File("images/BackgroundMoon.jpg"));  // Background image
            topPipeImage = ImageIO.read(new File("images/topPillar.png")); // Top pipe image
            bottomPipeImage = ImageIO.read(new File("images/Bottom.png")); // Bottom pipe image
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Reset pipe position
    private void resetPipePosition() {
        pipeX = 800; // Reset pipe to right side of screen
        pipeY = 100 + (int) (Math.random() * (400 - gap)); // Randomize pipe position within bounds
    }

    // Paint/draw the game
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw the background image first
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);  // Draw background image
        } else {
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, 800, 600);  // Fallback background if image not found
        }

        // Now draw the bird
        if (birdImage != null) {
            g.drawImage(birdImage, birdX, birdY, birdWidth, birdHeight, this);
        } else {
            g.setColor(Color.RED);
            g.fillRect(birdX, birdY, birdWidth, birdHeight);  // Fallback if bird image not loaded
        }

        // Draw pipes using images
        if (topPipeImage != null && bottomPipeImage != null) {
            g.drawImage(topPipeImage, pipeX, pipeY - topPipeImage.getHeight(this), pipeWidth, topPipeImage.getHeight(this), this); // Top pipe
            g.drawImage(bottomPipeImage, pipeX, pipeY + gap, pipeWidth, bottomPipeImage.getHeight(this), this); // Bottom pipe
        } else {
            g.setColor(Color.GREEN);
            g.fillRect(pipeX, 0, pipeWidth, pipeY - gap); // Top pipe
            g.fillRect(pipeX, pipeY, pipeWidth, 600 - pipeY); // Bottom pipe
        }

        // Draw the score
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 24));
        g.drawString("Score: " + score, 20, 30); // Display the score at the top-left corner

        // If game is over, show a message
        if (gameOver) {
            g.setColor(Color.WHITE);
            // Larger font for "Game Over"
            g.setFont(new Font("Arial", Font.BOLD, 36));
            g.drawString("Game Over!", 300, 250);

            // Smaller font for "Press 'F' to Restart"
            g.setFont(new Font("Arial", Font.PLAIN, 16));
            g.drawString("Press 'F' to Restart", 320, 300);
        }
    }

    // Update game state every frame
    @Override
    public void actionPerformed(ActionEvent e) {
        if (!gameOver) {
            // Bird gravity
            velocityY += 1;
            birdY += velocityY;

            // Move pipes
            pipeX -= 3;  // Game speed
            if (pipeX + pipeWidth < 0) {
                resetPipePosition();  // Reset pipe position with new random Y
                score++; // Increment score when pipe is passed
            }

            // Collision detection with screen bounds (top and bottom of the window)
            if (birdY < 0 || birdY + birdHeight > 600) {
                gameOver = true;  // Bird hit the top or bottom of the screen
                playSound("gameOver"); // Play game over sound
            }

            // Log the sizes of the bird and pipes
            System.out.println("Bird width: " + birdWidth + ", Bird height: " + birdHeight);
            System.out.println("Pipe width: " + pipeWidth + ", PipeY: " + pipeY);

            // Check horizontal collision
            if (birdX + birdWidth > pipeX && birdX < pipeX + pipeWidth) {
                // Vertical collision check
                if (birdY < pipeY - topPipeImage.getHeight(this) || birdY + birdHeight > pipeY + gap) {
                    gameOver = true;  // Bird hit a pipe (either top or bottom)
                    playSound("gameOver"); // Play game over sound
                }
            }

            repaint();  // Redraw the screen
        }
    }

    // Key press events
    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE && !gameOver) {
            // Bird jump when space bar is pressed
            velocityY = -14;  // Jump strength
            playSound("jump"); // Play jump sound
        }
        if (e.getKeyCode() == KeyEvent.VK_F && gameOver) {
            // Restart game if 'F' is pressed after game over
            resetGame();
            playSound("gameOver"); // Play game over sound when restarting
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // No actions on key release in this simple game
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // No actions on key type in this simple game
    }

    // Reset game state after game over
    private void resetGame() {
        birdY = 200;  // Reset bird position
        velocityY = 0;  // Reset bird velocity
        resetPipePosition();  // Reset pipe position
        score = 0;  // Reset score
        gameOver = false;  // Set game over to false
    }
}
