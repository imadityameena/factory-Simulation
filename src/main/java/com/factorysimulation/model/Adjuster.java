package com.factorysimulation.model;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

public class Adjuster {
  private final String id;
  private final String name;
  private final Set<String> skills;

  public Adjuster(String id, String name, Set<String> skills) {
    if (id == null || id.trim().isEmpty()) {
      throw new IllegalArgumentException("Adjuster id is required");
    }
    if (name == null || name.trim().isEmpty()) {
      throw new IllegalArgumentException("Adjuster name is required");
    }
    this.id = id.trim();
    this.name = name.trim();
    this.skills = new LinkedHashSet<String>();
    if (skills != null) {
      for (String skill : skills) {
        if (skill != null && !skill.trim().isEmpty()) {
          this.skills.add(normalize(skill));
        }
      }
    }
  }

  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public Set<String> getSkills() {
    return Collections.unmodifiableSet(skills);
  }

  public boolean canRepair(String categoryName) {
    if (skills.isEmpty()) {
      return true;
    }
    return skills.contains(normalize(categoryName));
  }

  public String skillsAsText() {
    if (skills.isEmpty()) {
      return "All categories";
    }
    StringBuilder builder = new StringBuilder();
    for (String skill : skills) {
      if (builder.length() > 0) {
        builder.append(", ");
      }
      builder.append(skill);
    }
    return builder.toString();
  }

  private String normalize(String value) {
    return value.trim().toLowerCase(Locale.ENGLISH);
  }
}
