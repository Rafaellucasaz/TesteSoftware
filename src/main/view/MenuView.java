package main.view;

import javax.swing.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.awt.event.ActionListener; // Importe ActionListener

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

    // Componentes tornados públicos ou com getters para o Controller acessar
    private JTextField criaturasField;
    private JButton iniciarButton;
    private JButton estatisticasButton;
    private JLabel userLabel; // Este será atualizado pelo Controller

    public MenuView() {
        setLayout(new BorderLayout());
        setBackground(new Color(240, 248, 255));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(240, 248, 255));

        JLabel titleLabel = new JLabel("Simulação de criaturas saltitantes", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        userLabel = new JLabel("", SwingConstants.RIGHT);
        userLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        userLabel.setForeground(new Color(105, 105, 105));
        headerPanel.add(userLabel, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        formPanel.setBackground(new Color(240, 248, 255));

        criaturasField = new JTextField(5);
        // Aplica o filtro numérico diretamente na View, pois é uma validação de entrada de UI
        ((AbstractDocument) criaturasField.getDocument()).setDocumentFilter(new NumericFilter());

        iniciarButton = new JButton("Iniciar");
        estatisticasButton = new JButton("Estatísticas");

        formPanel.add(new JLabel("Número de criaturas:"));
        formPanel.add(criaturasField);
        formPanel.add(iniciarButton);
        formPanel.add(estatisticasButton);

        add(formPanel, BorderLayout.CENTER);
    }

    // Métodos para o Controller adicionar os Listeners aos botões
    public void addIniciarButtonListener(ActionListener listener) {
        iniciarButton.addActionListener(listener);
    }

    public void addEstatisticasButtonListener(ActionListener listener) {
        estatisticasButton.addActionListener(listener);
    }

    // Métodos para o Controller obter os dados da View
    public String getNumeroCriaturasText() {
        return criaturasField.getText();
    }

    // Métodos para o Controller atualizar a View
    public void setUserLabelText(String text) {
        userLabel.setText(text);
    }

    public void showMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }

    public void clearCriaturasField() {
        criaturasField.setText("");
    }
}