
import javax.swing.*;
import java.awt.event.*;
import java.sql.*;

public class LoginPage extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginPage() {
        setTitle("Login");
        setSize(300, 150);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);

        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setBounds(20, 20, 80, 25);
        add(usernameLabel);

        usernameField = new JTextField();
        usernameField.setBounds(100, 20, 160, 25);
        add(usernameField);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(20, 50, 80, 25);
        add(passwordLabel);

        passwordField = new JPasswordField();
        passwordField.setBounds(100, 50, 160, 25);
        add(passwordField);

        JButton loginButton = new JButton("Login");
        loginButton.setBounds(100, 80, 80, 25);
        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                if (authenticateUser(username, password)) {
                    new MovieSelectionPage(username);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid credentials!");
                }
            }
        });
        add(loginButton);

        setVisible(true);
    }

    private boolean authenticateUser(String username, String password) {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/MovieBooking", "root", "")) {
            String query = "SELECT * FROM Users WHERE username = ? AND password = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public static void main(String[] args) {
        new LoginPage();
    }

    public static class TestConnection {
        public static void main(String[] args) {
            String url = "jdbc:mysql://localhost:3306/MovieBooking";
            String user = "root";
            String password = "";

            try {
                // Load MySQL JDBC Driver (optional for modern versions)
                Class.forName("com.mysql.cj.jdbc.Driver");

                // Establish connection
                Connection conn = DriverManager.getConnection(url, user, password);
                System.out.println("Connected to the database successfully!");

                // Close connection
                conn.close();
            } catch (ClassNotFoundException e) {
                System.out.println("MySQL JDBC Driver not found.");
                e.printStackTrace();
            } catch (SQLException e) {
                System.out.println("Database connection failed!");
                System.out.println("Error Message: " + e.getMessage());
                System.out.println("SQL State: " + e.getSQLState());
                System.out.println("Error Code: " + e.getErrorCode());
            }
        }
    }
}
