// ExhibitionRegistrationApp.java

// Import necessary packages
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.sql.*;
import javax.imageio.ImageIO;
import javax.swing.*;

// Main application class extending JFrame
public class ExhibitionRegistrationApp extends JFrame {
    // Form input components
    JTextField txtRegID, txtName, txtFaculty, txtProject, txtContact, txtEmail;
    JLabel lblImagePath;  // Displays selected image path
    JLabel lblImageDisplay;  // Displays thumbnail preview
    String imagePath = "";  // Stores absolute path of selected image

    // Constructor: Initializes UI components
    public ExhibitionRegistrationApp() {
        // Window configuration
        setTitle("Exhibition Registration System");
        setSize(700, 600);
        setLayout(new GridBagLayout());  // Flexible layout manager
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);  // Padding around components
        gbc.fill = GridBagConstraints.HORIZONTAL;  // Component resizing behavior

        // Initialize form fields
        txtRegID = new JTextField(20);
        txtName = new JTextField(20);
        txtFaculty = new JTextField(20);
        txtProject = new JTextField(20);
        txtContact = new JTextField(20);
        txtEmail = new JTextField(20);
        
        // Image display components
        lblImagePath = new JLabel("No Image Selected");
        lblImageDisplay = new JLabel();
        lblImageDisplay.setPreferredSize(new Dimension(150, 150));  // Fixed size for image preview
        lblImageDisplay.setBorder(BorderFactory.createLineBorder(Color.BLACK));  // Visible border

        // Action buttons
        JButton btnBrowse = new JButton("Browse Image");
        btnBrowse.addActionListener(e -> browseImage());  // Lambda for image selection

        JButton btnRegister = new JButton("Register");
        btnRegister.addActionListener(e -> registerParticipant());

        JButton btnSearch = new JButton("Search");
        btnSearch.addActionListener(e -> searchParticipant());

        JButton btnUpdate = new JButton("Update");
        btnUpdate.addActionListener(e -> updateParticipant());

        JButton btnDelete = new JButton("Delete");
        btnDelete.addActionListener(e -> deleteParticipant());

        JButton btnClear = new JButton("Clear");
        btnClear.addActionListener(e -> clearFields());

        JButton btnExit = new JButton("Exit");
        btnExit.addActionListener(e -> System.exit(0));  // Terminate application

        // Build UI layout using GridBagConstraints
        int row = 0;
        // Helper method to add labeled text fields
        addRow(gbc, row++, "Registration ID:", txtRegID);
        addRow(gbc, row++, "Name:", txtName);
        addRow(gbc, row++, "Faculty:", txtFaculty);
        addRow(gbc, row++, "Project Title:", txtProject);
        addRow(gbc, row++, "Contact No:", txtContact);
        addRow(gbc, row++, "Email:", txtEmail);

        // Image path label and browse button
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1;
        add(lblImagePath, gbc);
        gbc.gridx = 1;
        add(btnBrowse, gbc);
        row++;

        // Image preview area
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        add(lblImageDisplay, gbc);
        row++;

