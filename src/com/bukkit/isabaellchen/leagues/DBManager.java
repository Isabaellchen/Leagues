package com.bukkit.isabaellchen.leagues;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBManager {

    public DBManager(String url, String user, String password, String driver, int poolsize) {
        /*
        String driver = "com.mysql.jdbc.Driver";

        String url = "jdbc:mysql://db_host?user=db_user&password=db_pass/db_name;
         */
        try {
            new JDCConnectionDriver(driver, url, poolsize, 60000, user, password);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:"+Config.POOLNAME+":jdcpool");
    }

    public ArrayList<Map<String, String>> executeQuery(String query) {
        if (Config.VERBOSE) {
            System.out.println(query);
        }

        ResultSet resultSet = null;
        Statement statement = null;
        Connection connection = null;

        ArrayList<Map<String, String>> result = new ArrayList<Map<String, String>>();

        try {
            connection = getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(query);

            Map<String, String> entry;

            ResultSetMetaData meta = resultSet.getMetaData();

            while (resultSet.next()) {
                entry = new HashMap<String, String>();
                for (int j = 1; j <= meta.getColumnCount(); j++) {
                    entry.put(meta.getColumnLabel(j), resultSet.getString(j));
                }

                result.add(entry);
            }


        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            try {
                connection.close();
                statement.close();
                resultSet.close();
            } catch (SQLException ex) {
                Logger.getLogger(DBManager.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

        return result;
    }

    public boolean execute(String query) {
        if (Config.VERBOSE) {
            System.out.println(query);
        }

        boolean result = true;
        Statement statement = null;
        Connection connection = null;

        try {
            connection = getConnection();
            statement = connection.createStatement();
            statement.execute(query);


        } catch (Exception e) {
            e.printStackTrace();
            result = false;
        } finally {
            try {
                connection.close();
                statement.close();
            } catch (SQLException ex) {
                Logger.getLogger(DBManager.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

        return result;
    }

    public boolean insert(String table, Map<String, String> data) {

        String keys = "", values = "";

        for (Entry<String, String> entry : data.entrySet()) {
            keys += " ," + entry.getKey();
            values += " ,'" + entry.getValue() + "'";

        }

        keys = keys.substring(2);
        values = values.substring(2);

        String query = String.format("INSERT INTO %s (%s) VALUES (%s)", table, keys, values);
        return execute(query);
    }

    public boolean delete(int entryId, String table) {

        String query = String.format("DELETE FROM %s WHERE id='%d'", table, entryId);

        return execute(query);
    }

    public ArrayList<Map<String, String>> selectSingle(String table, String columnName,
            String columnValue) {

        HashMap<String, String> filter = new HashMap<String, String>();
        filter.put(columnName, columnValue);

        return select(table, filter);
    }

    public ArrayList<Map<String, String>> select(String table,
            Map<String, String> filter) {

        return select(table, "*", filter);
    }

    public ArrayList<Map<String, String>> select(String table,
            String select) {

        return select(table, select, null);
    }

    public ArrayList<Map<String, String>> select(String table,
            String select, Map<String, String> filter) {

        String query = String.format("SELECT %s FROM %s", select, table);

        if (filter != null && !filter.isEmpty()) {
            query += " WHERE ";

            boolean firstEntry = true;

            for (Entry<String, String> entry : filter.entrySet()) {

                query += firstEntry ? "" : " AND ";
                query += entry.getKey() + " = '" + entry.getValue() + "'";

                firstEntry = false;
            }
        }

        return executeQuery(query);
    }

    public boolean update(int entryId, String table, Map<String, String> data) {

        String query = String.format("UPDATE %s SET ", table);

        for (Entry<String, String> entry : data.entrySet()) {
            query += entry.getKey() + " = '" + entry.getValue() + "', ";

        }

        query = query.substring(0, query.length() - 2);
        query += " WHERE id=" + entryId;

        return execute(query);
    }

    public ArrayList<Map<String, String>> join(String table1, String table2,
            String t1Column, String t2Column, String select) {

        return join(table1, table2, t1Column, t2Column, select, null);
    }

    public ArrayList<Map<String, String>> join(String table1, String table2,
            String t1Column, String t2Column, Map<String, String> filter) {

        return join(table1, table2, t1Column, t2Column, "*", filter);
    }

    public ArrayList<Map<String, String>> join(String table1, String table2,
            String t1Column, String t2Column, String select, Map<String, String> filter) {

        String query = String.format("SELECT %s FROM %s JOIN %s ON %s.%s = %s.%s",
                select, table1, table2, table1, t1Column, table2, t2Column);

        if (filter != null && !filter.isEmpty()) {
            query += " WHERE ";

            boolean firstEntry = true;

            for (Entry<String, String> entry : filter.entrySet()) {

                query += firstEntry ? "" : " AND ";
                query += entry.getKey() + " = '" + entry.getValue() + "'";

                firstEntry = false;
            }
        }

        return executeQuery(query);
    }
}

