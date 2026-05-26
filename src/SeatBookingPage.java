import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.sql.*;

public class SeatBookingPage extends JFrame {
    private JButton[][] seats; // Buttons representing seats
    private JLabel selectedSeatsLabel;
    private JButton proceedButton;
    private int selectedSeatsCount = 0;
    private int maxSeats = 5; // Maximum number of seats a user can book
    private String movieTitle;

    public SeatBookingPage(String username, String movieTitle) {
        this.movieTitle = movieTitle;

        setTitle("Seat Booking - " + movieTitle);
        setSize(920, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        BufferedImage bgImg = loadBackground(movieTitle);
        BackgroundPanel bg = new BackgroundPanel(bgImg);
        bg.setLayout(new BorderLayout());

        JLabel top = new JLabel("Select Your Seats", SwingConstants.CENTER);
        top.setFont(new Font("SansSerif", Font.BOLD, 26));
        top.setForeground(new Color(245, 245, 255));
        top.setBorder(BorderFactory.createEmptyBorder(18, 12, 10, 12));
        top.setOpaque(false);
        bg.add(top, BorderLayout.NORTH);

        JPanel centerWrap = new JPanel(new BorderLayout());
        centerWrap.setOpaque(false);
        centerWrap.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));

        JPanel glass = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(10, 10, 18, 140));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                g2.setColor(new Color(120, 160, 255, 80));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 18, 18);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        glass.setOpaque(false);
        glass.setLayout(new BorderLayout(0, 12));
        glass.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JPanel legend = new JPanel(new FlowLayout(FlowLayout.CENTER, 18, 0));
        legend.setOpaque(false);
        JLabel l1 = new JLabel("Available");
        l1.setForeground(new Color(0, 255, 140));
        l1.setFont(new Font("SansSerif", Font.BOLD, 13));
        JLabel b1 = new JLabel("  ");
        b1.setOpaque(true);
        b1.setBackground(new Color(0, 180, 80));
        b1.setPreferredSize(new Dimension(22, 14));
        JLabel l2 = new JLabel("Selected");
        l2.setForeground(new Color(255, 80, 80));
        l2.setFont(new Font("SansSerif", Font.BOLD, 13));
        JLabel b2 = new JLabel("  ");
        b2.setOpaque(true);
        b2.setBackground(Color.RED);
        b2.setPreferredSize(new Dimension(22, 14));
        JLabel l3 = new JLabel("Occupied");
       l3.setForeground(Color.WHITE);
       l3.setFont(new Font("SansSerif", Font.BOLD, 13));
        JLabel b3 = new JLabel("  ");
        b3.setOpaque(true);
        b3.setBackground(new Color(90, 90, 95));
        b3.setPreferredSize(new Dimension(22, 14));

        legend.add(b1);
        legend.add(l1);
        legend.add(b2);
        legend.add(l2);
        legend.add(b3);
        legend.add(l3);

        JPanel seatPanel = new JPanel(new GridLayout(5, 10, 8, 8)); // spacing
        seatPanel.setOpaque(false);
        seats = new JButton[5][10];

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 10; j++) {
                int seatNum = i * 10 + j + 1;
              seats[i][j] = new JButton(String.valueOf(seatNum));

                seats[i][j].setBackground(new Color(0, 220, 120));

                seats[i][j].setForeground(Color.BLACK);

                seats[i][j].setFocusPainted(false);

                seats[i][j].setOpaque(true);

                seats[i][j].setMargin(new Insets(0,0,0,0));

                seats[i][j].setFont(
                     new Font("SansSerif", Font.BOLD, 12)
            );

        seats[i][j].setCursor(
        Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
);

seats[i][j].setBorder(
        BorderFactory.createLineBorder(
                new Color(255,255,255,40),
                1,
                true
        )
);

                seats[i][j].addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        JButton seatButton = (JButton) e.getSource();
                       if (seatButton.getBackground().equals(new Color(0, 220, 120))) {
                            if (selectedSeatsCount < maxSeats) {
                               seatButton.setBackground(new Color(220, 40, 40));// Selected seats turn red
                                seatButton.setForeground(Color.WHITE);
                                seatButton.setBorder(BorderFactory.createLineBorder(new Color(255, 60, 120, 200), 2, true));
                                selectedSeatsCount++;
                                updateSelectedSeats();
                            } else {
                                JOptionPane.showMessageDialog(null, "You can only book up to " + maxSeats + " seats.");
                            }
                        }else if (seatButton.getBackground().equals(new Color(220, 40, 40))){
                           seatButton.setBackground(new Color(0, 220, 120));// Deselect seat
                            seatButton.setForeground(Color.BLACK);
                            seatButton.setBorder(BorderFactory.createLineBorder(new Color(0, 180, 80, 160), 1, true));
                            selectedSeatsCount--;
                            updateSelectedSeats();
                        }
                    }
                });
                seatPanel.add(seats[i][j]);
            }
        }

        selectedSeatsLabel = new JLabel("Selected Seats: 0", SwingConstants.CENTER);
        selectedSeatsLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        selectedSeatsLabel.setForeground(new Color(245, 245, 255));
        selectedSeatsLabel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        proceedButton = new JButton("Proceed to Payment");
        proceedButton.setFont(new Font("SansSerif", Font.BOLD, 15));
        proceedButton.setFocusPainted(false);
        proceedButton.setForeground(new Color(245, 245, 255));
        proceedButton.setBackground(new Color(180, 25, 35));
        proceedButton.setOpaque(true);
        proceedButton.setBorderPainted(false);
        proceedButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                proceedButton.setBackground(new Color(220, 40, 50));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                proceedButton.setBackground(new Color(180, 25, 35));
            }
        });
        proceedButton.addActionListener(e -> proceedToPayment(username));

     JLabel screen = new JLabel("SCREEN", SwingConstants.CENTER);

