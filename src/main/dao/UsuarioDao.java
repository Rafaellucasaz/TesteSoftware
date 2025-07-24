package main.dao;

import main.model.Usuario;
import java.sql.SQLException;
import java.util.List;


public interface UsuarioDao {

    Usuario addUsuario(Usuario usuario) throws SQLException;
    Usuario getUsuarioById(int id) throws SQLException;
    Usuario getUsuarioByLogin(String login) throws SQLException;
    boolean updateUsuario(Usuario usuario) throws SQLException;
    boolean deleteUsuario(int id) throws SQLException;
    List<Usuario> getAllUsuarios() throws SQLException;
}
