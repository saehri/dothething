package com.dothething.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {
  private static final String URL = "jdbc:sqlite:database.db";

  public static Connection connect() {
    Connection conn = null;
    try {
      conn = DriverManager.getConnection(URL);
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
    return conn;
  }

  public static void createNewDatabase() {
    try (Connection conn = connect()) {
      if (conn != null) {
        Statement stmt = conn.createStatement();
        String sql = "CREATE TABLE IF NOT EXISTS todos ("
            + "	id integer PRIMARY KEY AUTOINCREMENT,"
            + "	name text NOT NULL,"
            + "	description text,"
            + "	due_date text,"
            + " completed BOOLEAN default 0,"
            + "	created_at text NOT NULL,"
            + "	updated_at text NOT NULL,"
            + " parent_id integer,"
            + " CONSTRAINT fk_todos FOREIGN KEY (parent_id) REFERENCES todos(id) ON DELETE CASCADE"
            + ");";

        stmt.execute(sql);
      }
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
  }

  public static void main(String[] args) {
    createNewDatabase();
  }
}
