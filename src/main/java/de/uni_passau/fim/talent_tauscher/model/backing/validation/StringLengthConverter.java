package de.uni_passau.fim.talent_tauscher.model.backing.validation;

import jakarta.enterprise.context.Dependent;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;
import jakarta.inject.Named;

/**
 * This converter takes a string and cuts it off at a specified maximum length. It only works
 * one-way, i.e., only the {@link #getAsString(FacesContext, UIComponent, String)} method is
 * supported. Therefor it should only be used on output components. The following example
 * illustrates the basic usage of this converter:
 *
 * <pre>
 *   &lt;h:outputPanel value="#{bean.someString}"&gt;
 *     &lt;o:converter converterId="stringLengthConverter" maxLength="10" /&gt;
 *   &lt;/h:outputPanel&gt;
 * </pre>
 *
 * Note that the {@code <o:converter>} tag must be used to make the parameterization by the {@code
 * maxLength} attribute work.
 */
@Named
@Dependent
@FacesConverter(value = "stringLengthConverter", managed = true)
public class StringLengthConverter implements Converter<String> {

  private static final int MAX_LENGTH = 100;

  /**
   * Not supported.
   *
   * @throws UnsupportedOperationException If called.
   */
  @Override
  public String getAsObject(FacesContext facesContext, UIComponent uiComponent, String s) {
    throw new UnsupportedOperationException();
  }

  /**
   * Cuts of the given string at the specified maximum length.
   *
   * @param facesContext The faces context of the request.
   * @param uiComponent The UI component which the converter is used on.
   * @param s The string to cut off.
   * @return The first part of the string that has not been cut off.
   */
  @Override
  public String getAsString(FacesContext facesContext, UIComponent uiComponent, String s) {
    if (s == null) {
      return "";
    }
    return s.substring(0, Math.min(MAX_LENGTH, s.length()));
  }
}
