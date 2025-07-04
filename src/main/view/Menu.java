package main.view;

import main.entity.Usuario;
import main.util.SessionManager;

import javax.swing.*;
import java.awt.*;
import javax.swing.text.*;


public class Menu extends JPanel {

    class NumericFilter extends DocumentFilter {
        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
            if (string == null) return;
            if (string.matches("\\d+")) {
                super.insertString(fb, offset, string, attr);
            }
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
            if (text == null) return;
            if (text.matches("\\d+")) {
                super.replace(fb, offset, length, text, attrs);
            }
        }
    }

    private JTextField criaturasField;
    private JButton iniciarButton;
    private JButton estatisticasButton;
    private JLabel userLabel;
    private Simulacao simulacao;
    private Estatisticas telaEstatisticas;

    public Menu(Simulacao simulacao, Estatisticas telaEstatisticas,JPanel mainPanel, CardLayout cardLayout) {
        this.simulacao = simulacao;
        setLayout(new BorderLayout());


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

        JPanel formPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10)); // Usando FlowLayout para o formulário
        formPanel.setBackground(new Color(240, 248, 255)); // Mesma cor de fundo

        criaturasField = new JTextField(5);
        iniciarButton = new JButton("Iniciar");
        estatisticasButton = new JButton("Estatísticas");




        ((AbstractDocument) criaturasField.getDocument()).setDocumentFilter(new NumericFilter());


        formPanel.add(new JLabel("Número de criaturas:"));
        formPanel.add(criaturasField);

        formPanel.add(iniciarButton);

        formPanel.add(estatisticasButton);

        add(formPanel, BorderLayout.CENTER);


        iniciarButton.addActionListener(e -> {

            String criaturas = criaturasField.getText();

            if ( !criaturas.isEmpty()) {
                int numCriaturas = Integer.parseInt(criaturas);


                if (numCriaturas > 1000) {
                    JOptionPane.showMessageDialog(this, "Número máximo de criaturas é 1000", "Erro de Entrada", JOptionPane.WARNING_MESSAGE);
                } else if (numCriaturas < 2) {
                    JOptionPane.showMessageDialog(this, "Número mínimo de criaturas é 2", "Erro de Entrada", JOptionPane.WARNING_MESSAGE);
                }
                else {
                    simulacao.iniciarSimulacao(numCriaturas);
                    cardLayout.show(mainPanel, "telaSimulacao");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Preencha todos os campos!", "Erro de Entrada", JOptionPane.WARNING_MESSAGE);
            }
        });

        estatisticasButton.addActionListener(e -> {
            telaEstatisticas.carregarEstatisticas();
            cardLayout.show(mainPanel,"telaEstatisticas");
        });
    }
    public void setLoggedInUserName() {
        Usuario usuario = SessionManager.getInstance().getLoggedInUser();
        if (usuario != null) {
            userLabel.setText("Usuário: " + usuario.getLogin());
        } else {
            userLabel.setText("");
        }
    }
}