        // Button panel at bottom
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(btnRegister);
        buttonPanel.add(btnSearch);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnClear);
        buttonPanel.add(btnExit);

        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        add(buttonPanel, gbc);

        // Final window setup
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  // Close application on window close
        setVisible(true);  // Make window visible
    }

    // Helper method to add labeled text fields to grid
    private void addRow(GridBagConstraints gbc, int row, String label, JTextField field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        add(new JLabel(label), gbc);  // Add label
        gbc.gridx = 1;
        add(field, gbc);  // Add text field
    }

    // Image selection handler
    private void browseImage() {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            imagePath = file.getAbsolutePath();  // Store absolute path
            lblImagePath.setText(imagePath);  // Update path label
            try {
                // Load and scale image for preview
                BufferedImage img = ImageIO.read(file);
                ImageIcon icon = new ImageIcon(img.getScaledInstance(150, 150, Image.SCALE_SMOOTH));
                lblImageDisplay.setIcon(icon);  // Display thumbnail
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Failed to load image");
            }
        }
    }

    // Reset all form fields
    private void clearFields() {
        txtRegID.setText("");
        txtName.setText("");
        txtFaculty.setText("");
        txtProject.setText("");
        txtContact.setText("");
        txtEmail.setText("");
        lblImagePath.setText("No Image Selected");
        lblImageDisplay.setIcon(null);  // Clear image preview
        imagePath = "";  // Reset stored path
    }

    // Input validation
    private boolean validateInput() {
        // Check empty fields
        if (txtRegID.getText().isEmpty() || txtName.getText().isEmpty() ||
            txtFaculty.getText().isEmpty() || txtProject.getText().isEmpty() ||
            txtContact.getText().isEmpty() || txtEmail.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields must be filled");
            return false;
        }
        // Basic email format validation
        if (!txtEmail.getText().matches("^[\\w.-]+@[\\w.-]+\\.\\w+$")) {
            JOptionPane.showMessageDialog(this, "Invalid email format");
            return false;
        }
        return true;
    }

    // Database operations (all use same Access DB file)
    private void registerParticipant() {
        if (!validateInput()) return;  // Pre-check
        String dbURL = "jdbc:ucanaccess://VUE_Exhibition.accdb";  // UCanAccess JDBC URL
        try (Connection conn = DriverManager.getConnection(dbURL)) {
            // Parameterized SQL insert
            String sql = "INSERT INTO Participants (RegistrationID, Name, Faculty, ProjectTitle, ContactNumber, Email, ImagePath) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            // Set parameters from form fields
            stmt.setString(1, txtRegID.getText());
            stmt.setString(2, txtName.getText());
            stmt.setString(3, txtFaculty.getText());
            stmt.setString(4, txtProject.getText());
            stmt.setString(5, txtContact.getText());
            stmt.setString(6, txtEmail.getText());
            stmt.setString(7, imagePath);  // Store file path, not image data
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Participant Registered Successfully");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Registration Failed: " + ex.getMessage());
        }
    }

    private void searchParticipant() {
        String dbURL = "jdbc:ucanaccess://VUE_Exhibition.accdb";
        try (Connection conn = DriverManager.getConnection(dbURL)) {
            // Search by RegistrationID
            String sql = "SELECT * FROM Participants WHERE RegistrationID=?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, txtRegID.getText());
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                // Populate form with retrieved data
                txtName.setText(rs.getString("Name"));
                txtFaculty.setText(rs.getString("Faculty"));
                txtProject.setText(rs.getString("ProjectTitle"));
                txtContact.setText(rs.getString("ContactNumber"));
                txtEmail.setText(rs.getString("Email"));
                imagePath = rs.getString("ImagePath");
                lblImagePath.setText(imagePath);
                // Load image preview if exists
                File file = new File(imagePath);
                if (file.exists()) {
                    BufferedImage img = ImageIO.read(file);
                    lblImageDisplay.setIcon(new ImageIcon(img.getScaledInstance(150, 150, Image.SCALE_SMOOTH)));
                }
            } else {
                JOptionPane.showMessageDialog(this, "No participant found");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Search Failed: " + ex.getMessage());
        }
    }

    private void updateParticipant() {
        if (!validateInput()) return;
        String dbURL = "jdbc:ucanaccess://VUE_Exhibition.accdb";
        try (Connection conn = DriverManager.getConnection(dbURL)) {
            // Update all fields by RegistrationID
            String sql = "UPDATE Participants SET Name=?, Faculty=?, ProjectTitle=?, ContactNumber=?, Email=?, ImagePath=? WHERE RegistrationID=?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, txtName.getText());
            stmt.setString(2, txtFaculty.getText());
            stmt.setString(3, txtProject.getText());
            stmt.setString(4, txtContact.getText());
            stmt.setString(5, txtEmail.getText());
            stmt.setString(6, imagePath);
            stmt.setString(7, txtRegID.getText());  // WHERE clause parameter
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Participant Updated Successfully");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Update Failed: " + ex.getMessage());
        }
    }

    private void deleteParticipant() {
        String dbURL = "jdbc:ucanaccess://VUE_Exhibition.accdb";
        try (Connection conn = DriverManager.getConnection(dbURL)) {
            // Delete by RegistrationID
            String sql = "DELETE FROM Participants WHERE RegistrationID=?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, txtRegID.getText());
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Participant Deleted Successfully");
            clearFields();  // Reset form after deletion
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Deletion Failed: " + ex.getMessage());
        }
    }

    // Entry point - launches Swing UI thread-safe
    public static void main(String[] args) {
        SwingUtilities.invokeLater(ExhibitionRegistrationApp::new);
    }
}