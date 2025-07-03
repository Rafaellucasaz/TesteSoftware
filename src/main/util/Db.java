package main.util;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Db {
    private static final String URL = "jdbc:postgresql://localhost:5432/";
    private static final String USER = "postgres";
    private static final String PASSWORD = "root";
    private static final String dbName = "simulacaocriaturas";

    public static void createDatabase(){
        Connection connection = null;
        Statement statement = null;
        try {
            connection = DriverManager.getConnection(URL+ "postgres", USER, PASSWORD);
            System.out.println("Conectado ao PostgreSQL com sucesso!");

            if (!databaseExists(connection, dbName)) {
                statement = connection.createStatement();
                String createDatabaseSQL = "CREATE DATABASE " + dbName;
                statement.executeUpdate(createDatabaseSQL);
                System.out.println("Banco de dados '" + dbName + "' criado com sucesso!");
            } else {
                System.out.println("Banco de dados '" + dbName + "' já existe.");
            }

            if (statement != null) {
                statement.close();
            }
            if (connection != null) {
                connection.close();
            }
            System.out.println("Conexão inicial fechada.");
            String newDbURL = "jdbc:postgresql://localhost:5432/" + dbName;
            connection = DriverManager.getConnection(newDbURL, USER, PASSWORD);
            System.out.println("Conectado ao banco de dados '" + dbName + "' para criar tabelas.");


            statement = connection.createStatement();

            String createTableUsuariosSQL = "CREATE TABLE IF NOT EXISTS usuarios (" +
                    "id SERIAL PRIMARY KEY," +
                    "login VARCHAR(100) NOT NULL," +
                    "senha VARCHAR(100) NOT NULL," +
                    "avatarURL VARCHAR NOT NULL," +
                    "pontuacao INTEGER," +
                    "qtdSimulacoes INTEGER" +
                    ");";
            statement.executeUpdate(createTableUsuariosSQL);
            System.out.println("Tabela 'usuarios' criada (ou já existe) com sucesso!");
        } catch (SQLException e) {
            System.err.println("Erro ao conectar ou executar operação no banco de dados:");
            e.printStackTrace();
        } finally {

            try {
                if (statement != null) {
                    statement.close();
                }
                if (connection != null) {
                    connection.close();
                }
                System.out.println("Conexão e Statement fechados.");
            } catch (SQLException e) {
                System.err.println("Erro ao fechar recursos do banco de dados:");
                e.printStackTrace();
            }
        }

    }

    private static boolean databaseExists(Connection connection, String dbName) throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            String checkDbSQL = "SELECT 1 FROM pg_database WHERE datname = LOWER('" + dbName + "');";
            return stmt.executeQuery(checkDbSQL).next();
        }
    }

    public static Connection getConnection() throws SQLException {

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("Erro: Driver JDBC do PostgreSQL não encontrado. Verifique sua dependência Maven.");
            throw new SQLException("Driver JDBC não encontrado", e);
        }

        return DriverManager.getConnection(URL + dbName, USER, PASSWORD);
    }
}
