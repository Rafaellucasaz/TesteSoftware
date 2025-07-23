package test.view;

import main.dao.impl.UsuarioDaoImpl;
import main.entity.Usuario;
import main.util.SessionManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class LoginTest {

    private UsuarioDaoImpl mockUsuarioDao;
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private Login loginPanel;
    private Menu menuMock;

    @BeforeEach
    void setup() {
        mockUsuarioDao = mock(UsuarioDaoImpl.class);
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        menuMock = mock(Menu.class);

        loginPanel = new Login(menuMock, mainPanel, cardLayout) {
            {
                this.usuarioDAO = mockUsuarioDao;
            }
        };

        mainPanel.add(loginPanel, "telaLogin");
    }

    @Test
    void testLoginComCamposVazios() {
        JTextField loginField = getComponent(loginPanel, JTextField.class);
        JPasswordField senhaField = getComponent(loginPanel, JPasswordField.class);

        loginField.setText("");
        senhaField.setText("");

        JButton botao = getButtonByText(loginPanel, "Entrar");
        botao.doClick();


    }

    @Test
    void testLoginBemSucedido() throws SQLException {
        Usuario fakeUser = new Usuario(1, "rafael", "1234", "", 10, 2);
        when(mockUsuarioDao.getUsuarioByLogin("rafael")).thenReturn(fakeUser);

        getComponent(loginPanel, JTextField.class).setText("rafael");
        getComponent(loginPanel, JPasswordField.class).setText("1234");

        getButtonByText(loginPanel, "Entrar").doClick();

        assertEquals(fakeUser, SessionManager.getInstance().getLoggedInUser());
        verify(menuMock).setLoggedInUserName();
    }

    @Test
    void testSenhaIncorreta() throws SQLException {
        Usuario fakeUser = new Usuario(1, "rafael", "1234", "", 10, 2);
        when(mockUsuarioDao.getUsuarioByLogin("rafael")).thenReturn(fakeUser);

        getComponent(loginPanel, JTextField.class).setText("rafael");
        getComponent(loginPanel, JPasswordField.class).setText("senhaErrada");

        getButtonByText(loginPanel, "Entrar").doClick();

        assertNotEquals(fakeUser, SessionManager.getInstance().getLoggedInUser());
    }

    @Test
    void testUsuarioNaoEncontrado() throws SQLException {
        when(mockUsuarioDao.getUsuarioByLogin("naoExiste")).thenReturn(null);

        getComponent(loginPanel, JTextField.class).setText("naoExiste");
        getComponent(loginPanel, JPasswordField.class).setText("qualquer");

        getButtonByText(loginPanel, "Entrar").doClick();

        assertNull(SessionManager.getInstance().getLoggedInUser());
    }

    @Test
    void testErroSQLException() throws SQLException {
        when(mockUsuarioDao.getUsuarioByLogin(any())).thenThrow(new SQLException("Falha simulada"));

        getComponent(loginPanel, JTextField.class).setText("erro");
        getComponent(loginPanel, JPasswordField.class).setText("erro");

        getButtonByText(loginPanel, "Entrar").doClick();

        assertNull(SessionManager.getInstance().getLoggedInUser());
    }

    @Test
    void testBotaoRegistroRedireciona() {
        JButton botao = getButtonByText(loginPanel, "Registre-se!");
        botao.doClick();


    }



    private <T extends JComponent> T getComponent(Container parent, Class<T> clazz) {
        for (Component c : parent.getComponents()) {
            if (clazz.isInstance(c)) return clazz.cast(c);
            if (c instanceof Container nested) {
                T result = getComponent(nested, clazz);
                if (result != null) return result;
            }
        }
        return null;
    }

    private JButton getButtonByText(Container parent, String text) {
        for (Component c : parent.getComponents()) {
            if (c instanceof JButton btn && text.equals(btn.getText())) return btn;
            if (c instanceof Container nested) {
                JButton found = getButtonByText(nested, text);
                if (found != null) return found;
            }
        }
        return null;
    }
}
