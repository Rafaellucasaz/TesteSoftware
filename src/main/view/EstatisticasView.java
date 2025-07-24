package main.view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;

public class EstatisticasView extends JPanel {


    private JTable statsTable;
    private DefaultTableModel tableModel;
    private JLabel totalSimulationsLabel;
    private JLabel avgScoreLabel;
    private JLabel avgSimulationsLabel;
    private JButton backButton;

    public EstatisticasView() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(240, 248, 255));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));


        JLabel titleLabel = new JLabel("Estatísticas da Simulação", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(new Color(60, 63, 65));
        add(titleLabel, BorderLayout.NORTH);


        String[] columnNames = {"Login", "Pontuação", "Simulações"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        statsTable = new JTable(tableModel);
        statsTable.setFont(new Font("Arial", Font.PLAIN, 12));
        statsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        statsTable.setRowHeight(25);
        statsTable.setFillsViewportHeight(true);

        JScrollPane scrollPane = new JScrollPane(statsTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180), 1));
        add(scrollPane, BorderLayout.CENTER);


        JPanel summaryPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        summaryPanel.setBackground(new Color(240, 248, 255));

        totalSimulationsLabel = new JLabel("Total de Simulações: 0", SwingConstants.LEFT);
        totalSimulationsLabel.setFont(new Font("Arial", Font.BOLD, 14));
        summaryPanel.add(totalSimulationsLabel);

        avgSimulationsLabel = new JLabel("Média de Simulações por Usuário: 0.0", SwingConstants.LEFT);
        avgSimulationsLabel.setFont(new Font("Arial", Font.BOLD, 14));
        summaryPanel.add(avgSimulationsLabel);

        avgScoreLabel = new JLabel("Média de Pontuação por Usuário: 0.0", SwingConstants.LEFT);
        avgScoreLabel.setFont(new Font("Arial", Font.BOLD, 14));
        summaryPanel.add(avgScoreLabel);


        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(new Color(240, 248, 255));

        backButton = new JButton("Voltar ao Menu");
        backButton.setFont(new Font("Arial", Font.BOLD, 14));
        backButton.setBackground(new Color(100, 149, 237));
        backButton.setForeground(Color.WHITE);
        backButton.setFocusPainted(false);
        backButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        buttonPanel.add(backButton);


        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(summaryPanel, BorderLayout.NORTH);
        southPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(southPanel, BorderLayout.SOUTH);
    }



    public void addBackButtonListener(ActionListener listener) {
        backButton.addActionListener(listener);
    }

    public void clearTable() {
        tableModel.setRowCount(0);
    }

    public void addTableRow(Object[] rowData) {
        tableModel.addRow(rowData);
    }

    public void setTotalSimulationsLabel(long totalSimulations) {
        totalSimulationsLabel.setText("Total de Simulações: " + totalSimulations);
    }

    public void setAvgSimulationsLabel(double avgSims) {
        avgSimulationsLabel.setText(String.format("Média de Simulações por Usuário: %.2f", avgSims));
    }

    public void setAvgScoreLabel(double avgScore) {
        avgScoreLabel.setText(String.format("Média de Pontuação por Usuário: %.2f", avgScore));
    }

    public void showMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }
}