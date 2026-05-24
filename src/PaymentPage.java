import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.sql.*;

public class PaymentPage extends JFrame {
    private JTextField cardNumberField;
    private JTextField expiryDateField;
    private JTextField cvvField;
    private JButton payButton;
    private String movieTitle;
    private int totalSeats;

    public PaymentPage(String username, String movieTitle, int totalSeats) {
        // Force layout after switching from SeatBookingPage so all payment fields render immediately.
        // (UI-only; keeps existing backend flow intact.)

        this.movieTitle = movieTitle;
        this.totalSeats = totalSeats;

        setTitle("Payment - " + movieTitle);
        setSize(920, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        BufferedImage bgImg = loadBackground(movieTitle);
        BackgroundPanel bg = new BackgroundPanel(bgImg);
        bg.setLayout(new GridBagLayout());

        JPanel card = createGlassCard();
        card.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("Complete Your Payment");
        title.setForeground(new Color(245, 245, 255));
        title.setFont(new Font("SansSerif", Font.BOLD, 26));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        card.add(title, gbc);

        JLabel cardIcon = new JLabel("[CARD]");

        cardIcon.setFont(new Font("SansSerif", Font.PLAIN, 22));
        cardIcon.setForeground(new Color(190, 210, 255));
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        card.add(cardIcon, gbc);

        JLabel numberLabel = new JLabel("Card Number");
        numberLabel.setForeground(new Color(210, 210, 230));
        numberLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        gbc.gridx = 1;
        gbc.gridy = 1;
        card.add(numberLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        cardNumberField = createStyledField();
        card.add(cardNumberField, gbc);

        JLabel expiryIcon = new JLabel("[EXP]");

        expiryIcon.setFont(new Font("SansSerif", Font.PLAIN, 20));
        expiryIcon.setForeground(new Color(190, 210, 255));
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        card.add(expiryIcon, gbc);

        JLabel expiryLabel = new JLabel("Expiry Date (MM/YY)");
        expiryLabel.setForeground(new Color(210, 210, 230));
        expiryLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        gbc.gridx = 1;
        gbc.gridy = 3;
        card.add(expiryLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        expiryDateField = createStyledField();
        card.add(expiryDateField, gbc);

        JLabel cvvIcon = new JLabel("[CVV]");

        cvvIcon.setFont(new Font("SansSerif", Font.PLAIN, 20));
        cvvIcon.setForeground(new Color(190, 210, 255));
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 1;
        card.add(cvvIcon, gbc);

        JLabel cvvLabel = new JLabel("CVV");
        cvvLabel.setForeground(new Color(210, 210, 230));
        cvvLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        gbc.gridx = 1;
        gbc.gridy = 5;
        card.add(cvvLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        cvvField = createStyledField();
        card.add(cvvField, gbc);

        gbc.gridy = 7;
        gbc.insets = new Insets(18, 10, 10, 10);
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;

        payButton = new JButton("Pay");
        payButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        payButton.setPreferredSize(new Dimension(220, 46));
        payButton.setFocusPainted(false);
        payButton.setForeground(new Color(245, 245, 255));
        payButton.setBackground(new Color(60, 120, 255));
        payButton.setOpaque(true);
        payButton.setBorderPainted(false);
        payButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                payButton.setBackground(new Color(78, 140, 255));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                payButton.setBackground(new Color(60, 120, 255));
            }
        });
        payButton.addActionListener(e -> processPayment(username));
        card.add(payButton, gbc);

        int pricePerSeat = 150;
        int totalAmount = totalSeats * pricePerSeat;

       JLabel summary = new JLabel(
        "<html>" +
        "Movie: " + movieTitle + "<br>" +
        "Seats Selected: " + totalSeats + "<br>" +
        "Price Per Seat: Rs." + pricePerSeat + "<br>" +
        "Total Amount: Rs." + totalAmount +
        "</html>"
);
        summary.setForeground(new Color(175, 185, 210));
        summary.setFont(new Font("SansSerif", Font.PLAIN, 12));
        gbc.gridy = 8;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        card.add(summary, gbc);


        bg.add(card);
        setContentPane(bg);
        setVisible(true);
    }

    private JTextField createStyledField() {
        JTextField f = new JTextField();
        f.setOpaque(false);
        f.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
        f.setForeground(new Color(245, 245, 255));
        f.setCaretColor(new Color(150, 220, 255));
        f.setFont(new Font("SansSerif", Font.PLAIN, 14));

        f.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                f.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(120, 160, 255), 2, true),
                        BorderFactory.createEmptyBorder(10, 14, 10, 14)
                ));
            }

            @Override
            public void focusLost(FocusEvent e) {
                f.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
            }
        });

        f.setPreferredSize(new Dimension(520, 44));
        JPanel wrapper = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, 120));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(new Color(120, 160, 255, 70));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        wrapper.setOpaque(false);
        wrapper.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        wrapper.setLayout(new BorderLayout());
        wrapper.add(f, BorderLayout.CENTER);

        // We must return the field itself, but the styling wrapper is not automatically applied.
        // So instead, use a custom panel as parent by setting the field's background painting directly.
        // For simplicity in Swing, rely on border + opaque(false) styling.
        return f;
    }

    private JPanel createGlassCard() {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, 70));
                g2.fillRoundRect(0, 14, getWidth(), getHeight(), 22, 22);
                g2.setColor(new Color(18, 18, 28, 200));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 22, 22);
                g2.setColor(new Color(120, 160, 255, 70));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 22, 22);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        return card;
    }

    private void processPayment(String username) {
        String cardNumber = cardNumberField.getText();
        String expiryDate = expiryDateField.getText();
        String cvv = cvvField.getText();

        if (cardNumber.isEmpty() || expiryDate.isEmpty() || cvv.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all payment details.");
            return;
        }

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/MovieBooking", "root", "")) {
            String query = "INSERT INTO Bookings (userId, movieId, seatsBooked) VALUES ((SELECT id FROM Users WHERE username=?), (SELECT id FROM Movies WHERE title=?), ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setString(2, movieTitle);
            stmt.setInt(3, totalSeats);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Payment Successful! Booking Confirmed.");
                dispose(); // Close payment page
                new ConfirmationPage(movieTitle, totalSeats); // Open confirmation page
            } else {
                JOptionPane.showMessageDialog(this, "Error processing booking. Please try again.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error processing payment.");
        }
    }

    private BufferedImage loadBackground(String movieName) {
        ImageIcon icon = loadImageIcon(movieName + ".png");
        if (icon == null) return null;
        Image img = icon.getImage();
        if (img instanceof BufferedImage) return (BufferedImage) img;

        BufferedImage bimg = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = bimg.createGraphics();
        g2.drawImage(img, 0, 0, null);
        g2.dispose();
        return bimg;
    }

    private ImageIcon loadImageIcon(String file) {
        java.net.URL url = getClass().getResource("/images/" + file);
        if (url == null) {
            url = getClass().getResource("images/" + file);
        }
        return url == null ? null : new ImageIcon(url);
    }

    private class BackgroundPanel extends JPanel {
        private final BufferedImage background;

        BackgroundPanel(BufferedImage background) {
            this.background = background;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (background != null) {
                g2.drawImage(background, 0, 0, getWidth(), getHeight(), null);
            } else {
                g2.setColor(new Color(10, 10, 18));
                g2.fillRect(0, 0, getWidth(), getHeight());
            }

            g2.setColor(new Color(0, 0, 0, 120));
            g2.fillRect(0, 0, getWidth(), getHeight());

            GradientPaint gp = new GradientPaint(0, 0, new Color(20, 24, 44, 60), 0, getHeight(), new Color(0, 0, 0, 190));
            g2.setPaint(gp);
            g2.fillRect(0, 0, getWidth(), getHeight());

            g2.dispose();
            super.paintComponent(g);
        }
    }
}

