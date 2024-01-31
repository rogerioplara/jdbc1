import db.DB;
import db.DbException;
import db.DbIntegrityException;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class Main {
    public static void main(String[] args) {

        // conexão com o banco, é o que será utilizado em todo o programa
        // Connection conn = DB.getConnection();
        // DB.closeConnection();

        /*
        Transações (ACID):
        Atomicidade: ou acontece tudo ou não acontece nada
        Consinstência
        Isolada
        Durável
         */

        transacoes();

    }

    public static void recuperarDados(){
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

    public static void inserirDados(){
        // objeto de conexão
        Connection conn = null;
        // prepared statement
        PreparedStatement st = null;

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        try {
            conn = DB.getConnection();

            /*
            // prepareStatement espera uma string de sql de inserção de dados
            st = conn.prepareStatement(
                "INSERT INTO seller "
                    + "(Name, Email, BirthDate, BaseSalary, DepartmentId)"
                    + "VALUES(?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS); // retorna o valor no final

            // troca dos values?(placeholders)
            st.setString(1, "Carl Purple");
            st.setString(2, "carl@gmail.com");
            st.setDate(3, new java.sql.Date(sdf.parse("22/04/1985").getTime()));
            st.setDouble(4, 3000.00);
            st.setInt(5, 4);
            */

            // inserção de 2 novos departamentos com o mesmo comando
            st = conn.prepareStatement("insert into department (Name) values ('D1'),('D2')",
                Statement.RETURN_GENERATED_KEYS);

            // operação de alteração, retorna quantas linhas foram alteradas no db
            int rowsAffected = st.executeUpdate();

            if (rowsAffected > 0){
                // retorna um objeto do tipo ResultSet
                ResultSet rs = st.getGeneratedKeys();

                // percorre o resultSet e imprime o id de cada linha afetada
                while (rs.next()){
                    int id = rs.getInt(1);
                    System.out.println("Done! ID = " + id);
                }

            } else {
                System.out.println("No rows affected");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
//        catch (ParseException e) {
//            e.printStackTrace();
//      }
        finally {
            DB.closeStatement(st);
            DB.closeConnection();
        }
    }

    public static void atualizarDados(){
        Connection conn = null;

        PreparedStatement st = null;

        try {
            conn = DB.getConnection();

            st = conn.prepareStatement(
                "UPDATE seller "
                + "SET BaseSalary = BaseSalary + ? "
                + "WHERE "
                + "(DepartmentId = ?)"

            );

            st.setDouble(1, 200.00);
            st.setInt(2, 2);

            int rowsAffected = st.executeUpdate();

            System.out.println("Done! Rows affected: " + rowsAffected);

        } catch (SQLException e){
            e.printStackTrace();
        } finally {
//            DB.closeResultSet(rs);
            DB.closeStatement(st);
            DB.closeConnection();
        }
    }

    public static void deletarDados(){
        Connection conn = null;

        PreparedStatement st = null;

        try {
            conn = DB.getConnection();

            st = conn.prepareStatement(
                "DELETE FROM department "
                + "WHERE "
                + "Id = ?"
            );

            st.setInt(1, 2);

            int rowsAffected = st.executeUpdate();

            System.out.println("Done! Rows affected: " + rowsAffected);

        } catch (SQLException e){
            throw new DbIntegrityException(e.getMessage());
        } finally {
//            DB.closeResultSet(rs);
            DB.closeStatement(st);
            DB.closeConnection();
        }


    }

    public static void transacoes(){
        Connection conn = null;
        Statement st = null;

        try{
            conn = DB.getConnection();

            // transação - não confirma a operação automaticamente
            conn.setAutoCommit(false);

            st = conn.createStatement();

            int rows1 = st.executeUpdate(
                "UPDATE seller SET BaseSalary = 2090.0 WHERE DepartmentId = 1");

            /*
            lançando um erro entre as transações para teste
            int x = 1;
            if (x < 2){
                throw new SQLException("Fake error");
            }
            */


            int rows2 = st.executeUpdate(
                "UPDATE seller SET BaseSalary = 3090.0 WHERE DepartmentId = 2");

            // confirma a transação
            conn.commit();

            System.out.println("rows1: " + rows1);
            System.out.println("rows2: " + rows2);

        } catch (SQLException e){
            // faz um rollback caso ocorra erro
            try {
                conn.rollback();
                throw new DbException("Transaction rolled back! Caused by: " + e.getMessage());
            } catch (SQLException ex) {
                throw new DbException("Error trying to rollback! Caused by: " + e.getMessage());
            }
        } finally {
            DB.closeStatement(st);
            DB.closeConnection();
        }
    }
}