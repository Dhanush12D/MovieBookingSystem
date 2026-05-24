import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class MovieSelectionPage extends JFrame {
    private JComboBox<String> movieDropdown;
    private JTextArea movieDetails;
    private JButton bookButton;

    public MovieSelectionPage(String username) {
        setTitle("Movie Selection");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        movieDropdown = new JComboBox<>();

        // 🛠 FIX: Initialize movieDetails before loadMovies()
        movieDetails = new JTextArea();
        movieDetails.setEditable(false);

        loadMovies();

        movieDropdown.addActionListener(e -> displayMovieDetails(movieDropdown.getSelectedItem().toString()));

        bookButton = new JButton("Book Tickets");
        bookButton.addActionListener(e -> {
            String selectedMovie = (String) movieDropdown.getSelectedItem();
            if (selectedMovie != null) {
                new SeatBookingPage(username, selectedMovie);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Please select a movie.");
            }
        });

        add(movieDropdown, BorderLayout.NORTH);
        add(new JScrollPane(movieDetails), BorderLayout.CENTER);
        add(bookButton, BorderLayout.SOUTH);

        setVisible(true);
    }


    private void loadMovies() {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/MovieBooking", "root", "")) {
            String query = "SELECT title FROM Movies";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                movieDropdown.addItem(rs.getString("title"));
            }

            if (movieDropdown.getItemCount() > 0) {
                displayMovieDetails(movieDropdown.getSelectedItem().toString());
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading movies.");
        }
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
}
