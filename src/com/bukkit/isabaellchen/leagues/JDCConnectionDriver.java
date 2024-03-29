package com.bukkit.isabaellchen.leagues;

import java.sql.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JDCConnectionDriver implements Driver {

    public static final String URL_PREFIX = "jdbc:" + Config.POOLNAME + ":";
    private static final int MAJOR_VERSION = 1;
    private static final int MINOR_VERSION = 0;
    private JDCConnectionPool pool;

    public JDCConnectionDriver(String driver, String url, int poolsize,
            long timeout, String user, String password) {
        try {
            DriverManager.registerDriver(this);
            Class.forName(driver).newInstance();
            pool = new JDCConnectionPool(url, poolsize, timeout, user, password);
        } catch (InstantiationException ex) {
            Logger.getLogger(JDCConnectionDriver.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(JDCConnectionDriver.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(JDCConnectionDriver.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(JDCConnectionDriver.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Connection connect(String url, Properties props)
            throws SQLException {
        if (!acceptsURL(url)) {
            return null;
        }
        return pool.getConnection();
    }

    public boolean acceptsURL(String url) {
        return url.startsWith(URL_PREFIX);
    }

    public int getMajorVersion() {
        return MAJOR_VERSION;
    }

    public int getMinorVersion() {
        return MINOR_VERSION;
    }

    public DriverPropertyInfo[] getPropertyInfo(String str, Properties props) {
        return new DriverPropertyInfo[0];
    }

    public boolean jdbcCompliant() {
        return false;
    }
}
