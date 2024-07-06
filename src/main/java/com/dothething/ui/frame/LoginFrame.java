package com.dothething.ui.frame;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {
  private final JPasswordField passwordField;

  public LoginFrame() {
    setTitle("Login");
    setSize(300, 150);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLocationRelativeTo(null);
    setLayout(new GridLayout(3, 1));

    JLabel passwordLabel = new JLabel("Enter Passkey:");
    passwordField = new JPasswordField();
    JButton loginButton = new JButton("Login");

    add(passwordLabel);
    add(passwordField);
    add(loginButton);

    loginButton.addActionListener(e -> {
      char[] input = passwordField.getPassword();
      if (isPasswordCorrect(input)) {
        dispose();
        new MainFrame().setVisible(true);
      } else {
        JOptionPane.showMessageDialog(null, "Invalid Passkey", "Error", JOptionPane.ERROR_MESSAGE);
      }
    });
  }

  private boolean isPasswordCorrect(char[] input) {
    // Implement passkey verification logic here
    return new String(input).equals("123456"); // Replace with actual logic
  }
}
