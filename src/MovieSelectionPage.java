import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class MovieSelectionPage extends JFrame {
    private JComboBox<String> movieDropdown; // kept for existing flow
    private JTextArea movieDetails;
    private JButton bookButton;

    private JPanel cardsPanel;
    private JPanel detailsOverlay;
    private BackgroundPanel bg;
    private final Map<String, JButton> cardByTitle = new HashMap<>();

    public MovieSelectionPage(String username) {
        setTitle("Movie Selection");
        getContentPane().setBackground(new Color(10, 10, 20));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
       

        BufferedImage bgImg = loadBackground("background");
        bg = new BackgroundPanel(bgImg);
        bg.setLayout(new BorderLayout(12, 12));
        setContentPane(bg);

        JLabel header = new JLabel("Choose Your Movie", SwingConstants.CENTER);
        header.setForeground(new Color(245, 245, 255));
        header.setFont(new Font("SansSerif", Font.BOLD, 26));
        header.setBorder(BorderFactory.createEmptyBorder(18, 12, 10, 12));
        header.setOpaque(false);
        bg.add(header, BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout(14, 14));
        center.setOpaque(false);

        cardsPanel = new JPanel(new GridLayout(1, 0, 14, 14));
        cardsPanel.setOpaque(false);

        JScrollPane cardsScroll = new JScrollPane(cardsPanel);
        cardsScroll.setOpaque(false);
        cardsScroll.getViewport().setOpaque(false);
        cardsScroll.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        center.add(cardsScroll, BorderLayout.CENTER);

        detailsOverlay = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(15, 15, 25, 160));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                g2.setColor(new Color(120, 160, 255, 90));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 18, 18);
                g2.dispose();
            }
        };
        detailsOverlay.setOpaque(false);
        detailsOverlay.setLayout(new BorderLayout());
        detailsOverlay.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        movieDetails = new JTextArea();
        movieDetails.setEditable(false);
        movieDetails.setOpaque(false);
        movieDetails.setForeground(new Color(245, 245, 255));
        movieDetails.setFont(new Font("SansSerif", Font.PLAIN, 14));
        movieDetails.setLineWrap(true);
        movieDetails.setWrapStyleWord(true);

        JScrollPane detailsScroll = new JScrollPane(movieDetails);
        detailsScroll.setOpaque(false);
        detailsScroll.getViewport().setOpaque(false);
        detailsScroll.setBorder(BorderFactory.createEmptyBorder());

        detailsOverlay.add(detailsScroll, BorderLayout.CENTER);
        center.add(detailsOverlay, BorderLayout.EAST);
        center.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        bg.add(center, BorderLayout.CENTER);

        bookButton = createPrimaryButton("Book Tickets");
        bookButton.addActionListener(e -> {
            String selectedMovie = (String) movieDropdown.getSelectedItem();
            if (selectedMovie != null) {
                new SeatBookingPage(username, selectedMovie);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Please select a movie.");
            }
        });

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 14));
        bottom.setOpaque(false);
        bottom.add(bookButton);
        bg.add(bottom, BorderLayout.SOUTH);

        // Keep JComboBox for booking flow, but hide it visually.
        movieDropdown = new JComboBox<>();
        movieDropdown.setVisible(false);
        bg.add(movieDropdown, BorderLayout.WEST);

        loadMovies();

        setContentPane(bg);
        setSize(1400, 800);
setLocationRelativeTo(null);
        setVisible(true);
    }

    private JButton createPrimaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.BOLD, 15));
        btn.setFocusPainted(false);
        btn.setForeground(new Color(245, 245, 255));
        btn.setBackground(new Color(60, 120, 255));
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setPreferredSize(new Dimension(170, 40));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(new Color(78, 140, 255));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(new Color(60, 120, 255));
            }
        });

        return btn;
    }

    private void loadMovies() {
        cardsPanel.removeAll();
        cardByTitle.clear();
        movieDropdown.removeAllItems();

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/MovieBooking", "root", "")) {
            String query = "SELECT title FROM Movies";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                String title = rs.getString("title");
                movieDropdown.addItem(title);
                cardsPanel.add(createMovieCard(title));
            }

            revalidate();
            repaint();

            if (movieDropdown.getItemCount() > 0) {
                String initial = movieDropdown.getSelectedItem().toString();
                syncSelectionAndBackground(initial);
                displayMovieDetails(initial);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading movies.");
        }
    }

    private JButton createMovieCard(String title) {
        JButton card = new JButton();
        card.setLayout(new BorderLayout(8, 8));
        card.setPreferredSize(new Dimension(210, 320));
        card.setFocusPainted(false);
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel posterLabel = new JLabel();
        ImageIcon posterIcon = loadImageIcon(title + ".png");
        if (posterIcon != null) {
            posterLabel.setIcon(new ImageIcon(posterIcon.getImage().getScaledInstance(180, 220, Image.SCALE_SMOOTH)));
        }
        posterLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel nameLabel = new JLabel(title, SwingConstants.CENTER);
        nameLabel.setForeground(new Color(245, 245, 255));
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 14));

        JLabel hint = new JLabel("Click", SwingConstants.CENTER);
        hint.setForeground(new Color(160, 170, 210));
        hint.setFont(new Font("SansSerif", Font.PLAIN, 12));

        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBorder(BorderFactory.createLineBorder(new Color(120, 160, 255, 190), 2, true));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                card.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                syncSelectionAndBackground(title);
                displayMovieDetails(title);
            }
        });

        cardByTitle.put(title, card);

        JPanel inner = new JPanel(new BorderLayout(8, 8));
        inner.setOpaque(false);
        inner.add(posterLabel, BorderLayout.CENTER);
        inner.add(nameLabel, BorderLayout.SOUTH);

        JPanel overlay = new JPanel(new BorderLayout());
        overlay.setOpaque(false);
        overlay.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255, 30), 1, true));
        overlay.add(inner, BorderLayout.CENTER);
        overlay.add(hint, BorderLayout.NORTH);

        card.add(overlay, BorderLayout.CENTER);
        return card;
    }

    private void syncSelectionAndBackground(String title) {
        movieDropdown.setSelectedItem(title);
        BufferedImage img = loadBackground(title);
        bg.setBackgroundImage(img);

        for (Map.Entry<String, JButton> entry : cardByTitle.entrySet()) {
            String t = entry.getKey();
            JButton card = entry.getValue();
            if (t.equals(title)) {
                card.setBorder(BorderFactory.createLineBorder(new Color(255, 70, 120, 200), 2, true));
            } else {
                card.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
            }
        }
        repaint();
    }

    private void displayMovieDetails(String movieTitle) {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/MovieBooking", "root", "")) {
            String query = "SELECT * FROM Movies WHERE title=?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, movieTitle);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                movieDetails.setText(
                        "Title: " + rs.getString("title") + "\n" +
                                "Description: " + rs.getString("description") + "\n" +
                                "Showtime: " + rs.getString("showtime") + "\n" +
                                "Theater: " + rs.getString("theater") + "\n" +
                                "Available Seats: " + rs.getInt("availableSeats")
                );
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error displaying movie details.");
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
        private BufferedImage background;

        BackgroundPanel(BufferedImage background) {
            this.background = background;
        }

        void setBackgroundImage(BufferedImage background) {
            this.background = background;
            repaint();
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

            GradientPaint gp = new GradientPaint(0, 0, new Color(20, 24, 44, 60), 0, getHeight(), new Color(0, 0, 0, 180));
            g2.setPaint(gp);
            g2.fillRect(0, 0, getWidth(), getHeight());

            g2.dispose();
            super.paintComponent(g);
        }
    }
}

