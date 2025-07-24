package test.controller;

import main.controller.RegistroController;
import main.dao.impl.UsuarioDaoImpl;
import main.model.Usuario;
import main.view.RegistroView;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Field;
import java.sql.SQLException;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

public class RegistroControllerTest {

    @Mock
    private RegistroView mockView;
    @Mock
    private UsuarioDaoImpl mockUsuarioDAO;
    @Mock
    private JPanel mockMainPanel;
    @Mock
    private CardLayout mockCardLayout;


    @Captor
    private ArgumentCaptor<ActionListener> registerButtonListenerCaptor;
    @Captor
    private ArgumentCaptor<ActionListener> backButtonListenerCaptor;

    // Injeta os mocks no controller
    @InjectMocks
    private RegistroController registroController;

    private ActionListener registerButtonListener;
    private ActionListener backButtonListener;

    @Before
    public void setUp() throws Exception {

        MockitoAnnotations.openMocks(this);

        doNothing().when(mockView).addRegisterButtonListener(registerButtonListenerCaptor.capture());
        doNothing().when(mockView).addBackButtonListener(backButtonListenerCaptor.capture());


        registroController = new RegistroController(mockView, mockMainPanel, mockCardLayout);

        registerButtonListener = registerButtonListenerCaptor.getValue();
        backButtonListener = backButtonListenerCaptor.getValue();


        Field daoField = RegistroController.class.getDeclaredField("usuarioDAO");
        daoField.setAccessible(true);
        daoField.set(registroController, mockUsuarioDAO);
    }

    @Test
    public void deveAvisarQuandoLoginEstaVazio() {

        when(mockView.getLogin()).thenReturn("");
        when(mockView.getSenha()).thenReturn("senha123");
        when(mockView.getSelectedAvatarPath()).thenReturn("assets/avatars/default.png");

        registerButtonListener.actionPerformed(new ActionEvent(mockView, 0, "register"));

        verify(mockView).showMessage(
                "Por favor, preencha todos os campos e selecione um avatar.",
                "Erro de Registro",
                JOptionPane.WARNING_MESSAGE
        );
        verifyNoInteractions(mockUsuarioDAO, mockCardLayout);
    }

    @Test
    public void deveAvisarQuandoSenhaEstaVazio() {

        when(mockView.getLogin()).thenReturn("usuario1");
        when(mockView.getSenha()).thenReturn("");
        when(mockView.getSelectedAvatarPath()).thenReturn("assets/avatars/default.png");

        registerButtonListener.actionPerformed(new ActionEvent(mockView, 0, "register"));

        verify(mockView).showMessage(
                "Por favor, preencha todos os campos e selecione um avatar.",
                "Erro de Registro",
                JOptionPane.WARNING_MESSAGE
        );
        verifyNoInteractions(mockUsuarioDAO, mockCardLayout);
    }

    @Test
    public void deveAvisarQuandoAvatarNaoSelecionado() {

        when(mockView.getLogin()).thenReturn("novoUser");
        when(mockView.getSenha()).thenReturn("senha123");
        when(mockView.getSelectedAvatarPath()).thenReturn(null);

        registerButtonListener.actionPerformed(new ActionEvent(mockView, 0, "register"));

        verify(mockView).showMessage(
                "Por favor, preencha todos os campos e selecione um avatar.",
                "Erro de Registro",
                JOptionPane.WARNING_MESSAGE
        );
        verifyNoInteractions(mockUsuarioDAO, mockCardLayout);
    }
    @Test
    public void deveAvisarQuandoAvatarVazio() {

        when(mockView.getLogin()).thenReturn("novoUser");
        when(mockView.getSenha()).thenReturn("senha123");
        when(mockView.getSelectedAvatarPath()).thenReturn("");

        registerButtonListener.actionPerformed(new ActionEvent(mockView, 0, "register"));

        verify(mockView).showMessage(
                "Por favor, preencha todos os campos e selecione um avatar.",
                "Erro de Registro",
                JOptionPane.WARNING_MESSAGE
        );
        verifyNoInteractions(mockUsuarioDAO, mockCardLayout);
    }


    @Test
    public void deveAvisarQuandoLoginJaExiste() throws SQLException {

        String loginExistente = "usuarioExistente";
        when(mockView.getLogin()).thenReturn(loginExistente);
        when(mockView.getSenha()).thenReturn("senhaNova");
        when(mockView.getSelectedAvatarPath()).thenReturn("assets/avatars/avatar1.png");


        when(mockUsuarioDAO.getUsuarioByLogin(loginExistente)).thenReturn(new Usuario(loginExistente, "senhaAntiga", "assets/avatars/old.png", 100, 5));

        registerButtonListener.actionPerformed(new ActionEvent(mockView, 0, "register"));

        verify(mockView).showMessage(
                "Login já existe. Por favor, escolha outro.",
                "Erro de Registro",
                JOptionPane.WARNING_MESSAGE
        );
        verify(mockUsuarioDAO, never()).addUsuario(any(Usuario.class));
        verifyNoInteractions(mockCardLayout);
    }

