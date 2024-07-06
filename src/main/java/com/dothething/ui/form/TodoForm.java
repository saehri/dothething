package com.dothething.ui.form;

import com.dothething.dao.todo.Todo;
import com.dothething.dao.todo.TodoDAO;
import com.dothething.ui.panel.TodoPanel;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.SqlDateModel;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

public class TodoForm extends JFrame {
  private final JTextField nameField;
  private final JTextArea descriptionArea;
  private final JDatePickerImpl datePicker;
  private final TodoDAO todoDAO;

  public TodoForm(Todo todo, TodoPanel todoPanel, int parentId) {
    todoDAO = new TodoDAO();

    setTitle(todo == null ? "Add Todo" : "Edit Todo");
    setSize(400, 300);
    setLayout(new GridLayout(5, 2));

    JLabel nameLabel = new JLabel("Name:");
    nameField = new JTextField();

    JLabel descriptionLabel = new JLabel("Description:");
    descriptionArea = new JTextArea();

    JLabel dueDateLabel = new JLabel("Due Date:");

    // Date picker setup
    SqlDateModel model = new SqlDateModel();
    Properties properties = new Properties();
    properties.put("text.today", "Today");
    properties.put("text.month", "Month");
    properties.put("text.year", "Year");
    JDatePanelImpl datePanel = new JDatePanelImpl(model, properties);
    datePicker = new JDatePickerImpl(datePanel, new DateLabelFormatter());

    JButton saveButton = new JButton("Save");

    add(nameLabel);
    add(nameField);
    add(descriptionLabel);
    add(new JScrollPane(descriptionArea));
    add(dueDateLabel);
    add(datePicker);
    add(new JLabel());
    add(saveButton);

    if (todo != null) {
      nameField.setText(todo.getName());
      descriptionArea.setText(todo.getDescription());
      datePicker.getModel().setDate(
          Integer.parseInt(todo.getDueDate().substring(0, 4)),
          Integer.parseInt(todo.getDueDate().substring(5, 7)) - 1,
          Integer.parseInt(todo.getDueDate().substring(8, 10))
      );
      datePicker.getModel().setSelected(true);
    }

    saveButton.addActionListener(e -> {
      String name = nameField.getText();
      String description = descriptionArea.getText();
      LocalDate selectedDate = LocalDate.of(
          datePicker.getModel().getYear(),
          datePicker.getModel().getMonth() + 1,
          datePicker.getModel().getDay()
      );
      String dueDate = selectedDate.toString();
      String currentDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

      if (todo == null) {
        Todo newTodo = new Todo(0, name, description, dueDate, currentDateTime, currentDateTime, parentId);
        todoDAO.addTodo(newTodo);
      } else {
        todo.setName(name);
        todo.setDescription(description);
        todo.setDueDate(dueDate);
        todo.setUpdatedAt(currentDateTime);
        todo.setParentId(parentId);
        todoDAO.updateTodo(todo);
      }

      todoPanel.loadTodos();
      dispose();
    });
  }

  // Custom formatter for the date picker
  public static class DateLabelFormatter extends JFormattedTextField.AbstractFormatter {
    private final String datePattern = "yyyy-MM-dd";
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(datePattern);

    @Override
    public Object stringToValue(String text) {
      return LocalDate.parse(text, dateFormatter);
    }

    @Override
    public String valueToString(Object value) {
      if (value != null) {
        if (value instanceof java.util.GregorianCalendar calendar) {
          LocalDate localDate = calendar.toZonedDateTime().toLocalDate();
          return dateFormatter.format(localDate);
        } else if (value instanceof LocalDate ld) {
          return dateFormatter.format(ld);
        }
      }
      return "";
    }
  }

}
