package de.uni_passau.fim.talent_tauscher.dto;

import java.io.Serial;
import java.io.Serializable;

/** Represents detailed information about a user. */
public class UserDetailsDTO extends UserDTO implements Serializable {
  @Serial private static final long serialVersionUID = 1L;
  private String phone;
  private String postalCode;
  private String city;
  private String street;
  private String houseNumber;
  private String addressAddition;
  private AuthTokenDTO authToken;
  private String firstname;
  private String lastname;
  private String country;
  private boolean admin;

  /** Default constructor. */
  public UserDetailsDTO() {
    // Default constructor
  }

  /**
   * Gets the phone number of the user.
   *
   * @return The phone number of the user.
   */
  public String getPhone() {
    return phone;
  }

  /**
   * Sets the phone number of the user.
   *
   * @param phone The phone number of the user.
   */
  public void setPhone(String phone) {
    this.phone = phone;
  }

  /**
   * Gets the postal code of the user.
   *
   * @return The postal code of the user.
   */
  public String getPostalCode() {
    return postalCode;
  }

  /**
   * Sets the postal code of the user.
   *
   * @param postalCode The postal code of the user.
   */
  public void setPostalCode(String postalCode) {
    this.postalCode = postalCode;
  }

  /**
   * Gets the city of the user.
   *
   * @return The city of the user.
   */
  public String getCity() {
    return city;
  }

  /**
   * Sets the city of the user.
   *
   * @param city The city of the user.
   */
  public void setCity(String city) {
    this.city = city;
  }

  /**
   * Gets the street of the user.
   *
   * @return The street of the user.
   */
  public String getStreet() {
    return street;
  }

  /**
   * Sets the street of the user.
   *
   * @param street The street of the user.
   */
  public void setStreet(String street) {
    this.street = street;
  }

  /**
   * Gets the house number of the user.
   *
   * @return The house number of the user.
   */
  public String getHouseNumber() {
    return houseNumber;
  }

  /**
   * Sets the house number of the user.
   *
   * @param houseNumber The house number of the user.
   */
  public void setHouseNumber(String houseNumber) {
    this.houseNumber = houseNumber;
  }

  /**
   * Gets the address addition of the user.
   *
   * @return The address addition of the user.
   */
  public String getAddressAddition() {
    return addressAddition;
  }

  /**
   * Sets the address addition of the user.
   *
   * @param addressAddition The address addition of the user.
   */
  public void setAddressAddition(String addressAddition) {
    this.addressAddition = addressAddition;
  }

  /**
   * Gets the first name of the user.
   *
   * @return The first name of the user.
   */
  public String getFirstname() {
    return firstname;
  }

  /**
   * Sets the first name of the user.
   *
   * @param firstname The first name of the user.
   */
  public void setFirstname(String firstname) {
    this.firstname = firstname;
  }

  /**
   * Gets the last name of the user.
   *
   * @return The last name of the user.
   */
  public String getLastname() {
    return lastname;
  }

  /**
   * Sets the last name of the user.
   *
   * @param lastname The last name of the user.
   */
  public void setLastname(String lastname) {
    this.lastname = lastname;
  }

  /**
   * Gets the authentication token of the user.
   *
   * @return The authentication token of the user.
   */
  public AuthTokenDTO getAuthToken() {
    return authToken;
  }

  /**
   * Sets the authentication token of the user.
   *
   * @param authToken The authentication token of the user.
   */
  public void setAuthToken(AuthTokenDTO authToken) {
    this.authToken = authToken;
  }

  /**
   * Gets the country of the user.
   *
   * @return The country of the user.
   */
  public String getCountry() {
    return country;
  }

  /**
   * Sets the country of the user.
   *
   * @param country The country of the user.
   */
  public void setCountry(String country) {
    this.country = country;
  }

  /**
   * Checks if the user has administrator rights.
   *
   * @return True if the user has administrator rights, false otherwise.
   */
  public boolean isAdmin() {
    return admin;
  }

  /**
   * Sets whether the user has administrator rights.
   *
   * @param admin Whether the user has administrator rights.
   */
  public void setAdmin(boolean admin) {
    this.admin = admin;
  }
}
