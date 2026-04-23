package com.factorysimulation;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.factorysimulation.ui.MainFrame;

public class App {
  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
      try {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      } catch (Exception ignored) {
      }
      MainFrame frame = new MainFrame();
      frame.setVisible(true);
    });
  }
}
