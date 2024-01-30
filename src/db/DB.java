package db;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;

public class DB {
    // métodos para conectar e desconectar o banco - estáticos

    // criação do atributo de conexão, inicializado com null
    private static Connection conn = null;

    // método para conectar com o banco de dados
    public static Connection getConnection(){
        // testa se o atributo está nulo, se estiver realiza a conexão
        if (conn == null){
            try {
                // chama o método para carregar as propriedades
                Properties props = loadProperties();
                // armazena a url do arquivo na variável
                String url = props.getProperty("dburl");
                // utiliza o driver para conectar com o banco e atribui à variável conn
                conn = DriverManager.getConnection(url, props);
            } catch (SQLException e){
                throw new DbException(e.getMessage());
            }
        }
        return conn;
    }

    // método para fechar a conexão com o banco de dados
    public static void closeConnection(){
        if (conn != null){
            try {
                conn.close();
            } catch (SQLException e){
                throw new DbException(e.getMessage());
            }
        }
    }

    // fará a leitura do db.properties e guardar as informações no objeto Properties
    private static Properties loadProperties(){
        // faz a leitura do arquivo de propriedades
        try(FileInputStream fs = new FileInputStream("db.properties")){
            // instancia do objeto properties
            Properties props = new Properties();
            // guarda os dados dentro do objeto props, arquivo indicado na variável fs
            props.load(fs);
            return props;
        } catch (IOException e){
            throw new DbException(e.getMessage());
        }
    }

    // método auxiliar para fechar o statement e o resultset, se não fizer isso precisa adicionar exceções
    public static void closeStatement(Statement st){
        if (st != null){
            try {
                st.close();
            } catch (SQLException e) {
                throw new DbException(e.getMessage());
            }
        }
    }

    // método auxiliar para fechar o statement e o resultset, se não fizer isso precisa adicionar exceções
    public static void closeResultSet(ResultSet rs){
        if (rs != null){
            try {
                rs.close();
            } catch (SQLException e) {
                throw new DbException(e.getMessage());
            }
        }
    }
}
