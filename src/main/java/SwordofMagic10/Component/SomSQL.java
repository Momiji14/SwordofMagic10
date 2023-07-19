package SwordofMagic10.Component;

import com.github.jasync.sql.db.*;
import com.github.jasync.sql.db.mysql.pool.MySQLConnectionFactory;
import com.github.jasync.sql.db.pool.ConnectionPool;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static SwordofMagic10.SomCore.Log;

public class SomSQL {
    private static final String host = "192.168.0.18";
    private static final int port = 3306;
    private static final String database = "SwordofMagic10";
    private static final String user = "somnet";
    private static final String pass = "somnet";

    private static Connection connection;

    public static void connection() {
        long start = System.currentTimeMillis();
        Log("§a[SomSQL]§eConnecting...");
        MySQLConnectionFactory factory = new MySQLConnectionFactory(new Configuration(user, host, port, pass, database));
        ConnectionPoolConfiguration configuration = new ConnectionPoolConfiguration();
        connection = new ConnectionPool<>(factory, configuration);
        //connection = MySQLConnectionBuilder.createConnectionPool("jdbc:mysql://$" + host + ":$" + port + "/$" + database + "?user=$" + user + "&password=$" + pass);
        try {
            connection.connect().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        long milli = System.currentTimeMillis() - start;
        if (connection.isConnected()) {
            Log("§a[SomSQL]§bConnection - " + milli + "ms");
        } else {
            Log("§a[SomSQL]§cError - " + milli + "ms");
        }
    }

    public static void disconnect() {
        try {
            connection.disconnect().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    public static ResultSet query(String query) {
        CompletableFuture<QueryResult> future = connection.sendPreparedStatement(query);
        try {
            return future.get().getRows();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public static ResultSet update(String query) {
        try {
            return connection.sendQuery(query).get().getRows();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public static String[] normalization(String[] primaryKey, Object[] primaryValue) {
        StringBuilder primaryInsert = new StringBuilder(primaryKey[0]);
        StringBuilder keyInsert = new StringBuilder(primaryValue[0] instanceof String ? "'" + primaryValue[0] + "'" : primaryValue[0].toString());
        for (int i = 1; i < primaryKey.length; i++) {
            primaryInsert.append(", ").append(primaryKey[i]);
            keyInsert.append(", ").append(primaryValue[i] instanceof String ? "'" + primaryValue[i] + "'" : primaryValue[i].toString());
        }
        return new String[]{primaryInsert.toString(), keyInsert.toString()};
    }

    public static RowData getSql(String table, String primaryKey, String primaryValue, String colum) {
        return getSql(table, new String[]{primaryKey}, new String[]{primaryValue}, colum);
    }

    public static RowData getSql(String table, String[] primaryKey, String[] primaryValue, String colum) {
        String[] primary = normalization(primaryKey, primaryValue);
        return query("select " + colum + " from " + table + " where (" + primary[0] + ") = (" + primary[1] + ")").get(0);
    }

    public static ResultSet getSqlList(String table, String[] primaryKey, String[] primaryValue, String colum) {
        String[] primary = normalization(primaryKey, primaryValue);
        return query("select " + colum + " from " + table + " where (" + primary[0] + ") = (" + primary[1] + ")");
    }

    public static void setSql(String table, String primaryKey, String primaryValue, String colum, Object value) {
        setSql(table, new String[]{primaryKey}, new String[]{primaryValue}, colum, value);
    }

    public static void setSql(String table, String[] primaryKey, String[] primaryValue, String colum, Object value) {
        String[] primary = normalization(primaryKey, primaryValue);
        String data = null;
        if (value instanceof String) {
            data = "'" + value + "'";
        } else if (value != null) {
            data = value.toString();
        }
        update("insert into " + table + " (" + primary[0] + ", " + colum + ") values (" + primary[1] + ", " + data + ") on duplicate key update " + colum + " = " + data);
    }

    public static boolean existSql(String table, String primaryKey, String primaryValue) {
        return existSql(table, new String[]{primaryKey}, new String[]{primaryValue});
    }

    public static boolean existSql(String table, String[] primaryKey, String[] primaryValue) {
        String[] primary = normalization(primaryKey, primaryValue);
        ResultSet result = query("select * from " + table + " where (" + primary[0] + ") = (" + primary[1] + ") limit 1");
        return result.size() > 0;
    }

    public static String getString(String table, String primaryKey, String primaryValue, String colum) {
        return getString(table, new String[]{primaryKey}, new String[]{primaryValue}, colum);
    }
    public static String getString(String table, String[] primaryKey, String[] primaryValue, String colum) {
        return getSql(table, primaryKey, primaryValue, colum).getString(colum);
    }

    public static List<String> getStringList(String table, String primaryKey, String primaryValue, String colum) {
        return getStringList(table, new String[]{primaryKey}, new String[]{primaryValue}, colum);
    }

    public static List<String> getStringList(String table, String[] primaryKey, String[] primaryValue, String colum) {
        List<String> list = new ArrayList<>();
        for (RowData data : getSqlList(table, primaryKey, primaryValue, colum)) {
            list.add(data.getString(colum));
        }
        return list;
    }

    public static double getDouble(String table, String primaryKey, String primaryValue, String colum) {
        return getDouble(table, new String[]{primaryKey}, new String[]{primaryValue}, colum);
    }

    public static double getDouble(String table, String[] primaryKey, String[] primaryValue, String colum) {
        return getSql(table, primaryKey, primaryValue, colum).getDouble(colum);
    }

    public static int getInt(String table, String primaryKey, String primaryValue, String colum) {
        return getInt(table, new String[]{primaryKey}, new String[]{primaryValue}, colum);
    }

    public static int getInt(String table, String[] primaryKey, String[] primaryValue, String colum) {
        return getSql(table, primaryKey, primaryValue, colum).getInt(colum);
    }

    public static long getLong(String table, String primaryKey, String primaryValue, String colum) {
        return getLong(table, new String[]{primaryKey}, new String[]{primaryValue}, colum);
    }

    public static long getLong(String table, String[] primaryKey, String[] primaryValue, String colum) {
        return getSql(table, primaryKey, primaryValue, colum).getLong(colum);
    }
}
