package de.uni_passau.fim.talent_tauscher.model.backing.exceptions;

import jakarta.faces.context.ExceptionHandlerFactory;

/**
 * Factory class for creating instances of the {@link AppExceptionHandler} class.
 *
 * @author Sturm
 */
public class AppExceptionHandlerFactory extends ExceptionHandlerFactory {

  /**
   * Constructor for the AppExceptionHandlerFactory class.
   *
   * @param wrapped The wrapped ExceptionHandlerFactory.
   */
  public AppExceptionHandlerFactory(ExceptionHandlerFactory wrapped) {
    super(wrapped);
  }

  /**
   * Creates a new {@link AppExceptionHandler} instance.
   *
   * @return The created instance.
   */
  @Override
  public AppExceptionHandler getExceptionHandler() {
    return new AppExceptionHandler(getWrapped().getExceptionHandler());
  }
}
