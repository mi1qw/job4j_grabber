package ru.job4j.grabber;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Connection, which rollback all commits.
 * It is used for integration test.
 */
final class ConnectionRollback {
    private static final Logger LOG = LoggerFactory.getLogger(ConnectionRollback.class);

    protected ConnectionRollback() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Create connection with autocommit=false mode and rollback call, when conneciton is closed.
     *
     * @param connection connection.
     * @return Connection object.
     */
    public static Connection create(final Connection connection) throws SQLException {
        connection.setAutoCommit(false);
        return (Connection) Proxy.newProxyInstance(
                ConnectionRollback.class.getClassLoader(),
                new Class[]{Connection.class},
                (proxy, method, args) -> {
                    Object rsl = null;
                    LOG.info("Connection ......{}", method.getName());
                    if ("close".equals(method.getName())) {
                        //connection.rollback();
                        rsl = method.invoke(connection, args);
                    } else if ("commit".equals(method.getName())) {
                        LOG.info("***    NOT commit !!! ***");
                    } else if ("prepareStatement".equals(method.getName())) {
                        rsl = method.invoke(connection, args);
                    } else {
                        rsl = method.invoke(connection, args);
                    }
                    return rsl;
                }
        );
    }
}