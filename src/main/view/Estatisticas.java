package main.view;

import main.dao.impl.UsuarioDaoImpl;
import main.entity.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class Estatisticas extends JPanel{
    private JPanel mainPanel;
    private CardLayout cardLayout;

    protected UsuarioDaoImpl usuarioDAO;
    private JTable statsTable;
    private DefaultTableModel tableModel;
    private JLabel totalSimulationsLabel;
    private JLabel avgScoreLabel;
    private JLabel avgSimulationsLabel;


    public Estatisticas(JPanel mainPanel, CardLayout cardLayout) {
        this.mainPanel = mainPanel;
        this.cardLayout = cardLayout;
        this.usuarioDAO = new UsuarioDaoImpl();
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

        add(summaryPanel, BorderLayout.SOUTH);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(new Color(240, 248, 255));

        JButton backButton = new JButton("Voltar ao Menu");
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

        backButton.addActionListener(e -> {
            cardLayout.show(mainPanel, "telaInicial");
        });
        carregarEstatisticas();
    }

    public void carregarEstatisticas() {
        tableModel.setRowCount(0);
        long totalSimulations = 0;
        long totalScore = 0;
        int userCount = 0;

        try {
            List<Usuario> usuarios = usuarioDAO.getAllUsuarios();
            userCount = usuarios.size();

            for (Usuario usuario : usuarios) {

                tableModel.addRow(new Object[]{
                        usuario.getLogin(),
                        usuario.getPontuacao(),
                        usuario.getQtdSimulacoes()
                });
                totalSimulations += usuario.getQtdSimulacoes();
                totalScore += usuario.getPontuacao();
            }


            totalSimulationsLabel.setText("Total de Simulações: " + totalSimulations);

            if (userCount > 0) {
                double avgSims = (double) totalSimulations / userCount;
                double avgScore = (double) totalScore / userCount;
                avgSimulationsLabel.setText(String.format("Média de Simulações por Usuário: %.2f", avgSims));
                avgScoreLabel.setText(String.format("Média de Pontuação por Usuário: %.2f", avgScore));
            } else {
                avgSimulationsLabel.setText("Média de Simulações por Usuário: 0.0");
                avgScoreLabel.setText("Média de Pontuação por Usuário: 0.0");
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar estatísticas do banco de dados: " + ex.getMessage(), "Erro de Banco de Dados", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}