screen.setForeground(Color.BLACK);

screen.setFont(
        new Font("SansSerif", Font.BOLD, 14)
);

screen.setBorder(
        BorderFactory.createEmptyBorder(5,0,5,0)
);

screen.setOpaque(true);

screen.setPreferredSize(new Dimension(120, 28));

JPanel topSection = new JPanel(new BorderLayout());

topSection.setOpaque(false);

topSection.add(legend, BorderLayout.NORTH);

topSection.add(screen, BorderLayout.SOUTH);

glass.add(topSection, BorderLayout.NORTH);

glass.add(seatPanel, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);
        bottom.add(selectedSeatsLabel, BorderLayout.NORTH);
        bottom.add(proceedButton, BorderLayout.CENTER);
        glass.add(bottom, BorderLayout.SOUTH);

        centerWrap.add(glass, BorderLayout.CENTER);
        bg.add(centerWrap, BorderLayout.CENTER);

        loadSeatAvailability();

        setContentPane(bg);
        setVisible(true);
    }

    private void updateSelectedSeats() {
        selectedSeatsLabel.setText("Selected Seats: " + selectedSeatsCount);
    }

    private void loadSeatAvailability() {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/MovieBooking", "root", "")) {
            String query = "SELECT occupied_seats FROM Movies WHERE title=?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, movieTitle);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String occupiedSeats = rs.getString("occupied_seats");
                if (occupiedSeats != null && !occupiedSeats.isEmpty()) {
                    String[] occupiedSeatArray = occupiedSeats.split(",");
                    for (String seat : occupiedSeatArray) {
                        int seatNumber = Integer.parseInt(seat.trim());
                        int row = (seatNumber - 1) / 10;
                        int col = (seatNumber - 1) % 10;
                        seats[row][col].setBackground(new Color(70,70,70)); // Occupied seats are gray
                        seats[row][col].setForeground(new Color(230, 230, 235));
                        seats[row][col].setBorder(BorderFactory.createLineBorder(new Color(120, 120, 128, 180), 1, true));
                        seats[row][col].setEnabled(false); // Disable button for occupied seats
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading seat availability.");
        }
    }

    private void proceedToPayment(String username) {
        if (selectedSeatsCount > 0) {
            new PaymentPage(username, movieTitle, selectedSeatsCount);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Please select at least one seat.");
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

    // Dark overlay
    g2.setColor(new Color(0, 0, 0, 110));

    g2.fillRect(0, 0, getWidth(), getHeight());

    // Red cinematic glow
    GradientPaint redGlow = new GradientPaint(
            0,
            0,
            new Color(120, 0, 20, 90),

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

