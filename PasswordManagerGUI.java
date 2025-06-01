import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

public class PasswordManagerGUI extends JFrame {
    private static final int SHIFT = 3;
    private static final String FILE_NAME = "passwords.txt";
    private HashMap<String, String> passwordDB = new HashMap<>();

    private JTextField siteField = new JTextField(20);
    private JTextField passwordField = new JTextField(20);
    private JTextArea outputArea = new JTextArea(10, 30);

    public PasswordManagerGUI() {
        setTitle("🔐 Password Manager");
        setSize(400, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());

        add(new JLabel("Website:"));
        add(siteField);
        add(new JLabel("Password:"));
        add(passwordField);

        JButton addButton = new JButton("Add");
        JButton getButton = new JButton("Get");
        JButton listButton = new JButton("List All");

        add(addButton);
        add(getButton);
        add(listButton);

        outputArea.setEditable(false);
        add(new JScrollPane(outputArea));

        loadFromFile();

        addButton.addActionListener(e -> addPassword());
        getButton.addActionListener(e -> getPassword());
        listButton.addActionListener(e -> listAll());

        setVisible(true);
    }

    private String encrypt(String text) {
        StringBuilder sb = new StringBuilder();
        for (char ch : text.toCharArray()) {
            sb.append((char) (ch + SHIFT));
        }
        return sb.toString();
    }

    private String decrypt(String text) {
        StringBuilder sb = new StringBuilder();
        for (char ch : text.toCharArray()) {
            sb.append((char) (ch - SHIFT));
        }
        return sb.toString();
    }

    private void addPassword() {
        String site = siteField.getText().trim();
        String pwd = passwordField.getText().trim();
        if (site.isEmpty() || pwd.isEmpty()) {
            showMessage("Please fill both fields.");
            return;
        }

        passwordDB.put(site, encrypt(pwd));
        saveToFile();
        showMessage("✅ Password saved for " + site);
        siteField.setText("");
        passwordField.setText("");
    }

    private void getPassword() {
        String site = siteField.getText().trim();
        if (passwordDB.containsKey(site)) {
            String decrypted = decrypt(passwordDB.get(site));
            showMessage("🔐 Password for " + site + ": " + decrypted);
        } else {
            showMessage("❌ No password found for " + site);
        }
    }

    private void listAll() {
        if (passwordDB.isEmpty()) {
            showMessage("📭 No passwords saved.");
            return;
        }
        StringBuilder sb = new StringBuilder("📋 Stored websites:\n");
        for (String site : passwordDB.keySet()) {
            sb.append("- ").append(site).append("\n");
        }
        showMessage(sb.toString());
    }

    private void showMessage(String msg) {
        outputArea.setText(msg);
    }

    private void saveToFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_NAME))) {
            for (Map.Entry<String, String> entry : passwordDB.entrySet()) {
                writer.println(entry.getKey() + ":" + entry.getValue());
            }
        } catch (IOException e) {
            showMessage("Error saving to file.");
        }
    }

    private void loadFromFile() {
        try (Scanner scanner = new Scanner(new File(FILE_NAME))) {
            while (scanner.hasNextLine()) {
                String[] parts = scanner.nextLine().split(":", 2);
                if (parts.length == 2) {
                    passwordDB.put(parts[0], parts[1]);
                }
            }
        } catch (FileNotFoundException e) {
            // First run: no file yet.
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(PasswordManagerGUI::new);
    }
}
