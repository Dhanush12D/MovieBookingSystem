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
      getContentPane().setBackground(Color.BLACK);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
       

       BufferedImage bgImg = loadBackground("background");

bg = new BackgroundPanel(bgImg);

bg.setLayout(new BorderLayout(12, 12));

setContentPane(bg);

// ADD THIS BELOW ↓↓↓

JPanel overlay = new JPanel();

overlay.setBackground(new Color(0, 0, 0, 140));

overlay.setLayout(new BorderLayout());

overlay.setOpaque(true);

bg.add(overlay, BorderLayout.CENTER);

        JLabel header = new JLabel("Choose Your Movie", SwingConstants.CENTER);
        header.setForeground(new Color(245, 245, 255));
        header.setFont(new Font("SansSerif", Font.BOLD, 26));
        header.setBorder(BorderFactory.createEmptyBorder(18, 12, 10, 12));
        header.setOpaque(false);
        overlay.add(header, BorderLayout.NORTH);

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

        overlay.add(center, BorderLayout.CENTER);

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
        overlay.add(bottom, BorderLayout.SOUTH);

        // Keep JComboBox for booking flow, but hide it visually.
        movieDropdown = new JComboBox<>();
        movieDropdown.setVisible(false);
        overlay.add(movieDropdown, BorderLayout.WEST);

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
        btn.setBackground(new Color(180, 25, 35));
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setPreferredSize(new Dimension(170, 40));

        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(40, 80, 170), 1, true),
                BorderFactory.createEmptyBorder(10, 16, 10, 16)
        ));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(new Color(220, 40, 50));
                btn.setForeground(Color.WHITE);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(new Color(180, 25, 35));
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
        // Rounded dark surface with hover glow. Backend logic (click->sync/display) remains unchanged.
        JButton card = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();
                int arc = 18;

                // Base card gradient (dark cinematic)
             Color top = new Color(55, 10, 18);

                Color bottom = new Color(18, 5, 8);
                GradientPaint gp = new GradientPaint(0, 0, top, 0, h, bottom);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, w, h, arc, arc);

                // Subtle inner glow/border
                g2.setColor(new Color(255, 255, 255, 18));
                g2.drawRoundRect(0, 0, w - 1, h - 1, arc, arc);

                // Hover glow effect (outer soft stroke)
                if (getModel().isRollover()) {
                    g2.setColor(new Color(255, 0, 60, 140));
                    g2.setStroke(new BasicStroke(2f));
                    g2.drawRoundRect(2, 2, w - 5, h - 5, arc - 2, arc - 2);

                    g2.setColor(new Color(255, 40, 40, 90));
                    g2.setStroke(new BasicStroke(1f));
                    g2.drawRoundRect(4, 4, w - 9, h - 9, arc - 4, arc - 4);
                }
                    paintChildren(g);
                g2.dispose();

               
            }

            @Override
            protected void paintBorder(Graphics g) {
                // Custom painting handles the border/glow.
            }
        };

        card.setLayout(new BorderLayout(10, 10));
      card.setPreferredSize(new Dimension(220, 260)); 
        card.setFocusPainted(false);
        card.setOpaque(false); // we paint the surface in paintComponent
        card.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel posterLabel = new JLabel();
        ImageIcon posterIcon = loadImageIcon(title + ".png");
        if (posterIcon != null) {
            posterLabel.setIcon(new ImageIcon(posterIcon.getImage().getScaledInstance(178, 222, Image.SCALE_SMOOTH)));
        }
        posterLabel.setHorizontalAlignment(SwingConstants.CENTER);
        posterLabel.setVerticalAlignment(SwingConstants.TOP);

        posterLabel.setHorizontalAlignment(SwingConstants.CENTER);

        posterLabel.setVerticalAlignment(SwingConstants.TOP);

        posterLabel.setBorder(
            BorderFactory.createLineBorder(
            new Color(255,255,255,30),
        1
    )
);

        JLabel hint = new JLabel("Click", SwingConstants.CENTER);
        hint.setForeground(new Color(190, 200, 235));
        hint.setFont(new Font("SansSerif", Font.PLAIN, 12));

        JLabel nameLabel = new JLabel(title, SwingConstants.CENTER);
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 14));

        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                // Keep behavior same; hover glow handled by paintComponent rollover.
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                syncSelectionAndBackground(title);
                displayMovieDetails(title);
            }
        });

        cardByTitle.put(title, card);

        // Inner content panel with better spacing/alignment.
        JPanel inner = new JPanel(new BorderLayout(6, 10));
        inner.setOpaque(false);
        inner.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(hint, BorderLayout.NORTH);

        inner.add(top, BorderLayout.NORTH);
        inner.add(posterLabel, BorderLayout.CENTER);
        inner.add(nameLabel, BorderLayout.SOUTH);

        card.add(inner, BorderLayout.CENTER);
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

    g2.setColor(new Color(0, 0, 0, 70));
    g2.fillRect(0, 0, getWidth(), getHeight());

    /*GradientPaint blueWash = new GradientPaint(
            0, 0,
           new Color(40, 30, 80, 70),
            getWidth(), getHeight(),
            new Color(0, 0, 0, 0)
    );

    g2.setPaint(blueWash);
    g2.fillRect(0, 0, getWidth(), getHeight()); */

    GradientPaint redWash = new GradientPaint(
            0, getHeight(),
         new Color(255, 0, 40, 220),
            getWidth(), 0,
            new Color(0, 0, 0, 0)
    );

    g2.setPaint(redWash);
    g2.fillRect(0, 0, getWidth(), getHeight());
    g2.setColor(new Color(120, 0, 20, 90));
    g2.fillRect(0, 0, getWidth(), getHeight());

    g2.dispose();
}
    }
}

