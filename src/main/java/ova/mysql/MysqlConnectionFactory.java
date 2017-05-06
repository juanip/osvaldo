package ova.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import ova.ConnectionFactory;
import ova.exceptions.OvaException;

public class MysqlConnectionFactory implements ConnectionFactory {

    public Connection getConnection() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/osvaldo", "root", "64586458");
            return con;
        } catch (Exception ex) {
            throw new OvaException(ex);
        }
    }

}
