package de.uni_passau.fim.talent_tauscher.dto;

import java.io.Serial;
import java.io.Serializable;

/** Represents a fatal error. */
public class ErrorDTO implements Serializable {
  @Serial private static final long serialVersionUID = 1L;
  private String message;
  private String exceptionMessage;
  private String exceptionClassName;
  private String errorString;
  private String stacktrace;

  /** Default constructor. */
  public ErrorDTO() {
    // Default constructor
  }

  /**
   * Gets the localized error message for the user.
   *
   * @return The localized error message for the user.
   */
  public String getMessage() {
    return message;
  }

  /**
   * Sets the localized error message for the user.
   *
   * @param message The localized error message for the user.
   */
  public void setMessage(String message) {
    this.message = message;
  }

  /**
   * Gets the exception message.
   *
   * @return The exception message.
   */
  public String getExceptionMessage() {
    return exceptionMessage;
  }

  /**
   * Sets the exception message.
   *
   * @param exceptionMessage The exception message.
   */
  public void setExceptionMessage(String exceptionMessage) {
    this.exceptionMessage = exceptionMessage;
  }

  /**
   * Gets the exception class name.
   *
   * @return The exception class name.
   */
  public String getExceptionClassName() {
    return exceptionClassName;
  }

  /**
   * Sets the exception class name.
   *
   * @param exceptionClassName The exception class name.
   */
  public void setExceptionClassName(String exceptionClassName) {
    this.exceptionClassName = exceptionClassName;
  }

  /**
   * Gets the error string representation.
   *
   * @return The error string representation.
   */
  public String getErrorString() {
    return errorString;
  }

  /**
   * Sets the error string representation.
   *
   * @param errorString The error string representation.
   */
  public void setErrorString(String errorString) {
    this.errorString = errorString;
  }

  /**
   * Gets the stack trace of the error.
   *
   * @return The stack trace of the error.
   */
  public String getStacktrace() {
    return stacktrace;
  }

  /**
   * Sets the stack trace of the error.
   *
   * @param stacktrace The stack trace of the error.
   */
  public void setStacktrace(String stacktrace) {
    this.stacktrace = stacktrace;
  }

  /**
   * Sets the exception details by extracting relevant information.
   *
   * @param exception The exception caught by the {@code AppExceptionHandler}.
   */
  public void setException(Throwable exception) {
    if (exception != null) {
      this.exceptionMessage = exception.getMessage();
      this.exceptionClassName = exception.getClass().getName();
      this.stacktrace = getStackTraceAsString(exception);
    }
  }

  /**
   * Converts the stack trace of a throwable to a string.
   *
   * @param throwable The throwable whose stack trace is to be converted.
   * @return The stack trace as a string.
   */
  private String getStackTraceAsString(Throwable throwable) {
    StringBuilder sb = new StringBuilder();
    for (StackTraceElement element : throwable.getStackTrace()) {
      sb.append(element.toString()).append("\n");
    }
    return sb.toString();
  }

  /**
   * Returns a string representation of the error.
   *
   * @return A string representation of the error.
   */
  @Override
  public String toString() {
    return "ErrorDTO{"
        + "message='"
        + message
        + '\''
        + ", exceptionMessage='"
        + exceptionMessage
        + '\''
        + ", exceptionClassName='"
        + exceptionClassName
        + '\''
        + ", errorString='"
        + errorString
        + '\''
        + ", stacktrace='"
        + stacktrace
        + '\''
        + '}';
  }
}
