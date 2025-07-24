package test.controller;

import main.controller.EstatisticasController;
import main.controller.MenuController;
import main.controller.SimulacaoController;
import main.dao.impl.UsuarioDaoImpl;
import main.model.Usuario;
import main.util.SessionManager;
import main.view.MenuView;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Field;
import java.sql.SQLException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class MenuControllerTest {

    @Mock private MenuView view;
    @Mock private SimulacaoController simulacaoController;
    @Mock private EstatisticasController estatisticasController;
    @Mock private JPanel mainPanel;
    @Mock private CardLayout cardLayout;
    @Mock private UsuarioDaoImpl daoMock;

    @Captor private ArgumentCaptor<ActionListener> iniciarCaptor;
    @Captor private ArgumentCaptor<ActionListener> estatisticasCaptor;
    @Captor private ArgumentCaptor<ActionListener> deletarCaptor;
    @Captor private ArgumentCaptor<ActionListener> logoutCaptor;

    private AutoCloseable mocks;
    private MenuController controller;
    private ActionListener iniciarListener;
    private ActionListener estatisticasListener;
    private ActionListener deletarListener;
    private ActionListener logoutListener;

    @Before
    public void setUp() throws Exception {
        mocks = MockitoAnnotations.openMocks(this);


        doNothing().when(view).addIniciarButtonListener(iniciarCaptor.capture());
        doNothing().when(view).addEstatisticasButtonListener(estatisticasCaptor.capture());
        doNothing().when(view).addDeletarUsuarioButtonListener(deletarCaptor.capture());
        doNothing().when(view).addLogoutButtonListener(logoutCaptor.capture());

        controller = new MenuController(
                view, simulacaoController, estatisticasController, mainPanel, cardLayout
        );


        iniciarListener      = iniciarCaptor.getValue();
        estatisticasListener = estatisticasCaptor.getValue();
        deletarListener      = deletarCaptor.getValue();
        logoutListener       = logoutCaptor.getValue();


        Field daoField = MenuController.class.getDeclaredField("usuarioDao");
        daoField.setAccessible(true);
        daoField.set(controller, daoMock);


        SessionManager.getInstance().setLoggedInUser(null);
    }

    @After
    public void tearDown() throws Exception {
        mocks.close();
    }

    @Test
    public void setLoggedInUserName_withUser_showsLoginAndAvatar() {
        Usuario u = new Usuario(42, "login123", "pw", "http://a.png", 0, 0);
        SessionManager.getInstance().setLoggedInUser(u);

        controller.setLoggedInUserName();

        verify(view).setUserInfo("login123", "http://a.png");
    }

    @Test
    public void setLoggedInUserName_withoutUser_showsEmpty() {
        SessionManager.getInstance().setLoggedInUser(null);

        controller.setLoggedInUserName();

        verify(view,times(2)).setUserInfo("", "");
    }

    @Test
    public void iniciar_emptyField_showsWarning() {
        when(view.getNumeroCriaturasText()).thenReturn("");

        iniciarListener.actionPerformed(new ActionEvent(view, 0, "iniciar"));

        verify(view).showMessage(
                "Por favor, preencha o número de criaturas.",
                "Erro de Entrada",
                JOptionPane.WARNING_MESSAGE
        );
        verifyNoInteractions(simulacaoController, cardLayout);
    }

    @Test
    public void iniciar_nonNumeric_showsError() {
        when(view.getNumeroCriaturasText()).thenReturn("abc");

        iniciarListener.actionPerformed(new ActionEvent(view, 0, "iniciar"));

        verify(view).showMessage(
                "Número de criaturas inválido. Digite apenas números inteiros.",
                "Erro de Entrada",
                JOptionPane.ERROR_MESSAGE
        );
        verifyNoInteractions(simulacaoController, cardLayout);
    }

    @Test
    public void iniciar_tooSmall_showsWarning() {
        when(view.getNumeroCriaturasText()).thenReturn("1");

        iniciarListener.actionPerformed(new ActionEvent(view, 0, "iniciar"));

        verify(view).showMessage(
                "Número mínimo de criaturas é 2.",
                "Erro de Entrada",
                JOptionPane.WARNING_MESSAGE
        );
        verifyNoInteractions(simulacaoController, cardLayout);
    }

    @Test
    public void iniciar_tooLarge_showsWarning() {
        when(view.getNumeroCriaturasText()).thenReturn("1001");

        iniciarListener.actionPerformed(new ActionEvent(view, 0, "iniciar"));

        verify(view).showMessage(
                "Número máximo de criaturas é 1000.",
                "Erro de Entrada",
                JOptionPane.WARNING_MESSAGE
        );
        verifyNoInteractions(simulacaoController, cardLayout);
    }

    @Test
    public void iniciar_valid_showsSimulation() {
        when(view.getNumeroCriaturasText()).thenReturn("10");

        iniciarListener.actionPerformed(new ActionEvent(view, 0, "iniciar"));

        verify(simulacaoController).iniciarSimulacao(10);
        verify(view).clearCriaturasField();
        verify(cardLayout).show(mainPanel, "telaSimulacao");
    }

    @Test
    public void estatisticas_navigatesToEstatisticas() {
        estatisticasListener.actionPerformed(new ActionEvent(view, 0, "estatisticas"));

        verify(estatisticasController).carregarEstatisticas();
        verify(cardLayout).show(mainPanel, "telaEstatisticas");
    }

    @Test
    public void deletar_success_navigatesToLogin() throws SQLException {
        Usuario u = new Usuario(42, "john", "pw", "", 0, 0);
        SessionManager.getInstance().setLoggedInUser(u);

        deletarListener.actionPerformed(new ActionEvent(view, 0, "deletar"));

        verify(daoMock).deleteUsuario(42);
        assertNull(SessionManager.getInstance().getLoggedInUser());
        verify(cardLayout).show(mainPanel, "telaLogin");
    }

    @Test
    public void deletar_sqlException_showsError() throws SQLException {
        Usuario u = new Usuario(99, "jane", "pw", "", 0, 0);
        SessionManager.getInstance().setLoggedInUser(u);
        doThrow(new SQLException("fail")).when(daoMock).deleteUsuario(99);

        deletarListener.actionPerformed(new ActionEvent(view, 0, "deletar"));

        verify(view).showMessage(
                "Erro ao deletar usuário: fail",
                "Erro de Banco de Dados",
                JOptionPane.ERROR_MESSAGE
        );
        // session remains
        assertSame(u, SessionManager.getInstance().getLoggedInUser());
        verify(cardLayout, never()).show(eq(mainPanel), anyString());
    }

    @Test
    public void logout_clearsSessionAndNavigatesToLogin() {
        SessionManager.getInstance().setLoggedInUser(new Usuario());
        logoutListener.actionPerformed(new ActionEvent(view, 0, "logout"));

        assertNull(SessionManager.getInstance().getLoggedInUser());
        verify(cardLayout).show(mainPanel, "telaLogin");
    }
}
