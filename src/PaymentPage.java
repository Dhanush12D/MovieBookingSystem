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
        card.setPreferredSize(new Dimension(500, 560));

        // Replace GridBagLayout-based form alignment with a simple vertical stack.
        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel title = new JLabel("Complete Your Payment");
        title.setForeground(new Color(245, 245, 255));
        title.setFont(new Font("SansSerif", Font.BOLD, 26));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(title);
        content.add(Box.createVerticalStrut(14));

        // ---------------- CARD NUMBER ----------------
        JLabel numberLabel = new JLabel("CARD NUMBER");
        numberLabel.setForeground(Color.WHITE);
        numberLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        numberLabel.setHorizontalAlignment(SwingConstants.CENTER);
        numberLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(numberLabel);
        content.add(Box.createVerticalStrut(6));

        cardNumberField = createStyledField();
        cardNumberField.setMaximumSize(new Dimension(Integer.MAX_VALUE, cardNumberField.getPreferredSize().height));
        cardNumberField.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(cardNumberField);
        content.add(Box.createVerticalStrut(10));

        // ---------------- EXPIRY ----------------
        JLabel expiryLabel = new JLabel("EXPIRY DATE (MM/YY)");
        expiryLabel.setForeground(Color.WHITE);
        expiryLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        expiryLabel.setHorizontalAlignment(SwingConstants.CENTER);
        expiryLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(expiryLabel);
        content.add(Box.createVerticalStrut(6));

        expiryDateField = createStyledField();
        expiryDateField.setMaximumSize(new Dimension(Integer.MAX_VALUE, expiryDateField.getPreferredSize().height));
        expiryDateField.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(expiryDateField);
        content.add(Box.createVerticalStrut(10));

        // ---------------- CVV ----------------
        JLabel cvvLabel = new JLabel("CVV");
        cvvLabel.setForeground(Color.WHITE);
        cvvLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        cvvLabel.setHorizontalAlignment(SwingConstants.CENTER);
        cvvLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(cvvLabel);
        content.add(Box.createVerticalStrut(6));

        cvvField = createStyledField();
        cvvField.setMaximumSize(new Dimension(Integer.MAX_VALUE, cvvField.getPreferredSize().height));
        cvvField.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(cvvField);
        content.add(Box.createVerticalStrut(12));

        // ---------------- PAY BUTTON ----------------
        payButton = new JButton("Pay Now");
        payButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        payButton.setPreferredSize(new Dimension(220, 46));
        payButton.setFocusPainted(false);
        payButton.setForeground(Color.WHITE);
        payButton.setBackground(new Color(180, 25, 35));
        payButton.setOpaque(true);
        payButton.setBorderPainted(false);
        payButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                payButton.setBackground(new Color(220, 40, 50));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                payButton.setBackground(new Color(180, 25, 35));
            }
        });
        payButton.addActionListener(
                e -> processPayment(username)
        );
        payButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(payButton);
        content.add(Box.createVerticalStrut(18));

        // ---------------- SUMMARY ----------------
        int pricePerSeat = 150;
        int totalAmount = totalSeats * pricePerSeat;

        JLabel summary = new JLabel(
                "<html><center>" +
                        "Movie: " + movieTitle + "<br>" +
                        "Seats Selected: " + totalSeats + "<br>" +
                        "Price Per Seat: Rs. " + pricePerSeat + "<br>" +
                        "Total Amount: Rs. " + totalAmount +
                        "</center></html>"
        );

        summary.setForeground(Color.WHITE);
        summary.setFont(new Font("SansSerif", Font.BOLD, 14));
        summary.setHorizontalAlignment(SwingConstants.CENTER);
        summary.setVerticalAlignment(SwingConstants.CENTER);
        summary.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(summary);

        // Vertically center the whole form stack inside the glass card.
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.add(Box.createVerticalGlue());
        card.add(content);
        card.add(Box.createVerticalGlue());

        ////////////////////////////////////////////////////////////////////////////////////////////////////////
        bg.add(card);
        setContentPane(bg);
        setVisible(true);
    }

    private JTextField createStyledField() {
        JTextField f = new JTextField();
        f.setOpaque(true);
        f.setBackground(new Color(20, 20, 30));
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
                f.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(255, 60, 80),
                                1,
                                true
                        ),
                        BorderFactory.createEmptyBorder(10, 14, 10, 14)
                ));
            }
        });

        f.setPreferredSize(new Dimension(420, 42));
        JPanel wrapper = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(20, 20, 30, 180));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(new Color(255, 40, 60, 90));
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
                g2.setColor(new Color(20, 20, 30, 180));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 22, 22);
                g2.setColor(new Color(255, 40, 60, 90));
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

                g2.drawImage(
                        background,
                        0,
                        0,
                        getWidth(),
                        getHeight(),
                        null
                );

            } else {

                g2.setColor(new Color(10, 10, 18));

                g2.fillRect(0, 0, getWidth(), getHeight());
            }

            // dark cinematic overlay
            g2.setColor(new Color(0, 0, 0, 150));

            g2.fillRect(0, 0, getWidth(), getHeight());

            // red cinematic glow
            GradientPaint redGlow = new GradientPaint(
                    0,
                    0,
                    new Color(120, 0, 20, 120),

                    getWidth(),
                    getHeight(),

                    new Color(0, 0, 0, 0)
            );

            g2.setPaint(redGlow);

            g2.fillRect(0, 0, getWidth(), getHeight());

            g2.dispose();
        }
    }
}

