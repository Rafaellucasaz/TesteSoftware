package test.dao;

import main.dao.UsuarioDao;
import main.dao.impl.UsuarioDaoImpl;
import main.entity.Usuario;
import main.util.Db;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UsuarioDaoImplTest {

    private UsuarioDao usuarioDao;
    private Connection connection;
    private PreparedStatement pstmt;
    private ResultSet resultSet;
    private Statement stmt;

    @BeforeEach
    void setup() throws Exception {
        usuarioDao = new UsuarioDaoImpl();

        connection = mock(Connection.class);
        pstmt = mock(PreparedStatement.class);
        resultSet = mock(ResultSet.class);
        stmt = mock(Statement.class);
    }

    @Test
    void testAddUsuario() throws Exception {
        Usuario usuario = new Usuario("teste", "1234", "url", 0, 0);

        try (MockedStatic<Db> dbMock = mockStatic(Db.class)) {
            dbMock.when(Db::getConnection).thenReturn(connection);

            when(connection.prepareStatement(anyString())).thenReturn(pstmt);
            when(pstmt.executeQuery()).thenReturn(resultSet);
            when(resultSet.next()).thenReturn(true);
            when(resultSet.getInt("id")).thenReturn(42);

            Usuario resultado = usuarioDao.addUsuario(usuario);

            assertEquals(42, resultado.getId());
            verify(pstmt).setString(1, "teste");
            verify(pstmt).setString(2, "1234");
            verify(pstmt).setString(3, "url");
        }
    }

    @Test
    void testAddUsuarioSQLException() {
        Usuario usuario = new Usuario("teste", "123", "url", 0, 0);

        try (MockedStatic<Db> dbMock = mockStatic(Db.class)) {
            dbMock.when(Db::getConnection).thenThrow(new SQLException("Erro de conexão"));

            SQLException ex = assertThrows(SQLException.class, () -> {
                usuarioDao.addUsuario(usuario);
            });

            assertTrue(ex.getMessage().contains("Erro de conexão"));
        }
    }


    @Test
    void testGetUsuarioById() throws Exception {
        try (MockedStatic<Db> dbMock = mockStatic(Db.class)) {
            dbMock.when(Db::getConnection).thenReturn(connection);

            when(connection.prepareStatement(anyString())).thenReturn(pstmt);
            when(pstmt.executeQuery()).thenReturn(resultSet);
            when(resultSet.next()).thenReturn(true);
            when(resultSet.getInt("id")).thenReturn(1);
            when(resultSet.getString("login")).thenReturn("user");
            when(resultSet.getString("senha")).thenReturn("pass");
            when(resultSet.getString("avatarURL")).thenReturn("url");
            when(resultSet.getInt("pontuacao")).thenReturn(10);
            when(resultSet.getInt("qtdSimulacoes")).thenReturn(2);

            Usuario user = usuarioDao.getUsuarioById(1);

            assertNotNull(user);
            assertEquals("user", user.getLogin());
            assertEquals(10, user.getPontuacao());
        }
    }

    @Test
    void testGetUsuarioByIdSQLException() {
        try (MockedStatic<Db> dbMock = mockStatic(Db.class)) {
            dbMock.when(Db::getConnection).thenThrow(new SQLException("Erro no banco"));

            SQLException ex = assertThrows(SQLException.class, () -> {
                usuarioDao.getUsuarioById(99);
            });

            assertTrue(ex.getMessage().contains("Erro no banco"));
        }
    }


    @Test
    void testGetUsuarioByLogin() throws Exception {
        try (MockedStatic<Db> dbMock = mockStatic(Db.class)) {

            dbMock.when(Db::getConnection).thenReturn(connection);

            when(connection.prepareStatement(anyString())).thenReturn(pstmt);
            when(pstmt.executeQuery()).thenReturn(resultSet);

            when(resultSet.next()).thenReturn(true);
            when(resultSet.getInt("id")).thenReturn(10);
            when(resultSet.getString("login")).thenReturn("usuarioTeste");
            when(resultSet.getString("senha")).thenReturn("senha123");
            when(resultSet.getString("avatarURL")).thenReturn("avatar.png");
            when(resultSet.getInt("pontuacao")).thenReturn(50);
            when(resultSet.getInt("qtdSimulacoes")).thenReturn(3);


            Usuario usuario = usuarioDao.getUsuarioByLogin("usuarioTeste");


            assertNotNull(usuario);
            assertEquals(10, usuario.getId());
            assertEquals("usuarioTeste", usuario.getLogin());
            assertEquals("senha123", usuario.getSenha());
            assertEquals("avatar.png", usuario.getAvatarURL());
            assertEquals(50, usuario.getPontuacao());
            assertEquals(3, usuario.getQtdSimulacoes());


            verify(pstmt).setString(1, "usuarioTeste");
        }
    }

    @Test
    void testGetUsuarioByLoginSQLException() {
        try (MockedStatic<Db> dbMock = mockStatic(Db.class)) {
            dbMock.when(Db::getConnection).thenThrow(new SQLException("Falha na query"));

            SQLException ex = assertThrows(SQLException.class, () -> {
                usuarioDao.getUsuarioByLogin("admin");
            });

            assertTrue(ex.getMessage().contains("Falha na query"));
        }
    }


    @Test
    void testUpdateUsuario() throws Exception {
        Usuario usuario = new Usuario(1, "user", "pass", "url", 10, 3);

        try (MockedStatic<Db> dbMock = mockStatic(Db.class)) {
            dbMock.when(Db::getConnection).thenReturn(connection);

            when(connection.prepareStatement(anyString())).thenReturn(pstmt);
            when(pstmt.executeUpdate()).thenReturn(1);

            boolean result = usuarioDao.updateUsuario(usuario);

            assertTrue(result);
            verify(pstmt).setString(1, "user");
            verify(pstmt).setInt(4, 10);
        }
    }

    @Test
    void testUpdateUsuarioSQLException() {
        Usuario usuario = new Usuario(1, "login", "senha", "avatar", 10, 2);

        try (MockedStatic<Db> dbMock = mockStatic(Db.class)) {
            dbMock.when(Db::getConnection).thenThrow(new SQLException("Erro ao atualizar"));

            SQLException ex = assertThrows(SQLException.class, () -> {
                usuarioDao.updateUsuario(usuario);
            });

            assertTrue(ex.getMessage().contains("Erro ao atualizar"));
        }
    }


    @Test
    void testDeleteUsuario() throws Exception {
        try (MockedStatic<Db> dbMock = mockStatic(Db.class)) {
            dbMock.when(Db::getConnection).thenReturn(connection);

            when(connection.prepareStatement(anyString())).thenReturn(pstmt);
            when(pstmt.executeUpdate()).thenReturn(1);

            boolean result = usuarioDao.deleteUsuario(1);

            assertTrue(result);
            verify(pstmt).setInt(1, 1);
        }
    }

    @Test
    void testDeleteUsuarioSQLException() {
        try (MockedStatic<Db> dbMock = mockStatic(Db.class)) {
            dbMock.when(Db::getConnection).thenThrow(new SQLException("Erro ao deletar"));

            SQLException ex = assertThrows(SQLException.class, () -> {
                usuarioDao.deleteUsuario(123);
            });

            assertTrue(ex.getMessage().contains("Erro ao deletar"));
        }
    }


    @Test
    void testGetAllUsuarios() throws Exception {
        try (MockedStatic<Db> dbMock = mockStatic(Db.class)) {
            dbMock.when(Db::getConnection).thenReturn(connection);

            when(connection.createStatement()).thenReturn(stmt);
            when(stmt.executeQuery(anyString())).thenReturn(resultSet);

            when(resultSet.next()).thenReturn(true, false); // 1 usuário
            when(resultSet.getInt("id")).thenReturn(1);
            when(resultSet.getString("login")).thenReturn("user");
            when(resultSet.getString("senha")).thenReturn("pass");
            when(resultSet.getString("avatarURL")).thenReturn("url");
            when(resultSet.getInt("pontuacao")).thenReturn(10);
            when(resultSet.getInt("qtdSimulacoes")).thenReturn(3);

            List<Usuario> usuarios = usuarioDao.getAllUsuarios();

            assertEquals(1, usuarios.size());
            assertEquals("user", usuarios.get(0).getLogin());
        }
    }

    @Test
    void testGetAllUsuariosSQLException() {
        try (MockedStatic<Db> dbMock = mockStatic(Db.class)) {
            dbMock.when(Db::getConnection).thenThrow(new SQLException("Erro geral"));

            SQLException ex = assertThrows(SQLException.class, () -> {
                usuarioDao.getAllUsuarios();
            });

            assertTrue(ex.getMessage().contains("Erro geral"));
        }
    }

}