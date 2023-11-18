// import javax.swing.*;
// import java.awt.*;
// import java.awt.event.ActionEvent;
// import java.awt.event.ActionListener;
// import java.util.HashMap;
// import java.util.Map;
// // import java.sql.Connection;
// // import java.sql.DriverManager;
// // import java.sql.PreparedStatement;

// public class UserAuthenticationApp {

//     private JFrame frame;
//     private Map<String, String> users = new HashMap<>(); // Simulated user database

//     public UserAuthenticationApp() {
//         frame = new JFrame("User Authentication App");
//         frame.setSize(400, 200);
//         frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//         frame.setLayout(new CardLayout());

//         JPanel loginPanel = createLoginPanel();
//         JPanel registerPanel = createRegisterPanel();

//         frame.add(loginPanel, "login");
//         frame.add(registerPanel, "register");

//         CardLayout cardLayout = (CardLayout) frame.getContentPane().getLayout();

//         JButton loginButton = new JButton("Login");
//         JButton registerButton = new JButton("Register");

//         loginButton.addActionListener(e -> cardLayout.show(frame.getContentPane(), "login"));
//         registerButton.addActionListener(e -> cardLayout.show(frame.getContentPane(), "register"));

//         JPanel buttonPanel = new JPanel();
//         buttonPanel.add(loginButton);
//         buttonPanel.add(registerButton);

//         frame.add(buttonPanel, BorderLayout.SOUTH);

//         frame.setVisible(true);
//     }

//     private JPanel createLoginPanel() {
//         JPanel panel = new JPanel();
//         panel.setLayout(new GridLayout(3, 1));

//         JTextField usernameField = new JTextField(20);
//         JPasswordField passwordField = new JPasswordField(20);

//         JButton loginButton = new JButton("Login");

//         loginButton.addActionListener(new ActionListener() {
//             @Override
//             public void actionPerformed(ActionEvent e) {
//                 String username = usernameField.getText();
//                 String password = new String(passwordField.getPassword());

//                 if (users.containsKey(username) && users.get(username).equals(password)) {
//                     JOptionPane.showMessageDialog(frame, "Login Successful!");
//                 } else {
//                     JOptionPane.showMessageDialog(frame, "Login Failed. Please check your credentials.");
//                 }
//             }
//         });

//         panel.add(new JLabel("Username:"));
//         panel.add(usernameField);
//         panel.add(new JLabel("Password:"));
//         panel.add(passwordField);
//         panel.add(loginButton);

//         return panel;
//     }

//     private JPanel createRegisterPanel() {
//         JPanel panel = new JPanel();
//         panel.setLayout(new GridLayout(3, 1));

//         JTextField usernameField = new JTextField(20);
//         JPasswordField passwordField = new JPasswordField(20);

//         JButton registerButton = new JButton("Register");

//         registerButton.addActionListener(new ActionListener() {
//             @Override
//             public void actionPerformed(ActionEvent e) {
//                 String username = usernameField.getText();
//                 String password = new String(passwordField.getPassword());

//                 if (users.containsKey(username)) {
//                     JOptionPane.showMessageDialog(frame, "Username already exists. Please choose a different one.");
//                 } else {
//                     users.put(username, password);
//                     JOptionPane.showMessageDialog(frame, "Registration Successful!");
//                 }
//             }
//         });

//         panel.add(new JLabel("Username:"));
//         panel.add(usernameField);
//         panel.add(new JLabel("Password:"));
//         panel.add(passwordField);
//         panel.add(registerButton);

//         return panel;
//     }

//     public static void main(String[] args) {
//         SwingUtilities.invokeLater(() -> new UserAuthenticationApp());
//     }
// }
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class UserAuthenticationApp {

    private JFrame frame;
    // private Map<String, String> users = new HashMap<>();
    private Connection connection;

    public UserAuthenticationApp() {
        initializeDatabase();
        frame = new JFrame("User Authentication App");
        frame.setSize(400, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new CardLayout());

        JPanel loginPanel = createLoginPanel();
        JPanel registerPanel = createRegisterPanel();

        frame.add(loginPanel, "login");
        frame.add(registerPanel, "register");

        CardLayout cardLayout = (CardLayout) frame.getContentPane().getLayout();

        JButton loginButton = new JButton("Login");
        JButton registerButton = new JButton("Register");

        loginButton.addActionListener(e -> cardLayout.show(frame.getContentPane(), "login"));
        registerButton.addActionListener(e -> cardLayout.show(frame.getContentPane(), "register"));

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);

        frame.add(buttonPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    private void initializeDatabase() {
        try {

            String url = "jdbc:mysql://localhost:3306/UserAuthenticationApp";

            connection = DriverManager.getConnection(url);

            String createUsersTable = "CREATE TABLE IF NOT EXISTS Users (id INT AUTO_INCREMENT PRIMARY KEY, username VARCHAR(255), password VARCHAR(255))";
            String createMessagesTable = "CREATE TABLE IF NOT EXISTS Messages (id INT AUTO_INCREMENT PRIMARY KEY, sender VARCHAR(255), recipient VARCHAR(255), message_content TEXT)";

            try (PreparedStatement createUsersTableStatement = connection.prepareStatement(createUsersTable);
                    PreparedStatement createMessagesTableStatement = connection.prepareStatement(createMessagesTable)) {

                createUsersTableStatement.executeUpdate();
                createMessagesTableStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();

        }
    }

    private JPanel createLoginPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 1));

        JTextField usernameField = new JTextField(20);
        JPasswordField passwordField = new JPasswordField(20);

        JButton loginButton = new JButton("Login");

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                if (checkLogin(username, password)) {
                    JOptionPane.showMessageDialog(frame, "Login Successful!");
                } else {
                    JOptionPane.showMessageDialog(frame, "Login Failed. Please check your credentials.");
                }
            }
        });

        panel.add(new JLabel("Username:"));
        panel.add(usernameField);
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);
        panel.add(loginButton);

        return panel;
    }

    private boolean checkLogin(String username, String password) {

        try {
            String query = "SELECT * FROM Users WHERE username = ? AND password = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, password);

                return preparedStatement.executeQuery().next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private JPanel createRegisterPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 1));

        JTextField usernameField = new JTextField(20);
        JPasswordField passwordField = new JPasswordField(20);

        JButton registerButton = new JButton("Register");

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                if (registerUser(username, password)) {
                    JOptionPane.showMessageDialog(frame, "Registration Successful!");
                } else {
                    JOptionPane.showMessageDialog(frame, "Username already exists. Please choose a different one.");
                }
            }
        });

        panel.add(new JLabel("Username:"));
        panel.add(usernameField);
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);
        panel.add(registerButton);

        return panel;
    }

    private boolean registerUser(String username, String password) {

        try {
            String query = "INSERT INTO Users (username, password) VALUES (?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, password);

                preparedStatement.executeUpdate();
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void main(String[] args) {

        UserAuthenticationApp App = new UserAuthenticationApp();
    }
}
