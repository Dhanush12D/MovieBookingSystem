import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class ConfirmationPage extends JFrame {
    private float glowAlpha = 0f;

    public ConfirmationPage(String movieTitle, int totalSeats) {
        setTitle("Booking Confirmation");
        setSize(920, 520);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        BufferedImage bgImg = loadBackground(movieTitle);
        BackgroundPanel bg = new BackgroundPanel(bgImg);
        bg.setLayout(new GridBagLayout());

        JPanel overlay = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, 130));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 22, 22);
                g2.setColor(new Color(120, 160, 255, 80));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 22, 22);

                // glowing border
                int glow = (int) (glowAlpha * 180);
                g2.setColor(new Color(255, 70, 120, Math.min(255, glow)));
                g2.drawRoundRect(8, 8, getWidth() - 17, getHeight() - 17, 18, 18);

                g2.dispose();
                super.paintComponent(g);
            }
        };
        overlay.setOpaque(false);
        overlay.setLayout(new BorderLayout());
        overlay.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        JLabel big = new JLabel("SUCCESS: Booking Confirmed", SwingConstants.CENTER);

        big.setFont(new Font("SansSerif", Font.BOLD, 34));
        big.setForeground(new Color(245, 245, 255));
        big.setBorder(BorderFactory.createEmptyBorder(26, 10, 10, 10));

        JLabel info = new JLabel(
                "<html><center>Movie: <b>" + movieTitle + "</b><br>Tickets: <b>" + totalSeats + "</b></center></html>",
                SwingConstants.CENTER);
        info.setFont(new Font("SansSerif", Font.PLAIN, 16));
        info.setForeground(new Color(210, 220, 240));

        JLabel sub = new JLabel("Enjoy your show!", SwingConstants.CENTER);
        sub.setFont(new Font("SansSerif", Font.PLAIN, 13));
        sub.setForeground(new Color(170, 180, 205));
        sub.setBorder(BorderFactory.createEmptyBorder(0, 0, 18, 0));

        overlay.add(big, BorderLayout.NORTH);
        overlay.add(info, BorderLayout.CENTER);
        overlay.add(sub, BorderLayout.SOUTH);

        bg.add(overlay);
        setContentPane(bg);
        setVisible(true);

        // Timer-based glow animation (UI only)
        Timer t = new Timer(30, e -> {
            glowAlpha += 0.035f;
            if (glowAlpha > 1f) glowAlpha = 0f;
            overlay.repaint();
        });
        t.start();
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

            GradientPaint gp = new GradientPaint(0, 0, new Color(20, 24, 44, 70), 0, getHeight(), new Color(0, 0, 0, 200));
            g2.setPaint(gp);
            g2.fillRect(0, 0, getWidth(), getHeight());

            g2.dispose();
            super.paintComponent(g);
        }
    }
}

