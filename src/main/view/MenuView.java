package main.view;

import javax.swing.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

public class MenuView extends JPanel {

    // DocumentFilter interno para garantir que apenas números sejam digitados
    class NumericFilter extends DocumentFilter {
        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
            if (string == null) return;
            if (string.matches("\\d+")) { // Verifica se a string contém apenas dígitos
                super.insertString(fb, offset, string, attr);
            }
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
            if (text == null) return;
            if (text.matches("\\d+")) { // Verifica se o texto contém apenas dígitos
                super.replace(fb, offset, length, text, attrs);
            }
        }
    }

    private JTextField criaturasField;
    private JButton iniciarButton;
    private JButton estatisticasButton;
    private JButton deletarUsuarioButton;
    private JButton logoutButton;
    private JLabel userLabel;
    private JLabel avatarDisplayLabel;

    public MenuView() {
        setLayout(new BorderLayout());
        setBackground(new Color(240, 248, 255));

        // --- Header Panel (remains at NORTH of BorderLayout) ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(240, 248, 255));

        JLabel titleLabel = new JLabel("Simulação de criaturas saltitantes", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        // --- User Info Panel (NEW: this will be the centered avatar/name) ---
        // Using a JPanel with BorderLayout for vertical stacking of avatar and label
        // And then placing this JPanel in the center of the MenuView's BorderLayout.
        JPanel userInfoPanel = new JPanel();
        userInfoPanel.setLayout(new BoxLayout(userInfoPanel, BoxLayout.Y_AXIS)); // Stack vertically
        userInfoPanel.setBackground(new Color(240, 248, 255));
        userInfoPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0)); // Some top padding

        avatarDisplayLabel = new JLabel();
        avatarDisplayLabel.setPreferredSize(new Dimension(80, 80)); // Increased size for avatar
        avatarDisplayLabel.setMaximumSize(new Dimension(80, 80)); // Ensure it doesn't grow too much
        avatarDisplayLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // Center horizontally

        userLabel = new JLabel("");
        userLabel.setFont(new Font("Arial", Font.BOLD, 14)); // Slightly larger font for prominence
        userLabel.setForeground(new Color(60, 63, 65));
        userLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // Center horizontally

        userInfoPanel.add(avatarDisplayLabel);
        userInfoPanel.add(Box.createVerticalStrut(5)); // Small vertical space
        userInfoPanel.add(userLabel);
        userInfoPanel.add(Box.createVerticalGlue()); // Push content to the top within its area
        JPanel topContentPanel = new JPanel(new BorderLayout());
        topContentPanel.setBackground(new Color(240, 248, 255));
        topContentPanel.add(titleLabel, BorderLayout.NORTH); // Main title at the very top


        topContentPanel.add(userInfoPanel, BorderLayout.CENTER);

        add(topContentPanel, BorderLayout.NORTH); // Add the combined top panel to the main view


        // --- Form Panel (remains at CENTER of BorderLayout) ---
        JPanel formPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        formPanel.setBackground(new Color(240, 248, 255));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0)); // Padding from top info

        criaturasField = new JTextField(5);
        ((AbstractDocument) criaturasField.getDocument()).setDocumentFilter(new NumericFilter());

        iniciarButton = new JButton("Iniciar");
        estatisticasButton = new JButton("Estatísticas");
        deletarUsuarioButton = new JButton("Deletar usuário");
        logoutButton = new JButton("Logout");
        formPanel.add(new JLabel("Número de criaturas:"));
        formPanel.add(criaturasField);
        formPanel.add(iniciarButton);
        formPanel.add(estatisticasButton);
        formPanel.add(deletarUsuarioButton);
        formPanel.add(logoutButton);
        add(formPanel, BorderLayout.CENTER);
    }

    public void addIniciarButtonListener(ActionListener listener) {
        iniciarButton.addActionListener(listener);
    }

    public void addEstatisticasButtonListener(ActionListener listener) {
        estatisticasButton.addActionListener(listener);
    }

    public void addDeletarUsuarioButtonListener(ActionListener listener){
        deletarUsuarioButton.addActionListener(listener);
    }
    public void addLogoutButtonListener(ActionListener listener){
        logoutButton.addActionListener(listener);
    }

    public String getNumeroCriaturasText() {
        return criaturasField.getText();
    }

    // Method to set both user name and avatar image
    public void setUserInfo(String userName, String avatarPath) {
        userLabel.setText(userName != null && !userName.isEmpty() ? "Usuário: " + userName : "");

        if (avatarPath != null && !avatarPath.isEmpty()) {
            try {
                ImageIcon icon = new ImageIcon(getClass().getClassLoader().getResource(avatarPath));
                if (icon.getImageLoadStatus() == MediaTracker.COMPLETE) {
                    Image image = icon.getImage();
                    Image scaledImage = image.getScaledInstance(
                            avatarDisplayLabel.getPreferredSize().width,
                            avatarDisplayLabel.getPreferredSize().height,
                            Image.SCALE_SMOOTH
                    );
                    avatarDisplayLabel.setIcon(new ImageIcon(scaledImage));
                    avatarDisplayLabel.setText(""); // Remove error text if image loads
                } else {
                    System.err.println("Erro: Imagem do avatar não pôde ser carregada completamente: " + avatarPath);
                    avatarDisplayLabel.setIcon(null);
                    avatarDisplayLabel.setText("!"); // Small indicator for load failure
                }
            } catch (Exception ex) {
                System.err.println("Exceção ao carregar imagem do avatar para o menu: " + avatarPath);
                ex.printStackTrace();
                avatarDisplayLabel.setIcon(null);
                avatarDisplayLabel.setText("!");
            }
        } else {
            avatarDisplayLabel.setIcon(null);
            avatarDisplayLabel.setText("");
        }
    }

    public void showMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }

    public void clearCriaturasField() {
        criaturasField.setText("");
    }
}