package main.controller;

import main.dao.impl.UsuarioDaoImpl;
import main.entity.Usuario;
import main.util.SessionManager;
import main.view.LoginView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class LoginController {

    private LoginView view;
    private UsuarioDaoImpl usuarioDAO;
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private MenuController menuController;

    public LoginController(LoginView view, JPanel mainPanel, CardLayout cardLayout, MenuController menuController) {
        this.view = view;
        this.usuarioDAO = new UsuarioDaoImpl();
        this.mainPanel = mainPanel;
        this.cardLayout = cardLayout;
        this.menuController = menuController;


        this.view.addLoginButtonListener(new LoginButtonListener());
        this.view.addRegisterButtonListener(new RegisterButtonListener());
    }


    class LoginButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String login = view.getLogin();
            String senha = view.getSenha();

            if (login.isEmpty() || senha.isEmpty()) {
                view.showMessage("Por favor, preencha todos os campos.", "Erro de Login", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                Usuario usuario = usuarioDAO.getUsuarioByLogin(login);

                if (usuario != null && usuario.getSenha().equals(senha)) {
                    SessionManager.getInstance().setLoggedInUser(usuario);
                    view.showMessage("Login bem-sucedido! Bem-vindo, " + usuario.getLogin() + "!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    view.clearFields();


                    if (menuController != null) {
                        menuController.setLoggedInUserName();
                    }
                    cardLayout.show(mainPanel, "telaMenu");
                } else {
                    view.showMessage("Login ou senha incorretos.", "Erro de Login", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                view.showMessage("Erro ao conectar ao banco de dados: " + ex.getMessage(), "Erro de Banco de Dados", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }


    class RegisterButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            view.clearFields();
            cardLayout.show(mainPanel, "telaRegistro");
        }
    }
}