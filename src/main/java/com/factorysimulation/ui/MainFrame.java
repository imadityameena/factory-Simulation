package com.factorysimulation.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

import com.factorysimulation.model.Adjuster;
import com.factorysimulation.model.MachineCategory;
import com.factorysimulation.model.SimulationResult;
import com.factorysimulation.sim.SimulationEngine;

public class MainFrame extends JFrame {
  private final DefaultTableModel categoryTableModel = new DefaultTableModel(
      new Object[] { "Category", "Quantity", "MTTF", "Repair Time" }, 0) {
    @Override
    public boolean isCellEditable(int row, int column) {
      return false;
    }
  };

  private final DefaultTableModel adjusterTableModel = new DefaultTableModel(new Object[] { "ID", "Name", "Skills" },
      0) {
    @Override
    public boolean isCellEditable(int row, int column) {
      return false;
    }
  };

  private final DefaultTableModel categorySummaryModel = new DefaultTableModel(
      new Object[] { "Category", "Machines", "Failures", "Utilization" }, 0) {
    @Override
    public boolean isCellEditable(int row, int column) {
      return false;
    }
  };

  private final DefaultTableModel adjusterSummaryModel = new DefaultTableModel(
      new Object[] { "ID", "Name", "Skills", "Repairs", "Utilization" }, 0) {
    @Override
    public boolean isCellEditable(int row, int column) {
      return false;
    }
  };

  private final JTextField categoryNameField = new JTextField(18);
  private final JSpinner categoryQuantitySpinner = new JSpinner(new SpinnerNumberModel(20, 1, 100000, 1));
  private final JSpinner mttfSpinner = new JSpinner(new SpinnerNumberModel(120.0, 0.1, 1000000.0, 0.5));
  private final JSpinner repairTimeSpinner = new JSpinner(new SpinnerNumberModel(2.0, 0.1, 1000000.0, 0.5));
  private final JTextField adjusterIdField = new JTextField(12);
  private final JTextField adjusterNameField = new JTextField(12);
  private final JTextField adjusterSkillsField = new JTextField(24);
  private final JSpinner durationSpinner = new JSpinner(new SpinnerNumberModel(480.0, 1.0, 10000000.0, 1.0));
  private final JTextField seedField = new JTextField("42", 12);

  private final JTextArea overviewArea = new JTextArea();
  private final JLabel machineUtilizationLabel = new JLabel("0.00%");
  private final JLabel adjusterUtilizationLabel = new JLabel("0.00%");
  private final JLabel failuresLabel = new JLabel("0");
  private final JLabel waitingLabel = new JLabel("0.00 hours");
  private final JLabel queueLabel = new JLabel("0.00");

  public MainFrame() {
    setTitle("Factory Machine Simulation System");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setMinimumSize(new Dimension(1240, 780));
    setSize(1360, 840);
    setLocationRelativeTo(null);

    JPanel root = new JPanel(new BorderLayout(0, 0));
    root.setBorder(new EmptyBorder(12, 12, 12, 12));
    root.setBackground(new Color(243, 246, 250));
    root.add(buildHeader(), BorderLayout.NORTH);

    JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, buildSetupTabs(), buildResultsPanel());
    splitPane.setResizeWeight(0.43);
    splitPane.setBorder(null);
    root.add(splitPane, BorderLayout.CENTER);

