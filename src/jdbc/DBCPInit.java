package jdbc;

import java.sql.DriverManager;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.commons.dbcp2.ConnectionFactory;
import org.apache.commons.dbcp2.DriverManagerConnectionFactory;
import org.apache.commons.dbcp2.PoolableConnection;
import org.apache.commons.dbcp2.PoolableConnectionFactory;
import org.apache.commons.dbcp2.PoolingDriver;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

public class DBCPInit extends HttpServlet {

   @Override
   public void init() throws ServletException {
      loadJDBCDriver();
      initConnection();

   }

   private void loadJDBCDriver() {
      try {
         Class.forName("com.mysql.jdbc.Driver");

      } catch (ClassNotFoundException e) {
         throw new RuntimeException("fail to load JDBC Driver", e);
      }
   }

   private void initConnection() {
      try {
         String jdbcUrl = "jdbc:mysql://localhost:3306/sj?" + "useUnicode=true&characterEncoding=utf8";
         String username = "root";
         String pw = "";
         ConnectionFactory connFactory = new DriverManagerConnectionFactory(jdbcUrl, username, pw);
         PoolableConnectionFactory poolableConnFactroy = new PoolableConnectionFactory(connFactory, null);
         poolableConnFactroy.setValidationQuery("select 1");
         GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
         poolConfig.setTimeBetweenEvictionRunsMillis(10001 * 601 * 5L);
         poolConfig.setTestWhileIdle(true);
         poolConfig.setMinIdle(4);
         poolConfig.setMaxTotal(50);

         GenericObjectPool<PoolableConnection> connentionPool = new GenericObjectPool<PoolableConnection>(
               poolableConnFactroy, poolConfig);
         poolableConnFactroy.setPool(connentionPool);

         Class.forName("org.apache.commons.dbcp2.PoolingDriver");
         PoolingDriver driver = (PoolingDriver) DriverManager.getDriver("jdbc:apache:commons:dbcp:");
         driver.registerPool("sj", connentionPool);

      } catch (Exception e) {
         throw new RuntimeException();
      }

   }

}