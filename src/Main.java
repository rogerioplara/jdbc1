import db.DB;
import db.DbException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Main {
    public static void main(String[] args) {

        // conexão com o banco, é o que será utilizado em todo o programa
        // Connection conn = DB.getConnection();
        // DB.closeConnection();

        // variável de conexão
        Connection conn = null;
        // variável do statement
        Statement st = null;
        // variavel do resultado
        ResultSet rs = null;

        try{
            // conecta ao banco de dados
            conn = DB.getConnection();

            // instância do objeto do tipo statement
            st = conn.createStatement();
            // espera uma string que é o comando sql, atribui à variável do resultado
            rs = st.executeQuery("select * from department");
            // percorrer a variável rs até que seja falsa
            while (rs.next()){
                // imprime o inteiro que está na coluna ID + a string que está na coluna name
                System.out.println(rs.getInt("Id") + ", " + rs.getString("Name"));
            }

        } catch (SQLException e){
            e.printStackTrace();
        } finally {
            DB.closeResultSet(rs);
            DB.closeStatement(st);
            DB.closeConnection();
        }

    }
}