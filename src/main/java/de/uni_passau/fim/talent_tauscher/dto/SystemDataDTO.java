package de.uni_passau.fim.talent_tauscher.dto;

import java.io.Serial;
import java.io.Serializable;
import org.omnifaces.cdi.GraphicImageBean;

/** Represents the system configuration data. */
@GraphicImageBean
public class SystemDataDTO implements Serializable {
  @Serial private static final long serialVersionUID = 1L;
  private String contact;
  private String imprint;
  private String privacyPolicy;
  private boolean adminConfirmationRequiredForRegistration;
  private long maxImageSize;
  private byte[] logo;
  private String cssStylesheetName;
  private int sumPaginatedItems;

  /** Default constructor. */
  public SystemDataDTO() {
    // Default constructor
  }

  /**
   * Gets the contact information of the administrators.
   *
   * @return The contact information of the administrators.
   */
  public String getContact() {
    return contact;
  }

  /**
   * Sets the contact information of the administrators.
   *
   * @param contact The contact information of the administrators.
   */
  public void setContact(String contact) {
    this.contact = contact;
  }

  /**
   * Gets the imprint.
   *
   * @return The imprint.
   */
  public String getImprint() {
    return imprint;
  }

  /**
   * Sets the imprint.
   *
   * @param imprint The imprint.
   */
  public void setImprint(String imprint) {
    this.imprint = imprint;
  }

  /**
   * Gets the privacy policy.
   *
   * @return The privacy policy.
   */
  public String getPrivacyPolicy() {
    return privacyPolicy;
  }

  /**
   * Sets the privacy policy.
   *
   * @param privacyPolicy The privacy policy.
   */
  public void setPrivacyPolicy(String privacyPolicy) {
    this.privacyPolicy = privacyPolicy;
  }

  /**
   * Checks whether the confirmation of an administrator is required to complete registration.
   *
   * @return {@code true} if the confirmation is required, {@code false} otherwise.
   */
  public boolean isAdminConfirmationRequiredForRegistration() {
    return adminConfirmationRequiredForRegistration;
  }

  /**
   * Sets whether the confirmation of an administrator is required to complete registration.
   *
   * @param adminConfirmationRequiredForRegistration Whether the confirmation of an administrator is
   *     required to register.
   */
  public void setAdminConfirmationRequiredForRegistration(
      boolean adminConfirmationRequiredForRegistration) {
    this.adminConfirmationRequiredForRegistration = adminConfirmationRequiredForRegistration;
  }

  /**
   * Gets the maximum allowed image size in bytes.
   *
   * @return The maximum allowed image size in bytes.
   */
  public long getMaxImageSize() {
    return maxImageSize;
  }

  /**
   * Sets the maximum allowed image size in bytes.
   *
   * @param maxImageSize The maximum allowed image size in bytes.
   */
  public void setMaxImageSize(long maxImageSize) {
    this.maxImageSize = maxImageSize;
  }

  /**
   * Gets a copy of the logo of the application.
   *
   * @return A copy of the logo of the application.
   */
  public byte[] getLogo() {
    return logo == null ? null : logo.clone();
  }

  /**
   * Sets the logo of the application by copying the input array.
   *
   * @param logo The logo of the application.
   */
  public void setLogo(byte[] logo) {
    this.logo = logo == null ? null : logo.clone();
  }

  /**
   * Gets the name of the CSS stylesheet used.
   *
   * @return The name of the CSS stylesheet used.
   */
  public String getCssStylesheetName() {
    return cssStylesheetName;
  }

  /**
   * Sets the name of the CSS stylesheet used.
   *
   * @param cssStylesheetName The name of the CSS stylesheet used.
   */
  public void setCssStylesheetName(String cssStylesheetName) {
    this.cssStylesheetName = cssStylesheetName;
  }

  /**
   * Gets the total number of paginated items.
   *
   * @return The total number of paginated items.
   */
  public int getSumPaginatedItems() {
    return sumPaginatedItems;
  }

  /**
   * Sets the total number of paginated items.
   *
   * @param sumPaginatedItems The total number of paginated items.
   */
  public void setSumPaginatedItems(int sumPaginatedItems) {
    this.sumPaginatedItems = sumPaginatedItems;
  }
}
