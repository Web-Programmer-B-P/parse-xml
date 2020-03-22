package xml.persistence;

import org.apache.commons.dbcp2.BasicDataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class PoolDaoConnection {
    private static final PoolDaoConnection INSTANCE = new PoolDaoConnection();
    private final BasicDataSource source = new BasicDataSource();

    private PoolDaoConnection() {
        source.setDriverClassName("org.postgresql.Driver");
        source.setDriverClassName("org.postgresql.Driver");
        source.setUrl("jdbc:postgresql://localhost:5432/box");
        source.setUsername("postgres");
        source.setPassword("password");
        source.setMinIdle(5);
        source.setMaxIdle(10);
        source.setMaxOpenPreparedStatements(100);
    }

    public static PoolDaoConnection getInstance() {
        return INSTANCE;
    }

    public Connection getConnection() throws SQLException {
        return source.getConnection();
    }
}
