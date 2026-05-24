
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.sql.*;

public class LoginPage extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginPage() {
        setTitle("Login");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(Color.BLACK);

        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setLayout(null);
        setContentPane(layeredPane);

        BufferedImage bgImg = loadBackgroundFromResource("background.png");
        BackgroundPanel background = new BackgroundPanel(bgImg);
        background.setBounds(0, 0, getWidth(), getHeight());
        layeredPane.add(background, JLayeredPane.DEFAULT_LAYER);

        JPanel overlay = createOverlayPanel();

        overlay.setBounds(0, 0, getWidth(), getHeight());
        layeredPane.add(overlay, JLayeredPane.PALETTE_LAYER);

        JPanel card = createGlassCard();
        card.setBounds(0, 0, 420, 360);
        layeredPane.add(card, JLayeredPane.MODAL_LAYER);

        // Center the card responsively
        layeredPane.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                background.setBounds(0, 0, layeredPane.getWidth(), layeredPane.getHeight());
                overlay.setBounds(0, 0, layeredPane.getWidth(), layeredPane.getHeight());

                int cw = card.getWidth();
                int ch = card.getHeight();
                int x = (layeredPane.getWidth() - cw) / 2;
                int y = (layeredPane.getHeight() - ch) / 2;
                card.setLocation(Math.max(0, x), Math.max(0, y));
            }
        });

        // Card layout
        card.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 12, 10, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;

        // Title section
        JLabel title = new JLabel(UIStrings.APP_TITLE, SwingConstants.CENTER);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 30));
        gbc.gridwidth = 2;
        card.add(title, gbc);

        JPanel decorative = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                int w = getWidth();
                int mid = w / 2;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(new Color(200, 35, 45));
                g2.fillRoundRect(mid - 70, 8, 140, 3, 3, 3);

                g2.setFont(new Font("Segoe UI", Font.BOLD, 18));
                g2.drawString("★", mid - 6, 26);
                g2.dispose();
            }
        };
        decorative.setOpaque(false);
        decorative.setPreferredSize(new Dimension(320, 40));
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        card.add(decorative, gbc);

        // Username row
        gbc.gridwidth = 1;
        gbc.gridy = 2;
        gbc.gridx = 0;
        JLabel userIcon = new JLabel("Username");
        userIcon.setForeground(Color.WHITE);
        userIcon.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        card.add(userIcon, gbc);

        gbc.gridx = 1;
        JPanel userFieldPanel = createFieldPanel();
        usernameField = new JTextField();
        usernameField.setOpaque(false);
        usernameField.setBorder(null);
        usernameField.setForeground(Color.WHITE);
        usernameField.setCaretColor(new Color(200, 35, 45));
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        usernameField.setToolTipText(UIStrings.USERNAME);
        userFieldPanel.add(usernameField, BorderLayout.CENTER);
        card.add(userFieldPanel, gbc);

        // Password row
        gbc.gridy = 3;
        gbc.gridx = 0;
        JLabel passIcon = new JLabel("Password");
        passIcon.setForeground(Color.WHITE);
        passIcon.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        card.add(passIcon, gbc);

        gbc.gridx = 1;
        JPanel passFieldPanel = createFieldPanel();
        passwordField = new JPasswordField();
        passwordField.setOpaque(false);
        passwordField.setBorder(null);
        passwordField.setForeground(Color.WHITE);
        passwordField.setCaretColor(new Color(200, 35, 45));
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordField.setToolTipText(UIStrings.PASSWORD);
        passFieldPanel.add(passwordField, BorderLayout.CENTER);
        card.add(passFieldPanel, gbc);

        // Button row
        gbc.gridy = 4;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(18, 12, 8, 12);

        JButton loginButton = createPrimaryButton(UIStrings.LOGIN);
        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                if (authenticateUser(username, password)) {
                    new MovieSelectionPage(username);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid credentials!");
                }
            }
        });
        card.add(loginButton, gbc);

        // Footer
        gbc.gridy = 5;
        gbc.insets = new Insets(10, 12, 10, 12);
        JLabel foot = new JLabel(UIStrings.SECURITY_FOOTER, SwingConstants.CENTER);
        foot.setForeground(new Color(220, 220, 240));
        foot.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        card.add(foot, gbc);


        // Initial position before first resize event
        card.setBounds((layeredPane.getWidth() - 420) / 2, (layeredPane.getHeight() - 360) / 2, 420, 360);
        setVisible(true);
    }


    private JPanel createGlassCard() {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();

                // Shadow
                g2.setColor(new Color(0, 0, 0, 65));
                g2.fillRoundRect(0, 12, w, h, 22, 22);

                // Glass background
                g2.setColor(new Color(15, 15, 20, 150));
                g2.fillRoundRect(0, 0, w, h, 22, 22);

                // Border
                g2.setColor(new Color(120, 160, 255, 70));
                g2.drawRoundRect(0, 0, w - 1, h - 1, 22, 22);

                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(22, 26, 22, 26));
        return card;
    }

    private JPanel createFieldPanel() {
        JPanel p = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth();
                int h = getHeight();

                g2.setColor(new Color(0, 0, 0, 0));
                g2.fillRect(0, 0, w, h);

                g2.setColor(new Color(255, 255, 255, 10));
                g2.fillRoundRect(0, 0, w - 1, h - 1, 14, 14);

                g2.setColor(new Color(120, 160, 255, 90));
                g2.drawRoundRect(0, 0, w - 1, h - 1, 14, 14);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(8, 10, 8, 10));
        return p;
    }

    private JButton createPrimaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFocusPainted(false);
        btn.setForeground(new Color(245, 245, 255));
        btn.setFont(new Font("SansSerif", Font.BOLD, 15));
        btn.setPreferredSize(new Dimension(160, 38));
        btn.setBorderPainted(false);

        btn.setBackground(new Color(180, 25, 35));
        btn.setOpaque(true);

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(new Color(220, 40, 50));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(new Color(180, 25, 35));
            }
        });

        return btn;
    }

    private BufferedImage loadBackgroundFromResource(String fileName) {
        java.net.URL url = getClass().getResource("/images/" + fileName);
        if (url == null) return null;
        ImageIcon icon = new ImageIcon(url);

        Image img = icon.getImage();
        if (img instanceof BufferedImage) {
            return (BufferedImage) img;
        }

        BufferedImage bimg = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = bimg.createGraphics();
        g2.drawImage(img, 0, 0, null);
        g2.dispose();
        return bimg;
    }

    // Kept for compatibility with the rest of the class.
    private BufferedImage loadBackground(String movieName) {
        ImageIcon icon = loadImageIcon(movieName + "background.png");
        if (icon == null) return null;
        Image img = icon.getImage();
        if (img instanceof BufferedImage) {
            return (BufferedImage) img;
        }
        BufferedImage bimg = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = bimg.createGraphics();
        g2.drawImage(img, 0, 0, null);
        g2.dispose();
        return bimg;
    }


    private ImageIcon loadImageIcon(String file) {
        java.net.URL url = getClass().getResource("/images/" + file);
        if (url == null) {
            // Fallback: try without leading slash (should still work in most IDE setups)
            url = getClass().getResource("images/" + file);
        }
        return url == null ? null : new ImageIcon(url);
    }

    private JPanel createOverlayPanel() {
        return new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // base semi-transparent black overlay
                g2.setColor(new Color(0, 0, 0, 120));
                g2.fillRect(0, 0, getWidth(), getHeight());

                // dark blue (left) -> dark red (right) glow
                GradientPaint blueGlow = new GradientPaint(
                        0, 0, new Color(10, 40, 120, 110),
                        getWidth(), 0, new Color(0, 0, 0, 0));
                g2.setPaint(blueGlow);
                g2.fillRect(0, 0, getWidth(), getHeight());

                GradientPaint redGlow = new GradientPaint(
                        0, 0, new Color(0, 0, 0, 0),
                        getWidth(), 0, new Color(120, 10, 25, 120));
                g2.setPaint(redGlow);
                g2.fillRect(0, 0, getWidth(), getHeight());

                // subtle vignette
                GradientPaint vignette = new GradientPaint(
                        0, 0, new Color(0, 0, 0, 40),
                        0, getHeight(), new Color(0, 0, 0, 220));
                g2.setPaint(vignette);
                g2.fillRect(0, 0, getWidth(), getHeight());

                g2.dispose();
               
            }
        };
    }

    private class BackgroundPanel extends JPanel {
        private final BufferedImage background;

        BackgroundPanel(BufferedImage background) {
            this.background = background;
        }

       @Override
protected void paintComponent(Graphics g) {
    super.paintComponent(g);

    Graphics2D g2 = (Graphics2D) g.create();

    g2.setRenderingHint(
        RenderingHints.KEY_INTERPOLATION,
        RenderingHints.VALUE_INTERPOLATION_BILINEAR
    );

    g2.setRenderingHint(
        RenderingHints.KEY_ANTIALIASING,
        RenderingHints.VALUE_ANTIALIAS_ON
    );

    if (background != null) {
        g2.drawImage(background, 0, 0, getWidth(), getHeight(), null);
    } else {
        g2.setColor(new Color(10, 10, 18));
        g2.fillRect(0, 0, getWidth(), getHeight());
    }

    g2.dispose();
}
    }



    private boolean authenticateUser(String username, String password) {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/MovieBooking", "root", "")) {
            String query = "SELECT * FROM Users WHERE username = ? AND password = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public static void main(String[] args) {
        new LoginPage();
    }

    public static class TestConnection {
        public static void main(String[] args) {
            String url = "jdbc:mysql://localhost:3306/MovieBooking";
            String user = "root";
            String password = "";

            try {
                // Load MySQL JDBC Driver (optional for modern versions)
                Class.forName("com.mysql.cj.jdbc.Driver");

                // Establish connection
                Connection conn = DriverManager.getConnection(url, user, password);
                System.out.println("Connected to the database successfully!");

                // Close connection
                conn.close();
            } catch (ClassNotFoundException e) {
                System.out.println("MySQL JDBC Driver not found.");
                e.printStackTrace();
            } catch (SQLException e) {
                System.out.println("Database connection failed!");
                System.out.println("Error Message: " + e.getMessage());
                System.out.println("SQL State: " + e.getSQLState());
                System.out.println("Error Code: " + e.getErrorCode());
            }
        }
    }
}

