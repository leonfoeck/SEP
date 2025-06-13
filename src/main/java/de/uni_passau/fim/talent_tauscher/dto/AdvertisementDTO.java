package de.uni_passau.fim.talent_tauscher.dto;

import java.io.Serial;
import java.io.Serializable;

/** Represents basic information about an advertisement. */
public class AdvertisementDTO implements Serializable {

  @Serial private static final long serialVersionUID = 1L;
  private long id;
  private String title;
  private long userId;
  private int cost;
  private String postalCode;
  private String country;
  private String city;
  private byte[] image;

  /** Default constructor. */
  public AdvertisementDTO() {
    // Default constructor
  }

  /**
   * Gets the id of the advertisement.
   *
   * @return The id of the advertisement.
   */
  public long getId() {
    return id;
  }

  /**
   * Sets the id of the advertisement.
   *
   * @param id The id of the advertisement.
   */
  public void setId(long id) {
    this.id = id;
  }

  /**
   * Gets the title of the advertisement.
   *
   * @return The title of the advertisement.
   */
  public String getTitle() {
    return title;
  }

  /**
   * Sets the title of the advertisement.
   *
   * @param title The title of the advertisement.
   */
  public void setTitle(String title) {
    this.title = title;
  }

  /**
   * Gets the user id of the advertisement creator.
   *
   * @return The user id of the advertisement creator.
   */
  public long getUserId() {
    return userId;
  }

  /**
   * Sets the user id of the advertisement creator.
   *
   * @param userId The user id of the advertisement creator.
   */
  public void setUserId(long userId) {
    this.userId = userId;
  }

  /**
   * Gets the cost in talent points.
   *
   * @return The cost in talent points.
   */
  public int getCost() {
    return cost;
  }

  /**
   * Sets the cost in talent points.
   *
   * @param cost The cost in talent points.
   */
  public void setCost(int cost) {
    this.cost = cost;
  }

  /**
   * Gets the postal code of the advertisement location.
   *
   * @return The postal code of the advertisement location.
   */
  public String getPostalCode() {
    return postalCode;
  }

  /**
   * Sets the postal code of the advertisement location.
   *
   * @param postalCode The postal code of the advertisement location.
   */
  public void setPostalCode(String postalCode) {
    this.postalCode = postalCode;
  }

  /**
   * Gets the city of the advertisement location.
   *
   * @return The city of the advertisement location.
   */
  public String getCity() {
    return city;
  }

  /**
   * Gets the country.
   *
   * @return The country.
   */
  public String getCountry() {
    return country;
  }

  /**
   * Sets the country.
   *
   * @param country The country.
   */
  public void setCountry(String country) {
    this.country = country;
  }

  /**
   * Sets the city of the advertisement location.
   *
   * @param city The city of the advertisement location.
   */
  public void setCity(String city) {
    this.city = city;
  }

  /**
   * Gets a copy of the image of the advertisement.
   *
   * @return A copy of the image of the advertisement.
   */
  public byte[] getImage() {
    return image == null ? null : image.clone();
  }

  /**
   * Sets the image of the advertisement by copying the input array.
   *
   * @param image The image of the advertisement.
   */
  public void setImage(byte[] image) {
    this.image = image == null ? null : image.clone();
  }
}
