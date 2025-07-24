package test.controller;

import main.controller.LoginController;
import main.controller.MenuController;
import main.dao.impl.UsuarioDaoImpl;
import main.model.Usuario;
import main.util.SessionManager;
import main.view.LoginView;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.mockito.*;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Field;
import java.sql.SQLException;

public class LoginControllerTest {

    @Mock private LoginView view;
    @Mock private JPanel mainPanel;
    @Mock private CardLayout cardLayout;
    @Mock private MenuController menuController;
    @Mock private UsuarioDaoImpl daoMock; // Mock do DAO

    @Captor private ArgumentCaptor<ActionListener> loginListenerCaptor;
    @Captor private ArgumentCaptor<ActionListener> registerListenerCaptor;

    private AutoCloseable mocks;
    private LoginController controller;
    private ActionListener loginListener;
    private ActionListener registerListener;

    @Before
    public void setUp() throws Exception {

        mocks = MockitoAnnotations.openMocks(this);


        doNothing().when(view).addLoginButtonListener(loginListenerCaptor.capture());
        doNothing().when(view).addRegisterButtonListener(registerListenerCaptor.capture());


        controller = new LoginController(view, mainPanel, cardLayout, menuController);


        loginListener    = loginListenerCaptor.getValue();
        registerListener = registerListenerCaptor.getValue();


        Field daoField = LoginController.class.getDeclaredField("usuarioDAO");
        daoField.setAccessible(true);
        daoField.set(controller, daoMock);


        SessionManager.getInstance().setLoggedInUser(null);
    }

    @After
    public void tearDown() throws Exception {
        mocks.close();
    }

    @Test
    public void deveAvisarQuandoCamposVazios() {

        when(view.getLogin()).thenReturn("");
        when(view.getSenha()).thenReturn("");


        loginListener.actionPerformed(new ActionEvent(view, 0, "login"));


        verify(view).showMessage(
                "Por favor, preencha todos os campos.",
                "Erro de Login",
                JOptionPane.WARNING_MESSAGE
        );

        verifyNoInteractions(daoMock, cardLayout, menuController);
    }

    @Test
    public void deveLogarComSucessoENavegarParaMenu() throws SQLException {
        String user = "alice";
        String pass = "1234";

        Usuario fakeUser = new Usuario(1, user, pass, "assets/avatars/avatar_alice.png", 150, 5);


        when(view.getLogin()).thenReturn(user);
        when(view.getSenha()).thenReturn(pass);

        when(daoMock.getUsuarioByLogin(user)).thenReturn(fakeUser);


        loginListener.actionPerformed(new ActionEvent(view, 0, "login"));


        assertSame(fakeUser, SessionManager.getInstance().getLoggedInUser());


        verify(view).showMessage(
                "Login bem-sucedido! Bem-vindo, " + user + "!",
                "Sucesso",
                JOptionPane.INFORMATION_MESSAGE
        );
        verify(view).clearFields();
        verify(menuController).setLoggedInUserName();
        verify(cardLayout).show(mainPanel, "telaMenu");
    }

    @Test
    public void deveMostrarErroEmCredenciaisInvalidas() throws SQLException {
        String login = "login123";
        String senhaCorreta = "senha123";

        Usuario existingUser = new Usuario(2, login, senhaCorreta, "assets/avatars/default.png", 0, 0);


        when(view.getLogin()).thenReturn(login);
        when(view.getSenha()).thenReturn("wrong_password");

        when(daoMock.getUsuarioByLogin(login)).thenReturn(existingUser);


        loginListener.actionPerformed(new ActionEvent(view, 0, "login"));


        verify(view).showMessage(
                "Login ou senha incorretos.",
                "Erro de Login",
                JOptionPane.ERROR_MESSAGE
        );

        verify(view, never()).clearFields();

        verifyNoInteractions(cardLayout, menuController);
    }

    @Test
    public void deveLidarComSQLExceptionGraciosamente() throws SQLException {
        String user = "eve";
        String pass = "pw";


        when(view.getLogin()).thenReturn(user);
        when(view.getSenha()).thenReturn(pass);

        when(daoMock.getUsuarioByLogin(user)).thenThrow(new SQLException("Erro de conexão simulado"));


        loginListener.actionPerformed(new ActionEvent(view, 0, "login"));


        verify(view).showMessage(
                startsWith("Erro ao conectar ao banco de dados:"),
                eq("Erro de Banco de Dados"),
                eq(JOptionPane.ERROR_MESSAGE)
        );

        verifyNoInteractions(cardLayout, menuController);
    }

    @Test
    public void deveNavegarParaTelaRegistroAoRegistrar() {

        registerListener.actionPerformed(new ActionEvent(view, 0, "register"));
        verify(view).clearFields();
        verify(cardLayout).show(mainPanel, "telaRegistro");
    }
}