    @Test
    public void deveRegistrarComSucessoENavegarParaTelaLogin() throws SQLException {

        String novoLogin = "novoUsuario";
        String novaSenha = "senhaNova";
        String novoAvatar = "assets/avatars/new_avatar.png";
        Usuario usuarioParaAdicionar = new Usuario(novoLogin, novaSenha, novoAvatar, 0, 0);
        Usuario usuarioRegistrado = new Usuario(1, novoLogin, novaSenha, novoAvatar, 0, 0);

        when(mockView.getLogin()).thenReturn(novoLogin);
        when(mockView.getSenha()).thenReturn(novaSenha);
        when(mockView.getSelectedAvatarPath()).thenReturn(novoAvatar);

        // Simula que o login não existe
        when(mockUsuarioDAO.getUsuarioByLogin(novoLogin)).thenReturn(null);
        // Simula que addUsuario retorna um usuário com ID (sucesso)
        when(mockUsuarioDAO.addUsuario(any(Usuario.class))).thenReturn(usuarioRegistrado);

        registerButtonListener.actionPerformed(new ActionEvent(mockView, 0, "register"));

        // Verifica se o usuário foi adicionado ao DAO
        verify(mockUsuarioDAO).addUsuario(argThat(u ->
                u.getLogin().equals(novoLogin) &&
                        u.getSenha().equals(novaSenha) &&
                        u.getAvatarURL().equals(novoAvatar) &&
                        u.getPontuacao() == 0 &&
                        u.getQtdSimulacoes() == 0
        ));
        verify(mockView).showMessage(
                "Registro bem-sucedido! Bem-vindo, " + novoLogin + "!",
                "Sucesso",
                JOptionPane.INFORMATION_MESSAGE
        );
        verify(mockView).clearFields();
        verify(mockCardLayout).show(mockMainPanel, "telaLogin");
    }

    @Test
    public void deveMostrarErroQuandoFalhaAoAdicionarUsuarioNoBanco() throws SQLException {

        String novoLogin = "userFalha";
        String novaSenha = "passFalha";
        String novoAvatar = "assets/avatars/fail_avatar.png";
        Usuario usuarioParaAdicionar = new Usuario(novoLogin, novaSenha, novoAvatar, 0, 0);
        Usuario usuarioSemId = new Usuario(0, novoLogin, novaSenha, novoAvatar, 0, 0); // Simula falha ao gerar ID

        when(mockView.getLogin()).thenReturn(novoLogin);
        when(mockView.getSenha()).thenReturn(novaSenha);
        when(mockView.getSelectedAvatarPath()).thenReturn(novoAvatar);

        when(mockUsuarioDAO.getUsuarioByLogin(novoLogin)).thenReturn(null);
        when(mockUsuarioDAO.addUsuario(any(Usuario.class))).thenReturn(usuarioSemId);

        registerButtonListener.actionPerformed(new ActionEvent(mockView, 0, "register"));

        verify(mockUsuarioDAO).addUsuario(any(Usuario.class));
        verify(mockView).showMessage(
                "Falha ao registrar usuário. Tente novamente.",
                "Erro de Registro",
                JOptionPane.ERROR_MESSAGE
        );
        verify(mockView, never()).clearFields();
        verify(mockCardLayout, never()).show(any(JPanel.class), anyString());
    }


    @Test
    public void deveLidarComSQLExceptionAoRegistrarUsuario() throws SQLException {

        String login = "dbErrorUser";
        String senha = "dbErrorPass";
        String avatar = "assets/avatars/error.png";

        when(mockView.getLogin()).thenReturn(login);
        when(mockView.getSenha()).thenReturn(senha);
        when(mockView.getSelectedAvatarPath()).thenReturn(avatar);

        when(mockUsuarioDAO.getUsuarioByLogin(login)).thenReturn(null);
        when(mockUsuarioDAO.addUsuario(any(Usuario.class))).thenThrow(new SQLException("Problema de conexão com o DB"));

        registerButtonListener.actionPerformed(new ActionEvent(mockView, 0, "register"));

        verify(mockView).showMessage(
                startsWith("Erro ao registrar usuário: Problema de conexão com o DB"),
                eq("Erro de Banco de Dados"),
                eq(JOptionPane.ERROR_MESSAGE)
        );
        verify(mockUsuarioDAO).addUsuario(any(Usuario.class));
        verify(mockView, never()).clearFields();
        verify(mockCardLayout, never()).show(any(JPanel.class), anyString());
    }

    @Test
    public void deveVoltarParaTelaLoginAoClicarEmVoltar() {

        backButtonListener.actionPerformed(new ActionEvent(mockView, 0, "back"));

        verify(mockView).clearFields();
        verify(mockCardLayout).show(mockMainPanel, "telaLogin");
        verifyNoInteractions(mockUsuarioDAO);
    }
}