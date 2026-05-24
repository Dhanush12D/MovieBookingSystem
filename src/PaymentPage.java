import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class PaymentPage extends JFrame {
    private JTextField cardNumberField;
    private JTextField expiryDateField;
    private JTextField cvvField;
    private JButton payButton;
    private String movieTitle;
    private int totalSeats;

    public PaymentPage(String username, String movieTitle, int totalSeats) {
        this.movieTitle = movieTitle;
        this.totalSeats = totalSeats;

        setTitle("Payment - " + movieTitle);
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(5, 2));

        add(new JLabel("Card Number:"));
        cardNumberField = new JTextField();
        add(cardNumberField);

        add(new JLabel("Expiry Date (MM/YY):"));
        expiryDateField = new JTextField();
        add(expiryDateField);

        add(new JLabel("CVV:"));
        cvvField = new JTextField();
        add(cvvField);

        payButton = new JButton("Pay");
        payButton.addActionListener(e -> processPayment(username));
        add(payButton);

        setVisible(true);
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
}
