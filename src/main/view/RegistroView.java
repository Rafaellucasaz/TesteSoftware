package main.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener; // Importe ActionListener
import java.awt.event.ItemEvent;      // Importe ItemEvent
import java.awt.event.ItemListener;    // Importe ItemListener
import java.util.HashMap;
import java.util.Map;

public class RegistroView extends JPanel {

    public JTextField loginField;
    public JPasswordField senhaField;
    public JComboBox<String> avatarComboBox;
    public JButton registerButton;
    public JButton backButton;
    private JLabel avatarImageLabel;


    private final Map<String, String> avatarPaths = new HashMap<>();

    public RegistroView() {
        setLayout(new BorderLayout());
        setBackground(new Color(240, 248, 255));


        avatarPaths.put("Avatar 1", "assets/avatars/Isabelle.jpg");
        avatarPaths.put("Avatar 2", "assets/avatars/BigBoss.jpg");
        avatarPaths.put("Avatar 3", "assets/avatars/Leon.jpg");
        avatarPaths.put("Avatar 4", "assets/avatars/Mario.jpg");
        avatarPaths.put("Avatar 5", "assets/avatars/Peach.jpg");
        avatarPaths.put("Avatar 6", "assets/avatars/Shadow.jpg");
        avatarPaths.put("Avatar 7", "assets/avatars/superman.png");
        avatarPaths.put("Avatar 8", "assets/avatars/superman2.png");

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


        JLabel avatarSelectionLabel = new JLabel("Escolha seu Avatar:");
        avatarSelectionLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.LINE_END;
        contentPanel.add(avatarSelectionLabel, gbc);

        String[] avatares = avatarPaths.keySet().toArray(new String[0]);
        avatarComboBox = new JComboBox<>(avatares);
        avatarComboBox.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.LINE_START;
        contentPanel.add(avatarComboBox, gbc);


        avatarImageLabel = new JLabel();
        avatarImageLabel.setPreferredSize(new Dimension(80, 80));
        avatarImageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        avatarImageLabel.setVerticalAlignment(SwingConstants.CENTER);
        avatarImageLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.fill = GridBagConstraints.NONE;
        contentPanel.add(avatarImageLabel, gbc);

        registerButton = new JButton("Registrar");
        registerButton.setFont(new Font("Arial", Font.BOLD, 16));
        registerButton.setBackground(new Color(34, 139, 34));
        registerButton.setForeground(Color.WHITE);
        registerButton.setFocusPainted(false);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2; // Ocupa 2 colunas para centralizar
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        contentPanel.add(registerButton, gbc);


        backButton = new JButton("Voltar ao Login");
        backButton.setFont(new Font("Arial", Font.PLAIN, 12));
        backButton.setForeground(new Color(70, 130, 180));
        backButton.setContentAreaFilled(false);
        backButton.setFocusPainted(false);
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        contentPanel.add(backButton, gbc);

        add(contentPanel, BorderLayout.CENTER);


        avatarComboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    String selectedAvatarName = (String) e.getItem();
                    updateAvatarImage(selectedAvatarName);
                }
            }
        });


        if (avatares.length > 0) {
            updateAvatarImage(avatares[0]);
        }
    }


    private void updateAvatarImage(String avatarName) {
        String imagePath = avatarPaths.get(avatarName);
        if (imagePath != null) {
            try {

                ImageIcon icon = new ImageIcon(getClass().getClassLoader().getResource(imagePath));


                Image image = icon.getImage();
                Image scaledImage = image.getScaledInstance(
                        avatarImageLabel.getPreferredSize().width,
                        avatarImageLabel.getPreferredSize().height,
                        Image.SCALE_SMOOTH
                );
                avatarImageLabel.setIcon(new ImageIcon(scaledImage));
            } catch (Exception ex) {
                System.err.println("Erro ao carregar imagem do avatar: " + imagePath);
                ex.printStackTrace();
                avatarImageLabel.setIcon(null);
                avatarImageLabel.setText("Erro img");
            }
        } else {
            avatarImageLabel.setIcon(null);
            avatarImageLabel.setText("N/A");
        }
    }


    public void addRegisterButtonListener(ActionListener listener) {
        registerButton.addActionListener(listener);
    }

    public void addBackButtonListener(ActionListener listener) {
        backButton.addActionListener(listener);
    }


    public String getLogin() {
        return loginField.getText();
    }

    public String getSenha() {
        return new String(senhaField.getPassword());
    }

    // Agora, este método retorna o caminho COMPLETO do avatar selecionado
    public String getSelectedAvatarPath() {
        String selectedName = (String) avatarComboBox.getSelectedItem();
        return avatarPaths.get(selectedName);
    }

    public void clearFields() {
        loginField.setText("");
        senhaField.setText("");
        avatarComboBox.setSelectedIndex(0);
        updateAvatarImage((String) avatarComboBox.getSelectedItem());
    }

    // Métodos para o Controller exibir mensagens
    public void showMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }
}