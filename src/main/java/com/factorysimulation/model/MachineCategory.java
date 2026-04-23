package com.factorysimulation.model;

public class MachineCategory {
  private final String name;
  private final int quantity;
  private final double meanTimeToFailureHours;
  private final double repairTimeHours;

  public MachineCategory(String name, int quantity, double meanTimeToFailureHours, double repairTimeHours) {
    if (name == null || name.trim().isEmpty()) {
      throw new IllegalArgumentException("Category name is required");
    }
    if (quantity <= 0) {
      throw new IllegalArgumentException("Quantity must be greater than zero");
    }
    if (meanTimeToFailureHours <= 0 || repairTimeHours <= 0) {
      throw new IllegalArgumentException("Times must be greater than zero");
    }
    this.name = name.trim();
    this.quantity = quantity;
    this.meanTimeToFailureHours = meanTimeToFailureHours;
    this.repairTimeHours = repairTimeHours;
  }

  public String getName() {
    return name;
  }

  public int getQuantity() {
    return quantity;
  }

  public double getMeanTimeToFailureHours() {
    return meanTimeToFailureHours;
  }

  public double getRepairTimeHours() {
    return repairTimeHours;
  }
}
