package de.uni_passau.fim.talent_tauscher.model.backing.beans;

import de.uni_passau.fim.talent_tauscher.dto.ErrorDTO;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;

/** Backing bean for the error page. */
@Named
@RequestScoped
public class ErrorBean {

  private ErrorDTO error;

  /** Default constructor. */
  public ErrorBean() {
    // Default constructor
  }

  /**
   * Gets the error that caused this page to be shown.
   *
   * @return The error.
   */
  public ErrorDTO getError() {
    if (error == null) {
      ExternalContext extCtx = FacesContext.getCurrentInstance().getExternalContext();
      error = (ErrorDTO) extCtx.getFlash().get("errorDTO");
      if (error == null) {
        error = new ErrorDTO();
      }
    }
    return error;
  }

  /**
   * Sets the error that caused this page to be shown.
   *
   * @param error The error.
   */
  public void setError(ErrorDTO error) {
    this.error = error;
  }
}
