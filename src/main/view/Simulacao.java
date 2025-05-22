package main.view;

import main.Constantes;
import main.entity.Criatura;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class Simulacao extends JPanel {

    private int rodadas;
    private int nCriaturas;
    private Criatura[] criaturas;
    private Timer timer;
    private int rodadaAtual = 1;
    private boolean pause = false;


    private JPanel score;
    private JPanel simulacao;
    private JLabel[] scoreLabels;
    private JLabel rodadaLabel;
    private JButton pauseButton;


    public Simulacao(JPanel mainPanel, CardLayout cardLayout) {
        setLayout(new BorderLayout());

        simulacao =   new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (criaturas != null) {

                    for (int i = 0; i < nCriaturas; i++) {
                        Criatura c = criaturas[i];

                        int x = (int) (c.getPosX() * 10);
                        int y = 150;
                        int largura = 5;
                        int altura = 5;


                        g.setColor(c.getColor());
                        int raio = 5;
                        g.fillOval(x, y , raio, raio);
                        g.setColor(Color.black);


                        g.drawLine((Constantes.comecoHorizonte*10) - raio,0,(Constantes.comecoHorizonte*10) - raio,400);
                        g.drawLine((Constantes.finalHorizonte*10) + raio,0,(Constantes.finalHorizonte*10) + raio,400);
                        g.drawLine((Constantes.comecoHorizonte*10) - raio,155,(Constantes.finalHorizonte*10) + raio,155);

                        g.setColor(Color.WHITE);

                    }
                }
            }
        };

        simulacao.setPreferredSize(new Dimension(Constantes.larguraTela, Constantes.alturaTela/3));

        rodadaLabel = new JLabel("Rodada Atual: " + rodadaAtual);
        pauseButton = new JButton("Pausar simulação");
        simulacao.add(rodadaLabel);
        simulacao.add(pauseButton);

        add(simulacao, BorderLayout.NORTH);
        score = new JPanel();
        score.setSize(new Dimension(Constantes.larguraTela,Constantes.alturaTela/3));
        score.setLayout(new GridLayout(1, 1));

        JScrollPane scrollPane = new JScrollPane(score);


        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        add(scrollPane,BorderLayout.CENTER);
    }

    public void iniciarSimulacao(int rodadas,int nCriaturas){
        this.rodadas = rodadas;
        this.nCriaturas = nCriaturas;
        criaturas = new Criatura[nCriaturas];
        scoreLabels = new JLabel[nCriaturas];


        pauseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(pause){
                    pause = false;
                    timer.start();
                    pauseButton.setText("Pausar simulação");
                }
                else{
                    pause = true;
                    timer.stop();
                    pauseButton.setText("Retomar simulação");
                }
            }
        });

        int linhas = nCriaturas/2;
        int colunas = nCriaturas/linhas;
        score.removeAll();
        score.setLayout(new GridLayout(linhas, colunas));

        for (int i = 0; i < nCriaturas; i++) {
            Criatura criatura = new Criatura(i);
            criaturas[i] = criatura;

            JLabel scoreLabel = new JLabel("ID: " + i
                    + " | posX: " + String.format("%.2f", criaturas[i].getPosX())
                    + " | ouro: " + criaturas[i].getOuro());
            scoreLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
            scoreLabel.setHorizontalAlignment(SwingConstants.CENTER);
            scoreLabel.setVerticalAlignment(SwingConstants.CENTER);
            scoreLabel.setOpaque(true);
            scoreLabel.setBackground(Color.lightGray);
            scoreLabels[i] = scoreLabel;
            score.add(scoreLabel);
        }

        score.revalidate();
        score.repaint();

        timer = new Timer(5000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                rodadaLabel.setText("Rodada Atual: " + rodadaAtual);
                for (int i = 0; i < nCriaturas; i++) {
                    criaturas[i].move();
                    criaturas[i].roubar(criaturas);
                    scoreLabels[i].setText("ID: " + i
                            + " | posX: " + String.format("%.2f", criaturas[i].getPosX())
                            + " | ouro: " + criaturas[i].getOuro());
                }
                rodadaAtual++;
                simulacao.repaint();


                if (rodadaAtual > rodadas) {
                    timer.stop();
                    System.out.println("Simulação finalizada.");
                }
            }
        });
        timer.start();
    }


    public int getnCriaturas() {
        return nCriaturas;
    }

    public void setnCriaturas(int nCriaturas) {
        this.nCriaturas = nCriaturas;
    }

    public int getRodadas() {
        return rodadas;
    }

    public void setRodadas(int rodadas) {
        this.rodadas = rodadas;
    }
}