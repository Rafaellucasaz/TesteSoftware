package main.controller; // Crie um pacote 'controller' para esta classe

import main.dao.impl.UsuarioDaoImpl;
import main.entity.Usuario;
import main.view.RegistroView; // Importe a View

import javax.swing.*;
import java.awt.*; // Importe CardLayout
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class RegistroController {

    private RegistroView view;
    private UsuarioDaoImpl usuarioDAO;
    private JPanel mainPanel; // Referência ao painel principal para troca de telas
    private CardLayout cardLayout; // Referência ao CardLayout para troca de telas

    public RegistroController(RegistroView view, JPanel mainPanel, CardLayout cardLayout) {
        this.view = view;
        this.usuarioDAO = new UsuarioDaoImpl(); // Instancia o DAO
        this.mainPanel = mainPanel;
        this.cardLayout = cardLayout;

        // Adiciona os ActionListeners aos botões da View
        this.view.addRegisterButtonListener(new RegisterButtonListener());
        this.view.addBackButtonListener(new BackButtonListener());
    }

    // Listener para o botão de Registrar
    class RegisterButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String login = view.getLogin();
            String senha = view.getSenha();
            String avatarURL = view.getAvatarURL();

            if (login.isEmpty() || senha.isEmpty() || avatarURL == null || avatarURL.isEmpty()) {
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

                // Cria um novo usuário
                Usuario novoUsuario = new Usuario(login, senha, avatarURL, 0, 0);

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
            view.clearFields(); // Opcional: Limpar campos ao voltar
            cardLayout.show(mainPanel, "telaLogin"); // Volta para a tela de login
        }
    }
}