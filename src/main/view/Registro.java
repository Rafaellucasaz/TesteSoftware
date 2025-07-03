package main.view;

import main.dao.impl.UsuarioDaoImpl;
import main.entity.Usuario;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class Registro extends JPanel {

    private JPanel mainPanel;
    private CardLayout cardLayout;

    private JTextField loginField;
    private JPasswordField senhaField;
    private JComboBox<String> avatarComboBox;
    private JButton registerButton;

    private UsuarioDaoImpl usuarioDAO;


    public Registro(JPanel mainPanel, CardLayout cardLayout) {
        this.mainPanel = mainPanel;
        this.cardLayout = cardLayout;
        this.usuarioDAO = new UsuarioDaoImpl();
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


        JButton backButton = new JButton("Voltar ao Login");
        backButton.setFont(new Font("Arial", Font.PLAIN, 12));
        backButton.setForeground(new Color(70, 130, 180));

        backButton.setContentAreaFilled(false);
        backButton.setFocusPainted(false);
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        contentPanel.add(backButton, gbc);

        add(contentPanel, BorderLayout.CENTER);


        registerButton.addActionListener(e -> registrar());


        backButton.addActionListener(e -> {
            cardLayout.show(mainPanel, "telaLogin");
        });
    }

    private void registrar() {
        String login = loginField.getText();
        String senha = new String(senhaField.getPassword());
        String avatarURL = (String) avatarComboBox.getSelectedItem();

        if (login.isEmpty() || senha.isEmpty() || avatarURL == null || avatarURL.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, preencha todos os campos e selecione um avatar.", "Erro de Registro", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {

            Usuario existingUser = usuarioDAO.getUsuarioByLogin(login);
            if (existingUser != null) {
                JOptionPane.showMessageDialog(this, "Login já existe. Por favor, escolha outro.", "Erro de Registro", JOptionPane.WARNING_MESSAGE);
                return;
            }


            Usuario novoUsuario = new Usuario(login, senha, avatarURL, 0, 0);


            novoUsuario = usuarioDAO.addUsuario(novoUsuario);

            if (novoUsuario.getId() > 0) {
                JOptionPane.showMessageDialog(this, "Registro bem-sucedido! Bem-vindo, " + novoUsuario.getLogin() + "!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                cardLayout.show(mainPanel, "telaLogin");
                loginField.setText("");
                senhaField.setText("");
                avatarComboBox.setSelectedIndex(0);
            } else {
                JOptionPane.showMessageDialog(this, "Falha ao registrar usuário. Tente novamente.", "Erro de Registro", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao registrar usuário: " + ex.getMessage(), "Erro de Banco de Dados", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}