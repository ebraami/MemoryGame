package MasterMind;


import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;

public class MemoryGameGUI {
    private JFrame frame;
    private JPanel mainPanel, gridPanel, difficultyPanel, endPanel;
    private JButton[] cardButtons;
    private JButton easyButton, mediumButton, hardButton, startButton;
    private ArrayList<Integer> cardOrder = new ArrayList<>();
    private int difficulty = 0; // Default difficulty not selected
    private int currentCard = 1;
    private Timer countdownTimer, delayTimer;
    private JLabel timerLabel;
    private int countdownTime;
    private boolean gameActive = false;

    // Constants
    private static final int EASY_LEVEL = 5;
    private static final int MEDIUM_LEVEL = 8;
    private static final int HARD_LEVEL = 12;
    private static final int TIMER_INTERVAL = 1000;
    private static final int DELAY_INTERVAL = 2000;
    private static final int BUTTON_SIZE = 60;
    private static final int FONT_SIZE = 26;
    private static final int TIMER_FONT_SIZE = 28;

    public MemoryGameGUI() {
        frame = new JFrame("Memory Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(700, 600);
        frame.setMinimumSize(new Dimension(500, 500));
        frame.setLayout(new CardLayout());
        showDifficultyScreen();
        frame.setVisible(true);
    }

    public void showDifficultyScreen() {
frame.getContentPane().removeAll();
        difficultyPanel = createPanelWithTitle("Memory Game", new Color(0x9fb0fa));

        JLabel selectLabel = new JLabel("Select Difficulty Level", SwingConstants.CENTER);
        selectLabel.setFont(new Font("Arial", Font.PLAIN, 28));

        easyButton = createDifficultyButton("Easy", new Color(0xebebeb), EASY_LEVEL, 5);
        mediumButton = createDifficultyButton("Medium", new Color(0xf9c0a0), MEDIUM_LEVEL, 8);
        hardButton = createDifficultyButton("Hard", new Color(0xf4a3ab), HARD_LEVEL, 12);

        startButton = createActionButton("Start", e -> showGameScreen());
        startButton.setEnabled(false); // Disabled initially

        difficultyPanel.add(selectLabel, createGBC(0, 1, 3, 1));
        difficultyPanel.add(easyButton, createGBC(0, 2, 1, 1));
        difficultyPanel.add(mediumButton, createGBC(1, 2, 1, 1));
        difficultyPanel.add(hardButton, createGBC(2, 2, 1, 1));
        difficultyPanel.add(startButton, createGBC(1, 3, 1, 1));

        frame.add(difficultyPanel);
        frame.revalidate();
        frame.repaint();
    }

    private JPanel createPanelWithTitle(String title, Color backgroundColor) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(backgroundColor);
        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 40));
        panel.add(titleLabel, createGBC(0, 0, 3, 1));
        return panel;
    }

    private JButton createDifficultyButton(String text, Color color, int level, int timer) {
        return createButton(text, color, 150, BUTTON_SIZE, e -> {
            setDifficulty(level, timer, color);
            startButton.setEnabled(true); // Enable the start button once a level is selected
            startButton.setBackground(new Color(0xc7a3da));
        });
    }

    private JButton createActionButton(String text, ActionListener action) {
        return createButton(text, null, 200, BUTTON_SIZE, action);
    }

    private JButton createButton(String text, Color backgroundColor, int width, int height, ActionListener action) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, FONT_SIZE));
        if (backgroundColor != null) button.setBackground(backgroundColor);
        button.setPreferredSize(new Dimension(width, height));
        button.setBorder(BorderFactory.createRaisedBevelBorder());
        button.addActionListener(action);
        return button;
    }

    private GridBagConstraints createGBC(int x, int y, int width, int height) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = width;
        gbc.gridheight = height;
        gbc.insets = new Insets(10, 10, 10, 10);
        return gbc;
    }

    public void setDifficulty(int level, int timer, Color backgroundColor) {
        difficulty = level;
        countdownTime = timer;
        frame.getContentPane().setBackground(backgroundColor);
        resetButtonBorders();
        switch (level) {
            case EASY_LEVEL:
                easyButton.setBorder(new LineBorder(Color.BLACK, 3));
                break;
            case MEDIUM_LEVEL:
                mediumButton.setBorder(new LineBorder(Color.BLACK, 3));
                break;
            case HARD_LEVEL:
                hardButton.setBorder(new LineBorder(Color.BLACK, 3));
                break;
        }
    }

    private void resetButtonBorders() {
        easyButton.setBorder(BorderFactory.createRaisedBevelBorder());
        mediumButton.setBorder(BorderFactory.createRaisedBevelBorder());
        hardButton.setBorder(BorderFactory.createRaisedBevelBorder());
    }

    public void showGameScreen() {
        frame.getContentPane().removeAll();
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(frame.getContentPane().getBackground());

        timerLabel = new JLabel("Numbers will hide in " + countdownTime + " seconds", SwingConstants.CENTER);
        timerLabel.setFont(new Font("Arial", Font.BOLD, TIMER_FONT_SIZE));
        mainPanel.add(timerLabel, BorderLayout.NORTH);

        gridPanel = new JPanel(new GridLayout(2, difficulty / 2, 10, 10));
        gridPanel.setBackground(new Color(0x9fb0fa));
        cardButtons = new JButton[difficulty];
        cardOrder.clear();
        for (int i = 1; i <= difficulty; i++) cardOrder.add(i);
        Collections.shuffle(cardOrder);

        for (int i = 0; i < difficulty; i++) {
            cardButtons[i] = new JButton(String.valueOf(cardOrder.get(i)));
            cardButtons[i].setFont(new Font("Arial", Font.BOLD, FONT_SIZE + 6));
            cardButtons[i].setBackground(new Color(220, 220, 220));
            cardButtons[i].setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createRaisedBevelBorder(),
                    BorderFactory.createLineBorder(Color.DARK_GRAY, 2)));
            cardButtons[i].setEnabled(false);
            final int index = i;
            cardButtons[i].addActionListener(e -> checkCard(index));
            gridPanel.add(cardButtons[i]);
            cardButtons[i].setBackground(frame.getContentPane().getBackground());
        }

        mainPanel.add(gridPanel, BorderLayout.CENTER);
        frame.add(mainPanel);
        frame.revalidate();
        frame.repaint();
        startCountdown();
    }

    public void startCountdown() {
        gameActive = false;
        countdownTimer = new Timer(TIMER_INTERVAL, e -> {
            countdownTime--;
            timerLabel.setText("Numbers will hide in " + countdownTime + " seconds");
            if (countdownTime <= 0) {
                countdownTimer.stop();
                hideCardNumbers();
            }
        });
        countdownTimer.start();
    }

    public void hideCardNumbers() {
        for (JButton button : cardButtons) {
            button.setText("?");
            button.setEnabled(true);
        }
        timerLabel.setText("Reveal the cards in order: 1 → 2 → 3 ...");
        gameActive = true;
    }

    public void checkCard(int index) {
        if (!gameActive) return;
        if (cardOrder.get(index) == currentCard) {
            cardButtons[index].setText(String.valueOf(currentCard));
            cardButtons[index].setBackground(new Color(144, 238, 144)); // Green for correct
            cardButtons[index].setEnabled(false);
            currentCard++;
            if (currentCard > difficulty) showEndScreen(true);
        } else {
            cardButtons[index].setBackground(Color.RED); // Red for incorrect
            gameActive = false;
            delayTimer = new Timer(DELAY_INTERVAL, e -> showEndScreen(false));
            delayTimer.setRepeats(false);
            delayTimer.start();
        }
    }

    public void showEndScreen(boolean success) {
        frame.getContentPane().removeAll();
        endPanel = new JPanel(new BorderLayout());
        endPanel.setBackground(success ? new Color(0x59d130) : new Color(0xf03131));

        JLabel endMessage = new JLabel(success ? "Congratulations!" : "Game Over!");
        endMessage.setFont(new Font("Arial", Font.BOLD, 50));
        endMessage.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(0x9fb0fa));
        JButton playAgain = createActionButton("Play Again", e -> showDifficultyScreen());
        playAgain.setBackground(new Color(0xc7a3da));
        JButton exit = createActionButton("Exit", e -> System.exit(0));
        exit.setBackground(new Color(0xc7a3da));


        buttonPanel.add(playAgain);
        buttonPanel.add(exit);
       

        endPanel.add(endMessage, BorderLayout.CENTER);
        endPanel.add(buttonPanel, BorderLayout.SOUTH);

        frame.add(endPanel);
        frame.revalidate();
        frame.repaint();
        currentCard = 1;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MemoryGameGUI::new);
    }
}

