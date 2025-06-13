package de.uni_passau.fim.talent_tauscher.model.persistence.dataaccess.interfaces;

/** Interface for factory classes for creating DAO’s. */
public interface DAOFactory {

  /**
   * Creates a new {@link UserDAO} instance.
   *
   * @return The created instance.
   */
  UserDAO createUserDAO();

  /**
   * Creates a new {@link AdvertisementDAO} instance.
   *
   * @return The created instance.
   */
  AdvertisementDAO createAdvertisementDAO();

  /**
   * Creates a new {@link SystemDataDAO} instance.
   *
   * @return The created instance.
   */
  SystemDataDAO createSystemDataDAO();

  /**
   * Creates a new {@link RequestDAO} instance.
   *
   * @return The created instance.
   */
  RequestDAO createRequestDAO();
}
