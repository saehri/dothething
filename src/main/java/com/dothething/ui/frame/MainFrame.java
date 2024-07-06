package com.dothething.ui.frame;

import com.dothething.ui.panel.TodoPanel;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
  public MainFrame() {
    setTitle("DOTHETHING ✅");
    setSize(800, 600);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLocationRelativeTo(null);

    // Add your main application components here
    TodoPanel todoPanel = new TodoPanel();
    add(todoPanel, BorderLayout.CENTER);
  }
}
