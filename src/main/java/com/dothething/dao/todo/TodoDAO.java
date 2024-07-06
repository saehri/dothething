package com.dothething.dao.todo;
import com.dothething.database.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TodoDAO {
  private Connection connect() {
    Database.createNewDatabase();
    return Database.connect();
  }

  public List<Todo> getAllTodos() {
    List<Todo> todos = new ArrayList<>();
    String sql = "SELECT * FROM todos";

    try (Connection conn = connect();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(sql)) {

      while (rs.next()) {
        Todo todo = new Todo(
            rs.getInt("id"),
            rs.getString("name"),
            rs.getString("description"),
            rs.getString("due_date"),
            rs.getString("created_at"),
            rs.getString("updated_at"),
            rs.getInt("parent_id")
        );
        todos.add(todo);
      }
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }

    return todos;
  }

  public void addTodo(Todo todo) {
    String sql = "INSERT INTO todos (name, description, due_date, created_at, updated_at, parent_id) VALUES (?, ?, ?, ?, ?, ?)";

    try (Connection conn = connect();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {

      pstmt.setString(1, todo.getName());
      pstmt.setString(2, todo.getDescription());
      pstmt.setString(3, todo.getDueDate());
      pstmt.setString(4, todo.getCreatedAt());
      pstmt.setString(5, todo.getUpdatedAt());
      pstmt.setInt(6, todo.getParentId());

      pstmt.executeUpdate();
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
  }

  public void updateTodo(Todo todo) {
    String sql = "UPDATE todos SET name = ?, description = ?, due_date = ?, updated_at = ? WHERE id = ?";

    try (Connection conn = connect();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {

      pstmt.setString(1, todo.getName());
      pstmt.setString(2, todo.getDescription());
      pstmt.setString(3, todo.getDueDate());
      pstmt.setString(4, todo.getUpdatedAt());
      pstmt.setInt(5, todo.getId());

      pstmt.executeUpdate();
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
  }

  public void deleteTodo(int id) {
    String sql = "DELETE FROM todos WHERE id = ?";

    try (Connection conn = connect();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {

      pstmt.setInt(1, id);
      pstmt.executeUpdate();
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
  }
}