package main.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Db {

    private static final String URL = "jdbc:h2:./simulacaocriaturas;DB_CLOSE_DELAY=-1";
    private static final String USER = "sa";
    private static final String PASSWORD = "";
    private static final String DB_NAME = "simulacaocriaturas";


    public static void createDatabase() {
        Connection connection = null;
        Statement statement = null;
        try {

            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Conectado ao H2 com sucesso! (Banco de dados: '" + DB_NAME + "')");

            statement = connection.createStatement();


            String createTableUsuariosSQL = "CREATE TABLE IF NOT EXISTS usuarios (" +
                    "id INT PRIMARY KEY AUTO_INCREMENT," +
                    "login VARCHAR(100) NOT NULL," +
                    "senha VARCHAR(100) NOT NULL," +
                    "avatarURL VARCHAR NOT NULL," +
                    "pontuacao INT," +
                    "qtdSimulacoes INT" +
                    ");";
            statement.executeUpdate(createTableUsuariosSQL);
            System.out.println("Tabela 'usuarios' criada (ou já existe) com sucesso!");

        } catch (SQLException e) {
            System.err.println("Erro ao conectar ou executar operação no banco de dados H2:");
            e.printStackTrace();
        } finally {

            try {
                if (statement != null) {
                    statement.close();
                }
                if (connection != null) {
                    connection.close();
                    System.out.println("Conexão H2 e Statement fechados.");
                }
            } catch (SQLException e) {
                System.err.println("Erro ao fechar recursos do banco de dados H2:");
                e.printStackTrace();
            }
        }
    }


    public static Connection getConnection() throws SQLException {
        try {

            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("Erro: Driver JDBC do H2 não encontrado. Verifique sua dependência Maven.");
            throw new SQLException("Driver JDBC não encontrado", e);
        }

        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}