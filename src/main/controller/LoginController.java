package main.controller; // Crie um pacote 'controller'

import main.dao.impl.UsuarioDaoImpl;
import main.entity.Usuario;
import main.util.SessionManager;
import main.view.LoginView; // Importe a View

import javax.swing.*;
import java.awt.*; // Importe CardLayout
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class LoginController {

    private LoginView view;
    private UsuarioDaoImpl usuarioDAO;
    private JPanel mainPanel;    // Referência ao painel principal para troca de telas
    private CardLayout cardLayout; // Referência ao CardLayout
    private MenuController menuController; // Nova dependência: para atualizar o nome do usuário no menu

    public LoginController(LoginView view, JPanel mainPanel, CardLayout cardLayout, MenuController menuController) {
        this.view = view;
        this.usuarioDAO = new UsuarioDaoImpl(); // Instancia o DAO
        this.mainPanel = mainPanel;
        this.cardLayout = cardLayout;
        this.menuController = menuController; // Atribui a referência ao MenuController

        // Adiciona os ActionListeners aos botões da View
        this.view.addLoginButtonListener(new LoginButtonListener());
        this.view.addRegisterButtonListener(new RegisterButtonListener());
    }

    /**
     * Listener para o botão de Login.
     */
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
                    SessionManager.getInstance().setLoggedInUser(usuario); // Define o usuário na sessão
                    view.showMessage("Login bem-sucedido! Bem-vindo, " + usuario.getLogin() + "!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    view.clearFields(); // Limpa os campos após o login bem-sucedido

                    // Atualiza o nome do usuário na tela de menu
                    if (menuController != null) {
                        menuController.setLoggedInUserName();
                    }
                    cardLayout.show(mainPanel, "telaMenu"); // Navega para a tela de menu
                } else {
                    view.showMessage("Login ou senha incorretos.", "Erro de Login", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                view.showMessage("Erro ao conectar ao banco de dados: " + ex.getMessage(), "Erro de Banco de Dados", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    /**
     * Listener para o botão de Registro.
     */
    class RegisterButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            view.clearFields(); // Opcional: Limpar campos ao navegar para o registro
            cardLayout.show(mainPanel, "telaRegistro"); // Navega para a tela de registro
        }
    }
}