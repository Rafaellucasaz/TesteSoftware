package main.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class LoginView extends JPanel {


    private JTextField loginField;
    private JPasswordField senhaField;
    private JButton loginButton;
    private JButton registerButton;

    public LoginView() {
        setLayout(new BorderLayout());
        setBackground(new Color(240, 248, 255));


        JLabel titleLabel = new JLabel("Simulação de criaturas saltitantes", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(new Color(60, 63, 65));
        add(titleLabel, BorderLayout.NORTH);


        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(240, 248, 255));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;


        JLabel loginLabel = new JLabel("Login:");
        loginLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.LINE_END;
        formPanel.add(loginLabel, gbc);

        loginField = new JTextField(20);
        loginField.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.LINE_START;
        formPanel.add(loginField, gbc);


        JLabel senhaLabel = new JLabel("Senha:");
        senhaLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.LINE_END;
        formPanel.add(senhaLabel, gbc);

        senhaField = new JPasswordField(20);
        senhaField.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        formPanel.add(senhaField, gbc);


        loginButton = new JButton("Entrar");
        loginButton.setFont(new Font("Arial", Font.BOLD, 16));
        loginButton.setBackground(new Color(70, 130, 180)); // Azul aço
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(loginButton, gbc);


        registerButton = new JButton("Registre-se!");
        registerButton.setFont(new Font("Arial", Font.PLAIN, 12));
        registerButton.setForeground(new Color(70, 130, 180));
        registerButton.setContentAreaFilled(false);
        registerButton.setFocusPainted(false);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        formPanel.add(registerButton, gbc);

        add(formPanel, BorderLayout.CENTER);
    }



    public void addLoginButtonListener(ActionListener listener) {
        loginButton.addActionListener(listener);
    }

    public void addRegisterButtonListener(ActionListener listener) {
        registerButton.addActionListener(listener);
    }

    public String getLogin() {
        return loginField.getText();
    }

    public String getSenha() {
        return new String(senhaField.getPassword());
    }

    public void clearFields() {
        loginField.setText("");
        senhaField.setText("");
    }

    public void showMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }
}