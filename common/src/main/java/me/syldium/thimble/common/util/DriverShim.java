package me.syldium.thimble.common.util;

import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * A {@link Driver} delegate.
 */
public final class DriverShim implements Driver {

    private final Driver driver;

    public DriverShim(@NotNull Driver driver) {
        this.driver = driver;
    }

    @Override
    public Connection connect(String s, Properties properties) throws SQLException {
        return this.driver.connect(s, properties);
    }

    @Override
    public boolean acceptsURL(String s) throws SQLException {
        return this.driver.acceptsURL(s);
    }

    @Override
    public DriverPropertyInfo[] getPropertyInfo(String s, Properties properties) throws SQLException {
        return this.driver.getPropertyInfo(s, properties);
    }

    @Override
    public int getMajorVersion() {
        return this.driver.getMajorVersion();
    }

    @Override
    public int getMinorVersion() {
        return this.driver.getMinorVersion();
    }

    @Override
    public boolean jdbcCompliant() {
        return this.driver.jdbcCompliant();
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return this.driver.getParentLogger();
    }
}
