package de.uni_passau.fim.talent_tauscher.dto;

import java.io.Serial;
import java.io.Serializable;

/** Represents help information for a particular view or section of the application. */
public class HelpDTO implements Serializable {
  @Serial private static final long serialVersionUID = 1L;
  private String text;
  private boolean visible;

  /** Default constructor. */
  public HelpDTO() {
    // Default constructor
  }

  /**
   * Gets the help text.
   *
   * @return The help text.
   */
  public String getText() {
    return text;
  }

  /**
   * Sets the help text.
   *
   * @param text The help text.
   */
  public void setText(String text) {
    this.text = text;
  }

  /**
   * Checks if the help text is visible.
   *
   * @return True if the help text is visible, false otherwise.
   */
  public boolean isVisible() {
    return visible;
  }

  /**
   * Sets the visibility of the help text.
   *
   * @param visible True to make the help text visible, false otherwise.
   */
  public void setVisible(boolean visible) {
    this.visible = visible;
  }
}
