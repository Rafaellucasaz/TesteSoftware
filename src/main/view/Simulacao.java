package main.view;

import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;

import main.Constantes;
import main.dao.UsuarioDao;
import main.dao.impl.UsuarioDaoImpl;
import main.entity.Criatura;
import main.entity.Tipos;
import main.entity.Usuario;
import main.util.SessionManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Simulacao extends JPanel {

    private UsuarioDao usuario;
    public int rodadas;
    public int nCriaturas;
    public List<Criatura> criaturas;
    public Timer timer;
    public int rodadaAtual = 1;
    public boolean pause = false;

    public JPanel score;
    public JPanel simulacao;
    public JLabel[] scoreLabels;
    public JLabel rodadaLabel;
    public JButton pauseButton;

    public Simulacao(JPanel mainPanel, CardLayout cardLayout) {
        setLayout(new BorderLayout());

        simulacao = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (criaturas != null) {

                    for (Criatura c : criaturas) {
                        int x = (int) (c.getPosX() * 10);
                        int y = 150;
                        int raio = 5;

                        g.setColor(c.getColor());
                        if(c.getTipo() == Tipos.minion){
                            g.fillOval(x, y, raio, raio);
                        }
                        else if(c.getTipo() == Tipos.cluster){
                            g.fillOval(x, y-10, raio*3, raio*3);
                        }
                        else if(c.getTipo() == Tipos.guardiao){
                            g.fillRect(x,y-25,30,30);
                            g.setColor(Color.BLACK);
                            g.setFont(new Font("Arial", Font.BOLD, 20));
                            FontMetrics fm = g.getFontMetrics();
                            int textX = x + (30 - fm.stringWidth("g")) / 2;
                            int textY = (y - 25) + ((30 - fm.getHeight()) / 2) + fm.getAscent();
                            g.drawString("g", textX, textY);
                        }
                        g.setColor(Color.BLACK);

                        g.drawLine((Constantes.comecoHorizonte * 10) - raio, 0, (Constantes.comecoHorizonte * 10) - raio, 400);
                        g.drawLine((Constantes.finalHorizonte * 10) + raio, 0, (Constantes.finalHorizonte * 10) + raio, 400);
                        g.drawLine((Constantes.comecoHorizonte * 10) - raio, 155, (Constantes.finalHorizonte * 10) + raio, 155);

                        g.setColor(Color.WHITE);
                    }
                }
            }
        };

        simulacao.setPreferredSize(new Dimension(Constantes.larguraTela, Constantes.alturaTela / 3));

        rodadaLabel = new JLabel("Rodada Atual: " + rodadaAtual);
        pauseButton = new JButton("Pausar simulação");
        simulacao.add(rodadaLabel);
        simulacao.add(pauseButton);

        add(simulacao, BorderLayout.NORTH);

        score = new JPanel();
        score.setSize(new Dimension(Constantes.larguraTela, Constantes.alturaTela / 3));
        score.setLayout(new GridLayout(1, 1));

        JScrollPane scrollPane = new JScrollPane(score);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        add(scrollPane, BorderLayout.CENTER);
    }

    public int iniciarSimulacao(int rodadas, int nCriaturas) {

        Usuario usuario = SessionManager.getInstance().getLoggedInUser();
        usuario.setQtdSimulacoes(usuario.getQtdSimulacoes()+1);
        UsuarioDao usuarioDao = new UsuarioDaoImpl();

        try {
            usuarioDao.updateUsuario(usuario);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        if (rodadas < 1 || nCriaturas < 2 || nCriaturas > 1000) {
            Window window = SwingUtilities.getWindowAncestor(this);
            if (window != null) {
                window.dispose();
            }
            return 1;
        }

        this.rodadas = rodadas;
        this.nCriaturas = nCriaturas;
        criaturas = new ArrayList<>();
        scoreLabels = new JLabel[nCriaturas];

        pauseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (pause) {
                    pause = false;
                    timer.start();
                    pauseButton.setText("Pausar simulação");
                } else {
                    pause = true;
                    timer.stop();
                    pauseButton.setText("Retomar simulação");
                }
            }
        });

        int linhas = nCriaturas / 2;
        int colunas = nCriaturas / linhas;
        score.removeAll();
        score.setLayout(new GridLayout(linhas, colunas));

        for (int i = 0; i < nCriaturas; i++) {
            Criatura criatura = new Criatura(i, Tipos.minion, 1000000);
            criaturas.add(criatura);

            JLabel scoreLabel = new JLabel("ID: " + i
                    + " | posX: " + String.format("%.2f", criatura.getPosX())
                    + " | ouro: " + criatura.getOuro());
            scoreLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
            scoreLabel.setHorizontalAlignment(SwingConstants.CENTER);
            scoreLabel.setVerticalAlignment(SwingConstants.CENTER);
            scoreLabel.setOpaque(true);
            scoreLabel.setBackground(Color.lightGray);

            scoreLabels[i] = scoreLabel;
            score.add(scoreLabel);
        }
        criaturas.add(new Criatura(nCriaturas+1,Tipos.guardiao,0));
        score.revalidate();
        score.repaint();

        timer = new Timer(5000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                rodadaLabel.setText("Rodada Atual: " + rodadaAtual);

                for (int i = 0; i < criaturas.size(); i++) {
                    Criatura c = criaturas.get(i);
                    c.move();
                    c.roubar(criaturas);

                    scoreLabels[i].setText("ID: " + i
                            + " | posX: " + String.format("%.2f", c.getPosX())
                            + " | ouro: " + c.getOuro());
                }

                rodadaAtual++;
                simulacao.repaint();

                if (rodadaAtual > rodadas) {
                    //usuario ganha 1 ponto por concluir a simulação
                    usuario.setPontuacao(usuario.getPontuacao()+1);
                    try {
                        usuarioDao.updateUsuario(usuario);
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                    timer.stop();
                }
            }
        });

        timer.start();
        return 0;
    }
}
