package main.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener; // Importe ActionListener

public class RegistroView extends JPanel {

    // Estes componentes são public para que o Controller possa acessá-los
    public JTextField loginField;
    public JPasswordField senhaField;
    public JComboBox<String> avatarComboBox;
    public JButton registerButton;
    public JButton backButton; // Adicionado para o controller manipular

    public RegistroView() {
        setLayout(new BorderLayout());
        setBackground(new Color(240, 248, 255));

        JLabel titleLabel = new JLabel("Criar Novo usuário", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(new Color(60, 63, 65));
        add(titleLabel, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(new Color(224, 255, 255));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel loginLabel = new JLabel("Login:");
        loginLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.LINE_END;
        contentPanel.add(loginLabel, gbc);

        loginField = new JTextField(20);
        loginField.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.LINE_START;
        contentPanel.add(loginField, gbc);

        JLabel senhaLabel = new JLabel("Senha:");
        senhaLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.LINE_END;
        contentPanel.add(senhaLabel, gbc);

        senhaField = new JPasswordField(20);
        senhaField.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        contentPanel.add(senhaField, gbc);

        JLabel avatarLabel = new JLabel("Escolha seu Avatar:");
        avatarLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.LINE_END;
        contentPanel.add(avatarLabel, gbc);

        String[] avatares = {"Avatar 1", "Avatar 2", "Avatar 3", "Avatar 4"}; // Exemplo de avatares
        avatarComboBox = new JComboBox<>(avatares);
        avatarComboBox.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.LINE_START;
        contentPanel.add(avatarComboBox, gbc);

        registerButton = new JButton("Registrar");
        registerButton.setFont(new Font("Arial", Font.BOLD, 16));
        registerButton.setBackground(new Color(34, 139, 34));
        registerButton.setForeground(Color.WHITE);
        registerButton.setFocusPainted(false);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        contentPanel.add(registerButton, gbc);

        backButton = new JButton("Voltar ao Login"); // Renomeado e tornado public para o controller
        backButton.setFont(new Font("Arial", Font.PLAIN, 12));
        backButton.setForeground(new Color(70, 130, 180));
        backButton.setContentAreaFilled(false);
        backButton.setFocusPainted(false);
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        contentPanel.add(backButton, gbc);

        add(contentPanel, BorderLayout.CENTER);
    }

    // Métodos para o Controller adicionar os Listeners
    public void addRegisterButtonListener(ActionListener listener) {
        registerButton.addActionListener(listener);
    }

    public void addBackButtonListener(ActionListener listener) {
        backButton.addActionListener(listener);
    }

    // Métodos para o Controller obter e limpar dados da View
    public String getLogin() {
        return loginField.getText();
    }

    public String getSenha() {
        return new String(senhaField.getPassword());
    }

    public String getAvatarURL() {
        return (String) avatarComboBox.getSelectedItem();
    }

    public void clearFields() {
        loginField.setText("");
        senhaField.setText("");
        avatarComboBox.setSelectedIndex(0);
    }

    // Métodos para o Controller exibir mensagens
    public void showMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }
}