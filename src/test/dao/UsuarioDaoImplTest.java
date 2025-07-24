package test.dao;

import main.dao.impl.UsuarioDaoImpl;
import main.model.Usuario;
import main.util.Db;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class UsuarioDaoImplTest {

    @Mock
    private Connection mockConnection;
    @Mock
    private PreparedStatement mockPstmt;
    @Mock
    private ResultSet mockResultSet;
    @Mock
    private Statement mockStatement;

    private UsuarioDaoImpl usuarioDao;
    private AutoCloseable mocksAutoCloseable;


    private MockedStatic<Db> dbMockedStatic;

    @BeforeEach
    void setUp() throws Exception {

        mocksAutoCloseable = MockitoAnnotations.openMocks(this);
        usuarioDao = new UsuarioDaoImpl();


        dbMockedStatic = mockStatic(Db.class);

        dbMockedStatic.when(Db::getConnection).thenReturn(mockConnection);
    }

    @AfterEach
    void tearDown() throws Exception {

        if (mocksAutoCloseable != null) {
            mocksAutoCloseable.close();
        }
        if (dbMockedStatic != null) {
            dbMockedStatic.close();
        }
    }

    @Test

    void deveAdicionarUsuarioEretornarComIdGerado() throws SQLException {

        Usuario usuario = new Usuario("novoUser", "senha123", "avatar.png", 0, 0);


        when(mockConnection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS)))
                .thenReturn(mockPstmt);


        when(mockPstmt.executeUpdate()).thenReturn(1);
        when(mockPstmt.getGeneratedKeys()).thenReturn(mockResultSet);


        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt(1)).thenReturn(99);


        Usuario resultado = usuarioDao.addUsuario(usuario);


        assertNotNull(resultado);
        assertEquals(99, resultado.getId());
        assertEquals("novoUser", resultado.getLogin());


        verify(mockPstmt).setString(1, "novoUser");
        verify(mockPstmt).setString(2, "senha123");
        verify(mockPstmt).setString(3, "avatar.png");
        verify(mockPstmt).setInt(4, 0);
        verify(mockPstmt).setInt(5, 0);
        verify(mockPstmt).executeUpdate();
        verify(mockPstmt).getGeneratedKeys();


        verify(mockResultSet).next();
        verify(mockResultSet).getInt(1);


        verify(mockConnection).close();
        verify(mockPstmt).close();
        verify(mockResultSet).close();
    }

    @Test
    void deveLancarSQLExceptionAoFalharNaAdicaoDeUsuario() throws SQLException {

        Usuario usuario = new Usuario("erroUser", "senha", "avatar.png", 0, 0);


        when(mockConnection.prepareStatement(anyString(), anyInt())).thenThrow(new SQLException("Erro de conexão no INSERT"));


        SQLException thrown = assertThrows(SQLException.class, () -> {
            usuarioDao.addUsuario(usuario);
        });

        assertEquals("Erro de conexão no INSERT", thrown.getMessage());

        verify(mockConnection).close();
    }

    @Test
    void deveBuscarUsuarioPorIdComSucesso() throws SQLException {

        int userId = 1;

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPstmt);


        when(mockPstmt.executeQuery()).thenReturn(mockResultSet);


        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getInt("id")).thenReturn(userId);
        when(mockResultSet.getString("login")).thenReturn("userTeste");
        when(mockResultSet.getString("senha")).thenReturn("passTeste");
        when(mockResultSet.getString("avatarURL")).thenReturn("urlTeste");
        when(mockResultSet.getInt("pontuacao")).thenReturn(100);
        when(mockResultSet.getInt("qtdSimulacoes")).thenReturn(5);


        Usuario usuarioEncontrado = usuarioDao.getUsuarioById(userId);


        assertNotNull(usuarioEncontrado);
        assertEquals(userId, usuarioEncontrado.getId());
        assertEquals("userTeste", usuarioEncontrado.getLogin());
        assertEquals("passTeste", usuarioEncontrado.getSenha());
        assertEquals("urlTeste", usuarioEncontrado.getAvatarURL());
        assertEquals(100, usuarioEncontrado.getPontuacao());
        assertEquals(5, usuarioEncontrado.getQtdSimulacoes());


        verify(mockPstmt).setInt(1, userId);
        verify(mockPstmt).executeQuery();
        verify(mockResultSet, times(1)).next();
        verify(mockResultSet).getInt("id");
        verify(mockResultSet).getString("login");


        verify(mockConnection).close();
        verify(mockPstmt).close();
        verify(mockResultSet).close();
    }

    @Test

    void deveRetornarNullAoBuscarUsuarioPorIdInexistente() throws SQLException {

        int userId = 999;
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPstmt);
        when(mockPstmt.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);


        Usuario usuarioEncontrado = usuarioDao.getUsuarioById(userId);


        assertNull(usuarioEncontrado);
        verify(mockPstmt).setInt(1, userId);
        verify(mockPstmt).executeQuery();
        verify(mockResultSet).next();

        verify(mockConnection).close();
        verify(mockPstmt).close();
        verify(mockResultSet).close();
    }

    @Test
    void deveBuscarUsuarioPorLoginComSucesso() throws SQLException {

        String userLogin = "userLogin";
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPstmt);
        when(mockPstmt.executeQuery()).thenReturn(mockResultSet);

        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getInt("id")).thenReturn(10);
        when(mockResultSet.getString("login")).thenReturn(userLogin);
        when(mockResultSet.getString("senha")).thenReturn("senhaLogin");
        when(mockResultSet.getString("avatarURL")).thenReturn("urlLogin");
        when(mockResultSet.getInt("pontuacao")).thenReturn(200);
        when(mockResultSet.getInt("qtdSimulacoes")).thenReturn(10);


        Usuario usuarioEncontrado = usuarioDao.getUsuarioByLogin(userLogin);


        assertNotNull(usuarioEncontrado);
        assertEquals(userLogin, usuarioEncontrado.getLogin());


        verify(mockPstmt).setString(1, userLogin);
        verify(mockPstmt).executeQuery();
        verify(mockResultSet, times(1)).next();

        verify(mockConnection).close();
        verify(mockPstmt).close();
        verify(mockResultSet).close();
    }

    @Test
    void deveRetornarNullAoBuscarUsuarioPorLoginInexistente() throws SQLException {

        String userLogin = "naoExiste";
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPstmt);
        when(mockPstmt.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);


        Usuario usuarioEncontrado = usuarioDao.getUsuarioByLogin(userLogin);


        assertNull(usuarioEncontrado);
        verify(mockPstmt).setString(1, userLogin);
        verify(mockPstmt).executeQuery();
        verify(mockResultSet).next();

        verify(mockConnection).close();
        verify(mockPstmt).close();
        verify(mockResultSet).close();
    }

    @Test
    void deveAtualizarUsuarioExistenteComSucesso() throws SQLException {

        Usuario usuarioParaAtualizar = new Usuario(5, "updatedUser", "newPass", "newAvatar.jpg", 150, 8);


        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPstmt);

        when(mockPstmt.executeUpdate()).thenReturn(1);


        boolean atualizado = usuarioDao.updateUsuario(usuarioParaAtualizar);


        assertTrue(atualizado);

        verify(mockPstmt).setString(1, usuarioParaAtualizar.getLogin());
        verify(mockPstmt).setString(2, usuarioParaAtualizar.getSenha());
        verify(mockPstmt).setString(3, usuarioParaAtualizar.getAvatarURL());
        verify(mockPstmt).setInt(4, usuarioParaAtualizar.getPontuacao());
        verify(mockPstmt).setInt(5, usuarioParaAtualizar.getQtdSimulacoes());
        verify(mockPstmt).setInt(6, usuarioParaAtualizar.getId());
        verify(mockPstmt).executeUpdate();

        verify(mockConnection).close();
        verify(mockPstmt).close();
    }

    @Test
    void deveRetornarFalseAoTentarAtualizarUsuarioInexistente() throws SQLException {

        Usuario usuarioParaAtualizar = new Usuario(99, "nonExistent", "pass", "avatar.png", 0, 0);

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPstmt);
        when(mockPstmt.executeUpdate()).thenReturn(0); // Simula zero linhas afetadas (não encontrado)


        boolean atualizado = usuarioDao.updateUsuario(usuarioParaAtualizar);

        assertFalse(atualizado);

        verify(mockPstmt).executeUpdate();
        verify(mockConnection).close();
        verify(mockPstmt).close();
    }

    @Test
    void deveDeletarUsuarioExistenteComSucesso() throws SQLException {
        int userId = 7;
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPstmt);
        when(mockPstmt.executeUpdate()).thenReturn(1); // Simula uma linha afetada

        boolean deletado = usuarioDao.deleteUsuario(userId);

        assertTrue(deletado);
        verify(mockPstmt).setInt(1, userId);
        verify(mockPstmt).executeUpdate();

        verify(mockConnection).close();
        verify(mockPstmt).close();
    }

    @Test
    void deveRetornarFalseAoTentarDeletarUsuarioInexistente() throws SQLException {
        int userId = 999;
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPstmt);
        when(mockPstmt.executeUpdate()).thenReturn(0); // Simula zero linhas afetadas

        boolean deletado = usuarioDao.deleteUsuario(userId);

        assertFalse(deletado);
        verify(mockPstmt).setInt(1, userId);
        verify(mockPstmt).executeUpdate();

        verify(mockConnection).close();
        verify(mockPstmt).close();
    }

    @Test
    void deveRetornarTodosOsUsuariosQuandoHouverDados() throws SQLException {
        when(mockConnection.createStatement()).thenReturn(mockStatement);
        when(mockStatement.executeQuery(anyString())).thenReturn(mockResultSet);


        when(mockResultSet.next()).thenReturn(true, true, false);

        when(mockResultSet.getInt("id")).thenReturn(1).thenReturn(2);
        when(mockResultSet.getString("login")).thenReturn("user1").thenReturn("user2");
        when(mockResultSet.getString("senha")).thenReturn("pass1").thenReturn("pass2");
        when(mockResultSet.getString("avatarURL")).thenReturn("url1").thenReturn("url2");
        when(mockResultSet.getInt("pontuacao")).thenReturn(100).thenReturn(200);
        when(mockResultSet.getInt("qtdSimulacoes")).thenReturn(5).thenReturn(10);


        List<Usuario> usuarios = usuarioDao.getAllUsuarios();


        assertNotNull(usuarios);
        assertEquals(2, usuarios.size());


        assertEquals(1, usuarios.get(0).getId());
        assertEquals("user1", usuarios.get(0).getLogin());
        assertEquals(100, usuarios.get(0).getPontuacao());


        assertEquals(2, usuarios.get(1).getId());
        assertEquals("user2", usuarios.get(1).getLogin());
        assertEquals(200, usuarios.get(1).getPontuacao());


        verify(mockStatement).executeQuery(anyString());
        verify(mockResultSet, times(3)).next();


        verify(mockConnection).close();
        verify(mockStatement).close();
        verify(mockResultSet).close();
    }

    @Test
    @DisplayName("Deve retornar uma lista vazia quando não há usuários")
    void deveRetornarListaVaziaQuandoNaoHaUsuarios() throws SQLException {

        when(mockConnection.createStatement()).thenReturn(mockStatement);
        when(mockStatement.executeQuery(anyString())).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false); // Simula que não há resultados


        List<Usuario> usuarios = usuarioDao.getAllUsuarios();


        assertNotNull(usuarios);
        assertTrue(usuarios.isEmpty());

        verify(mockStatement).executeQuery(anyString());
        verify(mockResultSet).next();

        verify(mockConnection).close();
        verify(mockStatement).close();
        verify(mockResultSet).close();
    }
}