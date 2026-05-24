import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
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
        setSize(500, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel seatPanel = new JPanel(new GridLayout(5, 10)); // 5 rows, 10 columns
        seats = new JButton[5][10];

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 10; j++) {
                seats[i][j] = new JButton((i * 10 + j + 1) + "");
                seats[i][j].setBackground(Color.GREEN); // Available seats are green
                seats[i][j].addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        JButton seatButton = (JButton) e.getSource();
                        if (seatButton.getBackground() == Color.GREEN) {
                            if (selectedSeatsCount < maxSeats) {
                                seatButton.setBackground(Color.RED); // Selected seats turn red
                                selectedSeatsCount++;
                                updateSelectedSeats();
                            } else {
                                JOptionPane.showMessageDialog(null, "You can only book up to " + maxSeats + " seats.");
                            }
                        } else if (seatButton.getBackground() == Color.RED) {
                            seatButton.setBackground(Color.GREEN); // Deselect seat
                            selectedSeatsCount--;
                            updateSelectedSeats();
                        }
                    }
                });
                seatPanel.add(seats[i][j]);
            }
        }

        selectedSeatsLabel = new JLabel("Selected Seats: 0");
        proceedButton = new JButton("Proceed to Payment");
        proceedButton.addActionListener(e -> proceedToPayment(username));

        add(seatPanel, BorderLayout.CENTER);
        add(selectedSeatsLabel, BorderLayout.NORTH);
        add(proceedButton, BorderLayout.SOUTH);

        loadSeatAvailability();

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
                        seats[row][col].setBackground(Color.GRAY); // Occupied seats are gray
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
}
