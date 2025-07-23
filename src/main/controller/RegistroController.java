package main.controller;

import main.dao.impl.UsuarioDaoImpl;
import main.entity.Usuario;
import main.view.RegistroView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class RegistroController {

    private RegistroView view;
    private UsuarioDaoImpl usuarioDAO; // Injetar este DAO é uma boa prática
    private JPanel mainPanel;
    private CardLayout cardLayout;

    public RegistroController(RegistroView view, JPanel mainPanel, CardLayout cardLayout) {
        this.view = view;
        this.usuarioDAO = new UsuarioDaoImpl(); // Idealmente, injetar via construtor
        this.mainPanel = mainPanel;
        this.cardLayout = cardLayout;

        this.view.addRegisterButtonListener(new RegisterButtonListener());
        this.view.addBackButtonListener(new BackButtonListener());
    }

    // Listener para o botão de Registrar
    class RegisterButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String login = view.getLogin();
            String senha = view.getSenha();
            // Agora pegamos o caminho do avatar da View
            String avatarPath = view.getSelectedAvatarPath();

            if (login.isEmpty() || senha.isEmpty() || avatarPath == null || avatarPath.isEmpty()) {
                view.showMessage("Por favor, preencha todos os campos e selecione um avatar.", "Erro de Registro", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                // Verifica se o usuário já existe
                Usuario existingUser = usuarioDAO.getUsuarioByLogin(login);
                if (existingUser != null) {
                    view.showMessage("Login já existe. Por favor, escolha outro.", "Erro de Registro", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // Cria um novo usuário com o caminho do avatar
                Usuario novoUsuario = new Usuario(login, senha, avatarPath, 0, 0);

                // Adiciona o usuário ao banco de dados
                novoUsuario = usuarioDAO.addUsuario(novoUsuario);

                if (novoUsuario.getId() > 0) {
                    view.showMessage("Registro bem-sucedido! Bem-vindo, " + novoUsuario.getLogin() + "!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    view.clearFields(); // Limpa os campos após o sucesso
                    cardLayout.show(mainPanel, "telaLogin"); // Volta para a tela de login
                } else {
                    view.showMessage("Falha ao registrar usuário. Tente novamente.", "Erro de Registro", JOptionPane.ERROR_MESSAGE);
                }

            } catch (SQLException ex) {
                view.showMessage("Erro ao registrar usuário: " + ex.getMessage(), "Erro de Banco de Dados", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    // Listener para o botão de Voltar
    class BackButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            view.clearFields(); // Limpa os campos ao voltar
            cardLayout.show(mainPanel, "telaLogin"); // Volta para a tela de login
        }
    }
}