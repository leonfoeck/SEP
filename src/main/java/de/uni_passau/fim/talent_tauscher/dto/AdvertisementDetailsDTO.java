package de.uni_passau.fim.talent_tauscher.dto;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

/** Represents detailed information about an advertisement. */
public class AdvertisementDetailsDTO extends AdvertisementDTO implements Serializable {
  @Serial private static final long serialVersionUID = 1L;
  private String description;
  private LocalDate startDate;
  private LocalDate endDate;
  private String street;
  private String email;
  private String phoneNumber;
  private boolean streetShown;
  private boolean nameShown;
  private boolean phoneNumberShown;
  private boolean hidden;

  /** Default constructor. */
  public AdvertisementDetailsDTO() {
    // Default constructor
  }

  /**
   * Gets the detail description of the advertisement.
   *
   * @return The detail description.
   */
  public String getDescription() {
    return description;
  }

  /**
   * Sets the detail description.
   *
   * @param description The detail description.
   */
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * Gets the start date of the availability period.
   *
   * @return The start date of the availability period.
   */
  public LocalDate getStartDate() {
    return startDate;
  }

  /**
   * Sets the start date of the availability period.
   *
   * @param startDate The start date of the availability period.
   */
  public void setStartDate(LocalDate startDate) {
    this.startDate = startDate;
  }

  /**
   * Gets the end date of the availability period.
   *
   * @return The end date of the availability period.
   */
  public LocalDate getEndDate() {
    return endDate;
  }

  /**
   * Sets the end date of the availability period.
   *
   * @param endDate The end date of the availability period.
   */
  public void setEndDate(LocalDate endDate) {
    this.endDate = endDate;
  }

  /**
   * Checks if the advertisement is hidden.
   *
   * @return True if the advertisement is hidden, false otherwise.
   */
  public boolean isHidden() {
    return hidden;
  }

  /**
   * Sets whether the advertisement is hidden.
   *
   * @param hidden Whether the advertisement is hidden.
   */
  public void setHidden(boolean hidden) {
    this.hidden = hidden;
  }

  /**
   * Checks if the street of the creator is shown to other users.
   *
   * @return True if the street is shown, false otherwise.
   */
  public boolean isStreetShown() {
    return streetShown;
  }

  /**
   * Sets whether the street of the creator should be shown to other users.
   *
   * @param streetShown Whether the street of the creator should be shown to other users.
   */
  public void setStreetShown(boolean streetShown) {
    this.streetShown = streetShown;
  }

  /**
   * Checks if the name of the creator is shown to other users.
   *
   * @return True if the name is shown, false otherwise.
   */
  public boolean isNameShown() {
    return nameShown;
  }

  /**
   * Sets whether the name of the creator should be shown to other users.
   *
   * @param nameShown Whether the name of the creator should be shown to other users.
   */
  public void setNameShown(boolean nameShown) {
    this.nameShown = nameShown;
  }

  /**
   * Checks if the phone number is shown to other users.
   *
   * @return True if the phone number is shown, false otherwise.
   */
  public boolean isPhoneNumberShown() {
    return phoneNumberShown;
  }

  /**
   * Sets whether the phone number should be shown to other users.
   *
   * @param phoneNumberShown Whether the phone number should be shown to other users.
   */
  public void setPhoneNumberShown(boolean phoneNumberShown) {
    this.phoneNumberShown = phoneNumberShown;
  }

  /**
   * Gets the street of the ad-specific contact data.
   *
   * @return The street.
   */
  public String getStreet() {
    return street;
  }

  /**
   * Sets the street of the ad-specific contact data.
   *
   * @param street The street.
   */
  public void setStreet(String street) {
    this.street = street;
  }

  /**
   * Gets the email address of the ad-specific contact data.
   *
   * @return The email address.
   */
  public String getEmail() {
    return email;
  }

  /**
   * Sets the email address of the ad-specific contact data.
   *
   * @param email The email address.
   */
  public void setEmail(String email) {
    this.email = email;
  }

  /**
   * Gets the phone number of the ad-specific contact data.
   *
   * @return The phone number.
   */
  public String getPhoneNumber() {
    return phoneNumber;
  }

  /**
   * Sets the phone number of the ad-specific contact data.
   *
   * @param phoneNumber The phone number.
   */
  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }
}
