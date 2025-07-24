package main.controller;

import main.dao.impl.UsuarioDaoImpl;
import main.model.Usuario;
import main.view.RegistroView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class RegistroController {

    private RegistroView view;
    private UsuarioDaoImpl usuarioDAO;
    private JPanel mainPanel;
    private CardLayout cardLayout;

    public RegistroController(RegistroView view, JPanel mainPanel, CardLayout cardLayout) {
        this.view = view;
        this.usuarioDAO = new UsuarioDaoImpl();
        this.mainPanel = mainPanel;
        this.cardLayout = cardLayout;

        this.view.addRegisterButtonListener(new RegisterButtonListener());
        this.view.addBackButtonListener(new BackButtonListener());
    }


    class RegisterButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String login = view.getLogin();
            String senha = view.getSenha();

            String avatarPath = view.getSelectedAvatarPath();

            if (login.isEmpty() || senha.isEmpty() || avatarPath == null || avatarPath.isEmpty()) {
                view.showMessage("Por favor, preencha todos os campos e selecione um avatar.", "Erro de Registro", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {

                Usuario existingUser = usuarioDAO.getUsuarioByLogin(login);
                if (existingUser != null) {
                    view.showMessage("Login já existe. Por favor, escolha outro.", "Erro de Registro", JOptionPane.WARNING_MESSAGE);
                    return;
                }


                Usuario novoUsuario = new Usuario(login, senha, avatarPath, 0, 0);


                novoUsuario = usuarioDAO.addUsuario(novoUsuario);

                if (novoUsuario.getId() > 0) {
                    view.showMessage("Registro bem-sucedido! Bem-vindo, " + novoUsuario.getLogin() + "!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    view.clearFields();
                    cardLayout.show(mainPanel, "telaLogin");
                } else {
                    view.showMessage("Falha ao registrar usuário. Tente novamente.", "Erro de Registro", JOptionPane.ERROR_MESSAGE);
                }

            } catch (SQLException ex) {
                view.showMessage("Erro ao registrar usuário: " + ex.getMessage(), "Erro de Banco de Dados", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }


    class BackButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            view.clearFields();
            cardLayout.show(mainPanel, "telaLogin");
        }
    }
}