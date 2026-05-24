import javax.swing.*;

public class ConfirmationPage extends JFrame {
    public ConfirmationPage(String movieTitle, int totalSeats) {
        setTitle("Booking Confirmation");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel confirmationLabel = new JLabel("<html><center>Booking Confirmed!<br>Movie: " + movieTitle + "<br>Tickets: " + totalSeats + "</center></html>", SwingConstants.CENTER);

        add(confirmationLabel);

        setVisible(true);
    }
}
