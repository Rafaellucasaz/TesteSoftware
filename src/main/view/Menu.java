package main.view;

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

    private JTextField rodadasField;
    private JTextField criaturasField;
    private JButton iniciarButton;
    private Simulacao simulacao;

    public Menu(Simulacao simulacao, JPanel mainPanel, CardLayout cardLayout) {
        setLayout(new BorderLayout());

        JLabel label = new JLabel("Simulação de criaturas saltitantes", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 20));
        add(label, BorderLayout.CENTER);

        JPanel formPanel = new JPanel();

        rodadasField = new JTextField(5);
        criaturasField = new JTextField(5);
        iniciarButton = new JButton("Iniciar");
        ((AbstractDocument) rodadasField.getDocument()).setDocumentFilter(new NumericFilter());
        ((AbstractDocument) criaturasField.getDocument()).setDocumentFilter(new NumericFilter());

        formPanel.add(new JLabel("Número de rodadas:"));
        formPanel.add(rodadasField);

        formPanel.add(new JLabel("Número de criaturas:"));
        formPanel.add(criaturasField);

        formPanel.add(iniciarButton);

        add(formPanel, BorderLayout.SOUTH);


        iniciarButton.addActionListener(e -> {
            String rodadas = rodadasField.getText();
            String criaturas = criaturasField.getText();

            if (!rodadas.isEmpty() && !criaturas.isEmpty()) {
                if(Integer.parseInt(criaturas) > 40){
                    JOptionPane.showMessageDialog(this, "Número máximo de criaturas é 40");
                }
                else if(Integer.parseInt(criaturas) < 2){
                    JOptionPane.showMessageDialog(this, "Número mínimo de criaturas é 2");
                }
                else{
                    simulacao.iniciarSimulacao(Integer.parseInt(rodadas),Integer.parseInt(criaturas));
                    cardLayout.show(mainPanel, "telaSimulacao");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Preencha todos os campos!");
            }
        });
    }
}