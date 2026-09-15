import javax.swing.*;
import java.awt.*;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.concurrent.CountDownLatch;

public class GameScreen {
    // swing
    private static JFrame frame;
    private static JTextArea logArea;
    private static JPanel buttonPanel;
    private static JPanel battlePanel;
    // java.lang (String)
    private static volatile String selection = null;
    private static Perso[] team1 = new Perso[3];
    private static Perso[] team2 = new Perso[3];
    // Library: java.lang (String)
    private static String player1Name = "Team 1";
    private static String player2Name = "Team 2";

    // Library: java.io (PrintStream)
    private static PrintStream originalOut = System.out;

    public static void init() {
        SwingUtilities.invokeLater(() -> {
            frame = new JFrame("Game Screen");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1200, 600);
            frame.setLayout(new BorderLayout());

            // Main container with battle display on left and logs on right
            JPanel mainPanel = new JPanel(new BorderLayout());

            // Battle display panel (left side)
            battlePanel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    drawBattle((Graphics2D) g);
                }
            };
            battlePanel.setBackground(new Color(50, 50, 50));
            battlePanel.setPreferredSize(new Dimension(500, 500));
            mainPanel.add(battlePanel, BorderLayout.WEST);

            // Right side panel with log and buttons
            JPanel rightPanel = new JPanel(new BorderLayout());

            logArea = new JTextArea();
            logArea.setEditable(false);
            JScrollPane scroll = new JScrollPane(logArea);
            scroll.setPreferredSize(new Dimension(900, 400));
            rightPanel.add(scroll, BorderLayout.CENTER);

            buttonPanel = new JPanel();
            // Use BorderLayout so the prompt sits on its own row and buttons appear below
            // it
            buttonPanel.setLayout(new BorderLayout());
            rightPanel.add(buttonPanel, BorderLayout.SOUTH);

            mainPanel.add(rightPanel, BorderLayout.CENTER);
            frame.add(mainPanel, BorderLayout.CENTER);

            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            // Redirect System.out to also append to the text area
            redirectSystemOut();
        });
    }

    private static void drawBattle(Graphics2D g) {
        int width = battlePanel.getWidth();

        // Draw team labels
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString(player1Name, 20, 30);
        g.drawString(player2Name, width - 120, 30);

        // Draw Team 1 (left side)
        drawTeamCharacters(g, team1, 50, 60, width / 2 - 50);

        // Draw Team 2 (right side)
        drawTeamCharacters(g, team2, width / 2 + 30, 60, width / 2 - 80);
    }

    private static void drawTeamCharacters(Graphics2D g, Perso[] team, int startX, int startY, int maxWidth) {
        int spacing = 140;
        for (int i = 0; i < team.length; i++) {
            if (team[i] != null) {
                int y = startY + (i * spacing);
                // Draw sprite placeholder
                if (team[i].hp[1] > 0) {
                    g.setColor(new Color(100, 150, 100));
                } else {
                    g.setColor(new Color(150, 100, 100));
                }
                g.fillRect(startX, y, 90, 100);
                g.setColor(Color.WHITE);
                g.drawRect(startX, y, 90, 100);

                // Draw character name below sprite
                g.setFont(new Font("Arial", Font.BOLD, 11));
                g.setColor(Color.WHITE);
                g.drawString(team[i].pseudo, startX + 3, y + 115);

                // Draw HP bar
                int barWidth = 85;
                int barHeight = 12;
                g.setColor(Color.DARK_GRAY);
                g.fillRect(startX + 2, y + 122, barWidth, barHeight);

                // HP bar fill
                float hpPercent = (float) team[i].hp[1] / team[i].hp[0];
                if (hpPercent > 0.3f) {
                    g.setColor(new Color(50, 200, 50));
                } else if (hpPercent > 0.1f) {
                    g.setColor(new Color(200, 200, 50));
                } else {
                    g.setColor(new Color(200, 50, 50));
                }
                g.fillRect(startX + 2, y + 122, (int) (barWidth * hpPercent), barHeight);
                g.setColor(Color.WHITE);
                g.drawRect(startX + 2, y + 122, barWidth, barHeight);

                // Draw HP text
                g.setFont(new Font("Arial", Font.PLAIN, 9));
                g.drawString(team[i].hp[1] + "/" + team[i].hp[0], startX + 35, y + 133);
            }
        }
    }

    public static void updateTeams(Perso[] t1, Perso[] t2) {
        team1 = t1;
        team2 = t2;
        SwingUtilities.invokeLater(() -> {
            battlePanel.repaint();
        });
    }

    public static void updateTeams(String p1Name, String p2Name, Perso[] t1, Perso[] t2) {
        player1Name = p1Name;
        player2Name = p2Name;
        team1 = t1;
        team2 = t2;
        SwingUtilities.invokeLater(() -> {
            battlePanel.repaint();
        });
    }

    private static void redirectSystemOut() {
        final PrintStream orig = originalOut;
        PrintStream tee = new PrintStream(new OutputStream() {
            StringBuilder sb = new StringBuilder();

            @Override
            public void write(int b) {
                orig.write(b);
                char c = (char) b;
                sb.append(c);
                if (c == '\n') {
                    final String line = sb.toString();
                    sb.setLength(0);
                    appendLog(line);
                }
            }
        }, true);
        System.setOut(tee);
        System.setErr(tee);
    }

    public static void appendLog(String text) {
        if (logArea == null)
            return;
        SwingUtilities.invokeLater(() -> {
            logArea.append(text);
            if (!text.endsWith("\n"))
                logArea.append("\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }

    // Clear the text area
    public static void clearLog() {
        if (logArea == null)
            return;
        SwingUtilities.invokeLater(() -> {
            logArea.setText("");
        });
    }

    // Show a set of buttons for options and wait for a selection (blocking)
    public static String askForSelection(String prompt, String[] options) {
        selection = null;
        CountDownLatch latch = new CountDownLatch(1);
        appendLog(prompt);
        SwingUtilities.invokeLater(() -> {
            buttonPanel.removeAll();
            // Prompt label at top
            JLabel lbl = new JLabel(prompt);
            buttonPanel.add(lbl, BorderLayout.NORTH);
            // Buttons go into a grid so they can appear on up to two rows and always fit
            int rows = Math.min(2, Math.max(1, options.length));
            int cols = (int) Math.ceil((double) options.length / rows);
            JPanel buttonsGrid = new JPanel(new GridLayout(rows, cols, 6, 6));
            for (String opt : options) {
                JButton b = new JButton(opt);
                b.addActionListener(e -> {
                    selection = opt;
                    latch.countDown();
                });
                buttonsGrid.add(b);
            }
            buttonPanel.add(buttonsGrid, BorderLayout.CENTER);
            buttonPanel.revalidate();
            buttonPanel.repaint();
        });

        try {
            latch.await();
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }

        final String res = selection;
        SwingUtilities.invokeLater(() -> {
            buttonPanel.removeAll();
            buttonPanel.revalidate();
            buttonPanel.repaint();
        });
        return res;
    }
}
