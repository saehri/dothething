package com.dothething.ui.panel;

import com.dothething.dao.todo.Todo;
import com.dothething.dao.todo.TodoDAO;
import com.dothething.ui.form.TodoForm;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TodoPanel extends JPanel {
  private final JTable todoTable;
  private final DefaultTableModel tableModel;
  private final TodoDAO todoDAO;

  public TodoPanel() {
    setLayout(new BorderLayout());

    todoDAO = new TodoDAO();
    tableModel = new DefaultTableModel(new Object[]{"ID", "Name", "Description", "Due Date", "Created At", "Updated At", "Parent ID"}, 0);
    todoTable = new JTable(tableModel) {
      @Override
      public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
        Component c = super.prepareRenderer(renderer, row, column);
        if (column == 3) {
          String dueDateStr = (String) getValueAt(row, column);
          LocalDate dueDate = LocalDate.parse(dueDateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
          if (dueDate.isBefore(LocalDate.now().plusDays(2))) {
            c.setBackground(Color.RED);
          } else {
            c.setBackground(Color.WHITE);
          }
        } else {
          c.setBackground(Color.WHITE);
        }
        return c;
      }
    };
    todoTable.setComponentPopupMenu(createContextMenu());

    loadTodos();

    add(new JScrollPane(todoTable), BorderLayout.CENTER);

    JPopupMenu popupMenu = new JPopupMenu();
    JMenuItem editMenuItem = new JMenuItem("Edit");
    JMenuItem deleteMenuItem = new JMenuItem("Delete");
    JMenuItem addSubtaskMenuItem = new JMenuItem("Add Subtask");
    popupMenu.add(editMenuItem);
    popupMenu.add(deleteMenuItem);
    popupMenu.add(addSubtaskMenuItem);

    todoTable.addMouseListener(new MouseAdapter() {
      public void mousePressed(MouseEvent e) {
        if (SwingUtilities.isRightMouseButton(e)) {
          int row = todoTable.rowAtPoint(e.getPoint());
          todoTable.setRowSelectionInterval(row, row);
          popupMenu.show(e.getComponent(), e.getX(), e.getY());
        }
      }
    });

    editMenuItem.addActionListener(e -> {
      int selectedRow = todoTable.getSelectedRow();
      if (selectedRow != -1) {
        int id = (int) tableModel.getValueAt(selectedRow, 0);
        String name = (String) tableModel.getValueAt(selectedRow, 1);
        String description = (String) tableModel.getValueAt(selectedRow, 2);
        String dueDate = (String) tableModel.getValueAt(selectedRow, 3);
        String createdAt = (String) tableModel.getValueAt(selectedRow, 4);
        String updatedAt = (String) tableModel.getValueAt(selectedRow, 5);
        int parentId = (int) tableModel.getValueAt(selectedRow, 6);

        Todo todo = new Todo(id, name, description, dueDate, createdAt, updatedAt, parentId);
        new TodoForm(todo, TodoPanel.this, todo.getParentId()).setVisible(true);
      }
    });

    deleteMenuItem.addActionListener(e -> {
      int selectedRow = todoTable.getSelectedRow();
      if (selectedRow != -1) {
        int id = (int) tableModel.getValueAt(selectedRow, 0);
        todoDAO.deleteTodo(id);
        loadTodos();
      }
    });

    addSubtaskMenuItem.addActionListener(e -> {
      int selectedRow = todoTable.getSelectedRow();
      if (selectedRow != -1) {
        int parentId = (int) tableModel.getValueAt(selectedRow, 0);
        new TodoForm(null, TodoPanel.this, parentId).setVisible(true);
      }
    });

    JButton addButton = new JButton("Add Todo");
    addButton.addActionListener(e -> new TodoForm(null, TodoPanel.this, 0).setVisible(true));

    add(addButton, BorderLayout.SOUTH);
  }

  public void loadTodos() {
    tableModel.setRowCount(0);
    List<Todo> todos = todoDAO.getAllTodos();
    for (Todo todo : todos) {
      addTodoToTable(todo);
    }
  }

  private void addTodoToTable(Todo todo) {
    String indent = todo.getParentId() == 0 ? "" : "    ";
    tableModel.addRow(new Object[]{todo.getId(), indent + todo.getName(), todo.getDescription(), todo.getDueDate(), todo.getCreatedAt(), todo.getUpdatedAt(), todo.getParentId()});
  }

  private JPopupMenu createContextMenu() {
    JPopupMenu contextMenu = new JPopupMenu();
    JMenuItem editItem = new JMenuItem("Edit");
    JMenuItem deleteItem = new JMenuItem("Delete");

    editItem.addActionListener(e -> {
      int selectedRow = todoTable.getSelectedRow();
      if (selectedRow != -1) {
        int id = (int) tableModel.getValueAt(selectedRow, 0);
        Todo todo = getTodoById(id);
        int parentId = (int) tableModel.getValueAt(selectedRow, 6);
        if (todo != null) {
          TodoForm todoForm = new TodoForm(todo, this, parentId);
          todoForm.setVisible(true);
        }
      }
    });

    deleteItem.addActionListener(e -> {
      int selectedRow = todoTable.getSelectedRow();
      if (selectedRow != -1) {
        int id = (int) tableModel.getValueAt(selectedRow, 0);
        todoDAO.deleteTodo(id);
        loadTodos();
      }
    });

    contextMenu.add(editItem);
    contextMenu.add(deleteItem);
    return contextMenu;
  }

  private Todo getTodoById(int id) {
    List<Todo> todos = todoDAO.getAllTodos();
    for (Todo todo : todos) {
      if (todo.getId() == id) {
        return todo;
      }
    }
    return null;
  }
}
