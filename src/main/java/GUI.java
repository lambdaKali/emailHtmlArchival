import javax.mail.MessagingException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class GUI {
    private static final String DEFAULT_PROTOCOL = "imaps";
    private static final String DEFAULT_HOST = "outlook.com";

    public GUI() {
        createAndShowGUI();
    }

    private void createAndShowGUI() {
        JFrame frame = new JFrame("HEAS");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);

        Container container = frame.getContentPane();
        container.setLayout(new GridLayout(7, 2));

        JLabel welcomeLabel = new JLabel("HTML EMAIL ARCHIVAL SYSTEM!");
        welcomeLabel.setHorizontalAlignment(JLabel.CENTER);
        container.add(welcomeLabel);
        container.add(new JLabel());

        JTextField emailField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JTextField emailsPerPageField = new JTextField();

        container.add(new JLabel("Email:"));
        container.add(emailField);
        container.add(new JLabel("Password:"));
        container.add(passwordField);
        container.add(new JLabel("Output Folder:"));

        JButton outputFolderButton = new JButton("...");
        container.add(outputFolderButton);
        container.add(new JLabel("Emails per Page:"));
        container.add(emailsPerPageField);

        outputFolderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int returnValue = fileChooser.showOpenDialog(frame);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File selectedFolder = fileChooser.getSelectedFile();
                    outputFolderButton.setText(selectedFolder.getAbsolutePath());
                }
            }
        });

        JButton submitButton = new JButton("Submit");
        container.add(new JLabel("Submit:"));
        container.add(submitButton);
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String email = emailField.getText();
                String password = new String(passwordField.getPassword());
                String outputFolder = outputFolderButton.getText();
                int emailsPerPage;

                // Check for valid email address - look into libraries for more comprehensive validations
                if (!email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
                    JOptionPane.showMessageDialog(frame, "Invalid email address. Please enter a valid email address.");
                    return;
                }

                // Check for valid password
                if (password.length() < 6) {
                    JOptionPane.showMessageDialog(frame, "Invalid password. Please enter a valid password.");
                    return;
                }

                // Check for valid number in "Emails per Page" field
                try {
                    emailsPerPage = Integer.parseInt(emailsPerPageField.getText());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Invalid number in 'Emails per Page' field. Please enter a valid number.");
                    return;
                }

                try {
                    // Attempt to connect to the email server to validate credentials
                    EmailClient client = new EmailClient(DEFAULT_PROTOCOL, DEFAULT_HOST, email, password);

                    // If the connection is successful, proceed with the email archiving
                    File folder = new File(outputFolder, "HTML-Archive");
                    if (!folder.exists()) {
                        folder.mkdirs();
                    }

                    int totalEmails = client.getEmailCount();
                    int totalPages = (int) Math.ceil((double) totalEmails / emailsPerPage);

                    // Loop through each page of emails
                    for (int i = 1; i <= totalPages; i++) {

                        // Calculate the starting and ending indexes for the emails on the current page
                        int startIndex = (i - 1) * emailsPerPage + 1;
                        int endIndex = Math.min(i * emailsPerPage, totalEmails);

                        // Retrieve a list of emails from a client object using the starting and ending indexes
                        List<Email> emails = client.getEmails(startIndex, endIndex);

                        // Generate an HTML file for the current page using the "generateHome" method in the FileBuilder class
                        // The method takes in the absolute path for the file, the list of emails, the total number of pages, and the current page number as arguments
                        FileBuilder.generateHome(folder.getAbsolutePath() + "/index_page_" + i + ".html", emails, totalPages, i);

                        // Generate separate HTML files for each email in the list using the "generateFiles" method in the FileBuilder class
                        // The method takes in the absolute path for the file and the email object as arguments
                        // The file names are generated using the hash code of the email object, concatenated with the string "mail"
                        for (Email emailObj : emails) {
                            FileBuilder.generateFiles(folder.getAbsolutePath() + "/mail" + emailObj.hashCode() + ".html", emailObj);
                        }
                    }

                    client.close();
                    JOptionPane.showMessageDialog(frame, "Thank you for using the HTML EMAIL ARCHIVAL SYSTEM! Your archive will be in your directory of choice, in a folder named: HTML-Archive.");

                    // Clear the entry fields
                    emailField.setText("");
                    passwordField.setText("");
                    outputFolderButton.setText("...");
                    emailsPerPageField.setText("");

                } catch (IOException | MessagingException ex) {
                    // If the connection is unsuccessful, show an error message and prompt to retry
                    JOptionPane.showMessageDialog(frame, "Invalid email address or password. Please check your credentials and try again.");
                }
            }
        });
        frame.setVisible(true);
    }
}