    setContentPane(root);
    loadSampleData();
    clearResults();
  }

  private Component buildHeader() {
    JPanel panel = new JPanel(new BorderLayout());
    panel.setOpaque(false);
    JLabel title = new JLabel("Factory Machine Simulation System");
    title.setFont(new Font("SansSerif", Font.BOLD, 24));
    JLabel subtitle = new JLabel("Swing-based factory maintenance planning and utilization analysis");
    subtitle.setForeground(new Color(85, 92, 104));
    panel.add(title, BorderLayout.NORTH);
    panel.add(subtitle, BorderLayout.SOUTH);
    panel.setBorder(new EmptyBorder(0, 4, 10, 4));
    return panel;
  }

  private Component buildSetupTabs() {
    JTabbedPane tabs = new JTabbedPane();
    tabs.addTab("Machine Setup", buildMachineTab());
    tabs.addTab("Adjusters", buildAdjusterTab());
    tabs.addTab("Simulation", buildSimulationTab());
    return tabs;
  }

  private Component buildMachineTab() {
    JPanel panel = new JPanel(new BorderLayout(10, 10));
    panel.setBorder(new EmptyBorder(12, 12, 12, 12));
    panel.setBackground(Color.WHITE);

    JPanel form = new JPanel(new GridBagLayout());
    form.setBackground(Color.WHITE);
    form.setBorder(new TitledBorder(new LineBorder(new Color(210, 215, 223)), "Add Machine Category"));

    GridBagConstraints gbc = constraints();
    addField(form, gbc, 0, "Category Name", categoryNameField);
    addField(form, gbc, 1, "Quantity", categoryQuantitySpinner);
    addField(form, gbc, 2, "MTTF (hours)", mttfSpinner);
    addField(form, gbc, 3, "Repair Time (hours)", repairTimeSpinner);

    JButton addButton = primaryButton("Add Category");
    JButton removeButton = secondaryButton("Remove Selected");
    JButton clearButton = secondaryButton("Clear All");
    JButton sampleButton = secondaryButton("Load Sample Data");
    JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
    buttonRow.setOpaque(false);
    buttonRow.add(addButton);
    buttonRow.add(removeButton);
    buttonRow.add(clearButton);
    buttonRow.add(sampleButton);

    gbc.gridx = 0;
    gbc.gridy = 4;
    gbc.gridwidth = 2;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 1.0;
    form.add(buttonRow, gbc);

    JTable table = new JTable(categoryTableModel);
    table.setRowHeight(24);
    table.setFillsViewportHeight(true);

    panel.add(form, BorderLayout.NORTH);
    panel.add(wrapTable(table, "Defined Categories"), BorderLayout.CENTER);

    addButton.addActionListener(e -> addCategory());
    removeButton.addActionListener(e -> removeSelectedRows(table, categoryTableModel));
    clearButton.addActionListener(e -> clearAll());
    sampleButton.addActionListener(e -> loadSampleData());
    return panel;
  }

  private Component buildAdjusterTab() {
    JPanel panel = new JPanel(new BorderLayout(10, 10));
    panel.setBorder(new EmptyBorder(12, 12, 12, 12));
    panel.setBackground(Color.WHITE);

    JPanel form = new JPanel(new GridBagLayout());
    form.setBackground(Color.WHITE);
    form.setBorder(new TitledBorder(new LineBorder(new Color(210, 215, 223)), "Add Adjuster"));

    GridBagConstraints gbc = constraints();
    addField(form, gbc, 0, "Adjuster ID", adjusterIdField);
    addField(form, gbc, 1, "Adjuster Name", adjusterNameField);
    addField(form, gbc, 2, "Skills (comma separated)", adjusterSkillsField);

    JButton addButton = primaryButton("Add Adjuster");
    JButton removeButton = secondaryButton("Remove Selected");
    JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
    buttonRow.setOpaque(false);
    buttonRow.add(addButton);
    buttonRow.add(removeButton);

    gbc.gridx = 0;
    gbc.gridy = 3;
    gbc.gridwidth = 2;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 1.0;
    form.add(buttonRow, gbc);

    JTable table = new JTable(adjusterTableModel);
    table.setRowHeight(24);
    table.setFillsViewportHeight(true);

    panel.add(form, BorderLayout.NORTH);
    panel.add(wrapTable(table, "Defined Adjusters"), BorderLayout.CENTER);

    addButton.addActionListener(e -> addAdjuster());
    removeButton.addActionListener(e -> removeSelectedRows(table, adjusterTableModel));
    return panel;
  }

  private Component buildSimulationTab() {
    JPanel panel = new JPanel(new BorderLayout(10, 10));
    panel.setBorder(new EmptyBorder(12, 12, 12, 12));
    panel.setBackground(Color.WHITE);

    JPanel form = new JPanel(new GridBagLayout());
    form.setBackground(Color.WHITE);
    form.setBorder(new TitledBorder(new LineBorder(new Color(210, 215, 223)), "Simulation Settings"));

    GridBagConstraints gbc = constraints();
    addField(form, gbc, 0, "Simulation Duration (hours)", durationSpinner);
    addField(form, gbc, 1, "Random Seed", seedField);

    JButton runButton = primaryButton("Run Simulation");
    JButton clearResultsButton = secondaryButton("Clear Results");
    JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
    buttonRow.setOpaque(false);
    buttonRow.add(runButton);
    buttonRow.add(clearResultsButton);

    gbc.gridx = 0;
    gbc.gridy = 2;
    gbc.gridwidth = 2;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 1.0;
    form.add(buttonRow, gbc);

    JTextArea instructions = new JTextArea(
        "How it works:\n" +
            "1. Add machine categories.\n" +
            "2. Add adjusters and their skills.\n" +
            "3. Run the simulation to calculate utilization, waiting time, and queue behavior.");
    instructions.setEditable(false);
    instructions.setLineWrap(true);
    instructions.setWrapStyleWord(true);
    instructions.setBorder(new EmptyBorder(14, 14, 14, 14));
    instructions.setBackground(new Color(248, 250, 252));

    panel.add(form, BorderLayout.NORTH);
    panel.add(instructions, BorderLayout.CENTER);

    runButton.addActionListener(e -> runSimulation());
    clearResultsButton.addActionListener(e -> clearResults());
    return panel;
  }

  private Component buildResultsPanel() {
    JPanel panel = new JPanel(new BorderLayout(0, 10));
    panel.setBorder(new EmptyBorder(12, 12, 12, 12));
    panel.setBackground(new Color(243, 246, 250));

    JPanel metrics = new JPanel(new GridBagLayout());
    metrics.setOpaque(false);
    metrics.add(createMetricCard("Machine Utilization", machineUtilizationLabel), metricConstraints(0));
    metrics.add(createMetricCard("Adjuster Utilization", adjusterUtilizationLabel), metricConstraints(1));
    metrics.add(createMetricCard("Total Failures", failuresLabel), metricConstraints(2));
    metrics.add(createMetricCard("Average Waiting", waitingLabel), metricConstraints(3));
    metrics.add(createMetricCard("Average Queue", queueLabel), metricConstraints(4));

    JTabbedPane tabs = new JTabbedPane();
    tabs.addTab("Overview", wrapArea(overviewArea));
    tabs.addTab("Category Metrics", wrapTable(new JTable(categorySummaryModel), null));
    tabs.addTab("Adjuster Metrics", wrapTable(new JTable(adjusterSummaryModel), null));

    panel.add(metrics, BorderLayout.NORTH);
    panel.add(tabs, BorderLayout.CENTER);
    return panel;
  }

  private JPanel createMetricCard(String title, JLabel valueLabel) {
    JPanel panel = new JPanel(new BorderLayout());
    panel.setBackground(Color.WHITE);
    panel.setBorder(BorderFactory.createCompoundBorder(
        new LineBorder(new Color(214, 220, 230)),
        new EmptyBorder(12, 14, 12, 14)));

    JLabel titleLabel = new JLabel(title);
    titleLabel.setForeground(new Color(92, 99, 111));
    titleLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
    valueLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
    valueLabel.setForeground(new Color(30, 41, 59));

    panel.add(titleLabel, BorderLayout.NORTH);
    panel.add(valueLabel, BorderLayout.CENTER);
    return panel;
  }

  private GridBagConstraints metricConstraints(int column) {
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = column;
    gbc.gridy = 0;
    gbc.weightx = 1.0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(0, 0, 0, 10);
    return gbc;
  }

  private GridBagConstraints constraints() {
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.insets = new Insets(8, 8, 8, 8);
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.weightx = 1.0;
    return gbc;
  }

  private void addField(JPanel panel, GridBagConstraints gbc, int row, String label, Component field) {
    gbc.gridy = row;
    gbc.gridwidth = 1;
    gbc.weightx = 0.0;
    gbc.fill = GridBagConstraints.NONE;

    gbc.gridx = 0;
    panel.add(new JLabel(label), gbc);

    gbc.gridx = 1;
    gbc.weightx = 1.0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    panel.add(field, gbc);
  }

  private JButton primaryButton(String text) {
    JButton button = new JButton(text);
    button.setFocusPainted(false);
    button.setBackground(new Color(41, 98, 255));
    
    return button;
  }

  private JButton secondaryButton(String text) {
    JButton button = new JButton(text);
    button.setFocusPainted(false);
    button.setBackground(new Color(229, 234, 242));
    return button;
  }

  private Component wrapTable(JTable table, String title) {
    javax.swing.JScrollPane scrollPane = new javax.swing.JScrollPane(table);
    if (title == null) {
      return scrollPane;
    }
    JPanel panel = new JPanel(new BorderLayout());
    panel.setBackground(Color.WHITE);
    panel.setBorder(new TitledBorder(new LineBorder(new Color(210, 215, 223)), title));
    panel.add(scrollPane, BorderLayout.CENTER);
    return panel;
  }

  private Component wrapArea(JTextArea area) {
    javax.swing.JScrollPane scrollPane = new javax.swing.JScrollPane(area);
    JPanel panel = new JPanel(new BorderLayout());
    panel.setBackground(Color.WHITE);
    panel.setBorder(new TitledBorder(new LineBorder(new Color(210, 215, 223)), "Simulation Summary"));
    panel.add(scrollPane, BorderLayout.CENTER);
    return panel;
  }

  private void addCategory() {
    try {
      MachineCategory category = new MachineCategory(
          categoryNameField.getText().trim(),
          ((Number) categoryQuantitySpinner.getValue()).intValue(),
          ((Number) mttfSpinner.getValue()).doubleValue(),
          ((Number) repairTimeSpinner.getValue()).doubleValue());
      categoryTableModel.addRow(new Object[] {
          category.getName(),
          category.getQuantity(),
          formatHours(category.getMeanTimeToFailureHours()),
          formatHours(category.getRepairTimeHours())
      });
      categoryNameField.setText("");
    } catch (IllegalArgumentException ex) {
      showError(ex.getMessage());
    }
  }

  private void addAdjuster() {
    try {
      Adjuster adjuster = new Adjuster(
          adjusterIdField.getText().trim(),
          adjusterNameField.getText().trim(),
          parseSkills(adjusterSkillsField.getText()));
      adjusterTableModel.addRow(new Object[] { adjuster.getId(), adjuster.getName(), adjuster.skillsAsText() });
      adjusterIdField.setText("");
      adjusterNameField.setText("");
      adjusterSkillsField.setText("");
    } catch (IllegalArgumentException ex) {
      showError(ex.getMessage());
    }
  }

  private Set<String> parseSkills(String text) {
    Set<String> skills = new LinkedHashSet<String>();
    if (text == null || text.trim().isEmpty()) {
      return skills;
    }
    String[] parts = text.split(",");
    for (String part : parts) {
      String value = part.trim();
      if (!value.isEmpty()) {
        skills.add(value);
      }
    }
    return skills;
  }

  private void runSimulation() {
    try {
      List<MachineCategory> categories = readCategories();
      List<Adjuster> adjusters = readAdjusters();
      double duration = ((Number) durationSpinner.getValue()).doubleValue();
      long seed = Long.parseLong(seedField.getText().trim());
      SimulationResult result = new SimulationEngine().run(categories, adjusters, duration, seed);
      updateResults(result);
    } catch (NumberFormatException ex) {
      showError("Seed must be a valid whole number");
    } catch (IllegalArgumentException ex) {
      showError(ex.getMessage());
    }
  }

  private List<MachineCategory> readCategories() {
    List<MachineCategory> categories = new ArrayList<MachineCategory>();
    for (int row = 0; row < categoryTableModel.getRowCount(); row++) {
      String name = String.valueOf(categoryTableModel.getValueAt(row, 0));
      int quantity = Integer.parseInt(String.valueOf(categoryTableModel.getValueAt(row, 1)));
      double mttf = parseHours(String.valueOf(categoryTableModel.getValueAt(row, 2)));
      double repair = parseHours(String.valueOf(categoryTableModel.getValueAt(row, 3)));
      categories.add(new MachineCategory(name, quantity, mttf, repair));
    }
    if (categories.isEmpty()) {
      throw new IllegalArgumentException("Add at least one machine category before running the simulation");
    }
    return categories;
  }

  private List<Adjuster> readAdjusters() {
    List<Adjuster> adjusters = new ArrayList<Adjuster>();
    for (int row = 0; row < adjusterTableModel.getRowCount(); row++) {
      String id = String.valueOf(adjusterTableModel.getValueAt(row, 0));
      String name = String.valueOf(adjusterTableModel.getValueAt(row, 1));
      String skillsText = String.valueOf(adjusterTableModel.getValueAt(row, 2));
      adjusters.add(new Adjuster(id, name, parseSkills(skillsText)));
    }
    if (adjusters.isEmpty()) {
      throw new IllegalArgumentException("Add at least one adjuster before running the simulation");
    }
    return adjusters;
  }

  private double parseHours(String text) {
    String cleaned = text.replace("hours", "").replace("hour", "").replace("h", "").trim();
    return Double.parseDouble(cleaned);
  }

  private void updateResults(SimulationResult result) {
    machineUtilizationLabel.setText(formatPercent(result.getMachineUtilization()));
    adjusterUtilizationLabel.setText(formatPercent(result.getAdjusterUtilization()));
    failuresLabel.setText(String.valueOf(result.getTotalFailures()));
    waitingLabel.setText(formatHours(result.getAverageWaitingTime()));
    queueLabel.setText(String.format("%.2f", result.getAverageQueueLength()));

    overviewArea.setText(result.buildOverviewText());

    categorySummaryModel.setRowCount(0);
    for (SimulationResult.CategorySummary summary : result.getCategorySummaries()) {
      categorySummaryModel.addRow(new Object[] {
          summary.getCategoryName(),
          summary.getMachineCount(),
          summary.getFailureCount(),
          formatPercent(summary.getUtilization())
      });
    }

    adjusterSummaryModel.setRowCount(0);
    for (SimulationResult.AdjusterSummary summary : result.getAdjusterSummaries()) {
      adjusterSummaryModel.addRow(new Object[] {
          summary.getAdjusterId(),
          summary.getAdjusterName(),
          summary.getSkills(),
          summary.getRepairsCompleted(),
          formatPercent(summary.getUtilization())
      });
    }
  }

  private void clearResults() {
    overviewArea.setText("");
    machineUtilizationLabel.setText("0.00%");
    adjusterUtilizationLabel.setText("0.00%");
    failuresLabel.setText("0");
    waitingLabel.setText("0.00 hours");
    queueLabel.setText("0.00");
    categorySummaryModel.setRowCount(0);
    adjusterSummaryModel.setRowCount(0);
  }

  private void loadSampleData() {
    categoryTableModel.setRowCount(0);
    adjusterTableModel.setRowCount(0);

    categoryTableModel.addRow(new Object[] { "Lathe Machines", 200, "120.0", "2.5" });
    categoryTableModel.addRow(new Object[] { "Turning Machines", 50, "90.0", "1.8" });
    categoryTableModel.addRow(new Object[] { "Drilling Machines", 70, "110.0", "2.0" });
    categoryTableModel.addRow(new Object[] { "Soldering Machines", 30, "95.0", "1.5" });

    adjusterTableModel.addRow(new Object[] { "A-1", "Alice", "Lathe Machines, Turning Machines" });
    adjusterTableModel.addRow(new Object[] { "A-2", "Bob", "Drilling Machines, Soldering Machines" });
    adjusterTableModel.addRow(new Object[] { "A-3", "Carla", "Lathe Machines, Drilling Machines" });
    adjusterTableModel.addRow(new Object[] { "A-4", "David", "Turning Machines, Soldering Machines, Lathe Machines" });

    durationSpinner.setValue(Double.valueOf(480.0));
    seedField.setText("42");
  }

  private void clearAll() {
    categoryTableModel.setRowCount(0);
    adjusterTableModel.setRowCount(0);
    clearResults();
  }

  private void removeSelectedRows(JTable table, DefaultTableModel model) {
    int[] selectedRows = table.getSelectedRows();
    if (selectedRows.length == 0) {
      return;
    }
    for (int i = selectedRows.length - 1; i >= 0; i--) {
      model.removeRow(selectedRows[i]);
    }
  }

  private void showError(String message) {
    JOptionPane.showMessageDialog(this, message, "Validation Error", JOptionPane.ERROR_MESSAGE);
  }

  private String formatPercent(double value) {
    return String.format("%.2f%%", value * 100.0);
  }

  private String formatHours(double value) {
    return String.format("%.2f hours", value);
  }
}
