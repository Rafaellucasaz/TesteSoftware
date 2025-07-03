package main.dao.impl;


import main.dao.UsuarioDao;
import main.entity.Usuario;
import main.util.Db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
public class UsuarioDaoImpl implements UsuarioDao{
    @Override
    public Usuario addUsuario(Usuario usuario) throws SQLException {
        String sql = "INSERT INTO usuarios (login, senha, avatarURL, pontuacao, qtdSimulacoes) VALUES (?, ?, ?, ?, ?) RETURNING id;";


        try (Connection conn = Db.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, usuario.getLogin());
            pstmt.setString(2, usuario.getSenha());
            pstmt.setString(3, usuario.getAvatarURL());
            pstmt.setInt(4, usuario.getPontuacao());
            pstmt.setInt(5, usuario.getQtdSimulacoes());


            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    usuario.setId(rs.getInt("id"));
                }
            }
            System.out.println("Usuário adicionado: " + usuario.getLogin());
            return usuario;

        } catch (SQLException e) {
            System.err.println("Erro ao adicionar usuário: " + e.getMessage());
            throw e;
        }
    }
    @Override
    public Usuario getUsuarioById(int id) throws SQLException {
        String sql = "SELECT id, login, senha, avatarURL, pontuacao, qtdSimulacoes FROM usuarios WHERE id = ?;";
        Usuario usuario = null;

        try (Connection conn = Db.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id); // Define o ID como parâmetro

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    usuario = new Usuario(
                            rs.getInt("id"),
                            rs.getString("login"),
                            rs.getString("senha"),
                            rs.getString("avatarURL"),
                            rs.getInt("pontuacao"),
                            rs.getInt("qtdSimulacoes")
                    );
                }
            }
            return usuario;

        } catch (SQLException e) {
            System.err.println("Erro ao buscar usuário por ID: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public Usuario getUsuarioByLogin(String login) throws SQLException {
        String sql = "SELECT id, login, senha, avatarURL, pontuacao, qtdSimulacoes FROM usuarios WHERE login = ?;";
        Usuario usuario = null;

        try (Connection conn = Db.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, login);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    usuario = new Usuario(
                            rs.getInt("id"),
                            rs.getString("login"),
                            rs.getString("senha"),
                            rs.getString("avatarURL"),
                            rs.getInt("pontuacao"),
                            rs.getInt("qtdSimulacoes")
                    );
                }
            }
            return usuario;

        } catch (SQLException e) {
            System.err.println("Erro ao buscar usuário por login: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public boolean updateUsuario(Usuario usuario) throws SQLException {
        String sql = "UPDATE usuarios SET login = ?, senha = ?, avatarURL = ?, pontuacao = ?, qtdSimulacoes = ? WHERE id = ?;";

        try (Connection conn = Db.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {


            pstmt.setString(1, usuario.getLogin());
            pstmt.setString(2, usuario.getSenha());
            pstmt.setString(3, usuario.getAvatarURL());
            pstmt.setInt(4, usuario.getPontuacao());
            pstmt.setInt(5, usuario.getQtdSimulacoes());
            pstmt.setInt(6, usuario.getId());

            int rowsAffected = pstmt.executeUpdate();
            System.out.println("Usuário atualizado: " + usuario.getLogin());
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Erro ao atualizar usuário: " + e.getMessage());
            throw e;
        }
    }
    @Override
    public boolean deleteUsuario(int id) throws SQLException {
        String sql = "DELETE FROM usuarios WHERE id = ?;";

        try (Connection conn = Db.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            int rowsAffected = pstmt.executeUpdate();
            System.out.println("Usuário com ID " + id + " deletado.");
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Erro ao deletar usuário: " + e.getMessage());
            throw e;
        }
    }


    @Override
    public List<Usuario> getAllUsuarios() throws SQLException {
        String sql = "SELECT id, login, senha, avatarURL, pontuacao, qtdSimulacoes FROM usuarios;";
        List<Usuario> usuarios = new ArrayList<>();

        try (Connection conn = Db.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {

                Usuario usuario = new Usuario(
                        rs.getInt("id"),
                        rs.getString("login"),
                        rs.getString("senha"),
                        rs.getString("avatarURL"),
                        rs.getInt("pontuacao"),
                        rs.getInt("qtdSimulacoes")
                );
                usuarios.add(usuario);
            }
            return usuarios;

        } catch (SQLException e) {
            System.err.println("Erro ao buscar todos os usuários: " + e.getMessage());
            throw e;
        }
    }
}
