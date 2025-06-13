package de.uni_passau.fim.talent_tauscher.model.business.services;

import de.uni_passau.fim.talent_tauscher.dto.AuthTokenDTO;
import de.uni_passau.fim.talent_tauscher.dto.LoginDTO;
import de.uni_passau.fim.talent_tauscher.dto.PaginationDTO;
import de.uni_passau.fim.talent_tauscher.dto.PasswordInputDTO;
import de.uni_passau.fim.talent_tauscher.dto.PasswordResetDTO;
import de.uni_passau.fim.talent_tauscher.dto.UserAuthenticationDTO;
import de.uni_passau.fim.talent_tauscher.dto.UserDTO;
import de.uni_passau.fim.talent_tauscher.dto.UserDetailsDTO;
import de.uni_passau.fim.talent_tauscher.dto.VerificationTokenDTO;
import de.uni_passau.fim.talent_tauscher.model.business.util.PasswordAuthentication;
import de.uni_passau.fim.talent_tauscher.model.persistence.dataaccess.TransactionFactory;
import de.uni_passau.fim.talent_tauscher.model.persistence.dataaccess.interfaces.Transaction;
import de.uni_passau.fim.talent_tauscher.model.persistence.dataaccess.interfaces.UserDAO;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;

/** Manages interactions with {@link UserDAO}. */
@ApplicationScoped
public class UserService implements Serializable {

  @Serial private static final long serialVersionUID = 1L;

  @Inject private PasswordAuthentication passwordAuthentication;

  /**
   * Creates a new user.
   *
   * @param user The user data.
   * @param passwordInput The input password.
   * @return The id of the newly created user.
   * @author Sturm
   */
  public long create(UserDetailsDTO user, PasswordInputDTO passwordInput) {
    AuthTokenDTO authToken = passwordAuthentication.hash(passwordInput.getPassword());
    user.setAuthToken(authToken);

    try (Transaction transaction = TransactionFactory.createTransaction()) {
      UserDAO userDAO = transaction.getDAOFactory().createUserDAO();

      UserAuthenticationDTO userCheck = new UserAuthenticationDTO();
      userCheck.setNickname(user.getNickname());
      userCheck.setEmail(user.getEmail());

      if (userDAO.findUserByNickname(userCheck).isPresent()
          || userDAO.findUserByEmail(userCheck).isPresent()) {
        transaction.commit();
        return -1L;
      } else {
        long userId = userDAO.createUser(user);
        transaction.commit();
        return userId;
      }
    }
  }

  /**
   * Updates the given user.
   *
   * @param user The user to update.
   * @param passwordInput The input password DTO.
   * @return Whether update was successful.
   * @author Sturm
   */
  public boolean update(UserDetailsDTO user, PasswordInputDTO passwordInput) {
    try (Transaction transaction = TransactionFactory.createTransaction()) {
      UserDAO userDAO = transaction.getDAOFactory().createUserDAO();

      UserAuthenticationDTO uniqueCheck = new UserAuthenticationDTO();
      uniqueCheck.setNickname(user.getNickname());
      uniqueCheck.setEmail(user.getEmail());

      Optional<UserAuthenticationDTO> foundUser = userDAO.findUserByEmail(uniqueCheck);
      if (foundUser.isPresent() && foundUser.get().getId() != user.getId()) {
        transaction.commit();
        return false;
      }
      foundUser = userDAO.findUserByNickname(uniqueCheck);
      if (foundUser.isPresent() && foundUser.get().getId() != user.getId()) {
        transaction.commit();
        return false;
      }

      if (!(passwordInput.getPassword() == null || passwordInput.getPassword().equals(""))
          || !(passwordInput.getRepetition() == null || passwordInput.getRepetition().equals(""))) {
        if (!passwordInput.getPassword().equals(passwordInput.getRepetition())) {
          transaction.commit();
          return false;
        }
        user.setAuthToken(passwordAuthentication.hash(passwordInput.getPassword()));
      }
      userDAO.updateUser(user);
      transaction.commit();
      return true;
    }
  }

  /**
   * Deletes the given user.
   *
   * @param user The user to delete.
   * @author Sturm
   */
  public void delete(UserDTO user) {
    try (Transaction transaction = TransactionFactory.createTransaction()) {
      UserDAO userDAO = transaction.getDAOFactory().createUserDAO();
      userDAO.deleteUser(user);
      transaction.commit();
    }
  }

  /**
   * Checks if the given login credentials can be used to log in to the application. If so, the
   * associated user is returned.
   *
   * @param login The login credentials.
   * @return The user associated with the login credentials, wrapped into an {@link Optional}. If
   *     the credentials are not valid, {@code Optional.empty()} is returned.
   */
  public Optional<UserAuthenticationDTO> authenticate(LoginDTO login) {
    try (Transaction transaction = TransactionFactory.createTransaction()) {
      UserDAO userDAO = transaction.getDAOFactory().createUserDAO();
      UserAuthenticationDTO user = new UserAuthenticationDTO();

      Optional<UserAuthenticationDTO> foundUser;
      String nick = login.getNickname();

      if (nick.contains("@")) {
        user.setEmail(nick);
        foundUser = userDAO.findUserByEmail(user);
      } else {
        user.setNickname(nick);
        foundUser = userDAO.findUserByNickname(user);
      }

      if (foundUser.isPresent()
          && passwordAuthentication.authenticate(
              login.getPassword(), foundUser.get().getAuthToken())) {
        return foundUser;
      } else {
        return Optional.empty();
      }
    }
  }

  /**
   * Checks if a nickname is unique among all users.
   *
   * @param user The user. The nickname must be set.
   * @return {@code true} if the nickname is unique, {@code false} otherwise.
   * @author Sturm
   */
  public boolean isNicknameUnique(UserAuthenticationDTO user) {
    try (Transaction transaction = TransactionFactory.createTransaction()) {
      UserDAO userDAO = transaction.getDAOFactory().createUserDAO();
      Optional<UserAuthenticationDTO> foundUser = userDAO.findUserByNickname(user);
      transaction.commit();
      return foundUser.isEmpty();
    }
  }

  /**
   * Checks if an email address is unique among all users.
   *
   * @param user The user. The email address must be set.
   * @return {@code true} if the email address is unique, {@code false} otherwise.
   * @author Sturm
   */
  public boolean isEmailUnique(UserAuthenticationDTO user) {
    try (Transaction transaction = TransactionFactory.createTransaction()) {
      UserDAO userDAO = transaction.getDAOFactory().createUserDAO();
      Optional<UserAuthenticationDTO> foundUser = userDAO.findUserByEmail(user);
      transaction.commit();
      return foundUser.isEmpty();
    }
  }

  /**
   * Retrieves the total number of users that match the filter constraints in {@code pagination}.
   *
   * @param pagination The pagination information.
   * @return The number of matching users.
   */
  public int getUserCount(PaginationDTO pagination) {

    try (Transaction transaction = TransactionFactory.createTransaction(); ) {
      UserDAO userDAO = transaction.getDAOFactory().createUserDAO();
      int userCount = userDAO.getCount(pagination);
      transaction.commit();

      return userCount;
    }
  }

  /**
   * Retrieves a list of users. The results are filtered, sorted and paginated according to the
   * given pagination information.
   *
   * @param pagination The pagination information.
   * @return The list of users.
   */
  public List<UserDTO> getUsers(PaginationDTO pagination) {
    try (Transaction transaction = TransactionFactory.createTransaction(); ) {
      UserDAO userDAO = transaction.getDAOFactory().createUserDAO();
      List<UserDTO> users = userDAO.getUsers(pagination);
      transaction.commit();
      return users;
    }
  }

  /**
   * Retrieves the user with a given email address, if one exists.
   *
   * @param user The user. The email address must be set.
   * @return The user wrapped into an {@link Optional}, or {@code Optional.empty()} if no user with
   *     that email exists.
   * @author Jakob Edmaier
   */
  public Optional<UserAuthenticationDTO> findUserByEmail(UserAuthenticationDTO user) {
    try (Transaction transaction = TransactionFactory.createTransaction(); ) {
      UserDAO userDAO = transaction.getDAOFactory().createUserDAO();
      Optional<UserAuthenticationDTO> result = userDAO.findUserByEmail(user);
      transaction.commit();
      return result;
    }
  }

  /**
   * Sets an email verification token for a user.
   *
   * @param token The verification token.
   * @author Jakob Edmaier
   */
  public void putEmailVerificationToken(VerificationTokenDTO token) {
    try (Transaction transaction = TransactionFactory.createTransaction(); ) {
      UserDAO userDAO = transaction.getDAOFactory().createUserDAO();
      userDAO.putEmailVerificationToken(token);
      transaction.commit();
    }
  }

  /**
   * Sets a password-reset verification token for a user.
   *
   * @param token The verification token.
   * @author Jakob Edmaier
   */
  public void putPasswordResetToken(VerificationTokenDTO token) {
    try (Transaction transaction = TransactionFactory.createTransaction()) {
      UserDAO userDAO = transaction.getDAOFactory().createUserDAO();
      userDAO.putPasswordResetToken(token);
      transaction.commit();
    }
  }

  /**
   * Retrieves detailed data about the user with a specified id.
   *
   * @param user The user. The id must be set.
   * @return The user.
   * @author Jakob Edmaier
   */
  public UserDetailsDTO getUserDetails(UserDetailsDTO user) {
    try (Transaction transaction = TransactionFactory.createTransaction()) {
      UserDAO userDAO = transaction.getDAOFactory().createUserDAO();
      UserDetailsDTO result = userDAO.getUserDetails(user);
      transaction.commit();
      return result;
    }
  }

  /**
   * Retrieves the user with the specified id.
   *
   * @param user The user. The id must be set.
   * @return The user.
   */
  public UserDTO getUser(UserDTO user) {
    try (Transaction transaction = TransactionFactory.createTransaction()) {
      UserDAO userDAO = transaction.getDAOFactory().createUserDAO();
      UserDTO result = userDAO.getUser(user);
      transaction.commit();
      return result;
    }
  }

  /**
   * Retrieves the data relevant for authentication and authorization of the user with the specified
   * id.
   *
   * @param user The user. The id must be set.
   * @return The user data.
   * @author Jakob Edmaier
   */
  public UserAuthenticationDTO getAuthDataOfUser(UserAuthenticationDTO user) {
    try (Transaction transaction = TransactionFactory.createTransaction()) {
      UserDAO userDAO = transaction.getDAOFactory().createUserDAO();
      UserAuthenticationDTO result = userDAO.getAuthDataOfUser(user);
      transaction.commit();
      return result;
    }
  }

  /**
   * Verifies the email of the user with the provided verification token.
   *
   * @param verificationToken The verification token.
   * @author Jakob Edmaier
   */
  public void verifyEmail(VerificationTokenDTO verificationToken) {
    try (Transaction transaction = TransactionFactory.createTransaction()) {
      UserDAO userDAO = transaction.getDAOFactory().createUserDAO();
      userDAO.verifyEmail(verificationToken);
      transaction.commit();
    }
  }

  /**
   * Resets the password of a user.
   *
   * @param passwordReset The password reset data consisting of a verification token and the hashed
   *     password.
   * @author Jakob Edmaier
   */
  public void resetPassword(PasswordResetDTO passwordReset) {
    try (Transaction transaction = TransactionFactory.createTransaction()) {
      UserDAO userDAO = transaction.getDAOFactory().createUserDAO();
      userDAO.resetPassword(passwordReset);
      transaction.commit();
    }
  }

  /**
   * Resets the password of a user.
   *
   * @param user The {@link UserAuthenticationDTO} to fill.
   * @param passwordReset The password reset data consisting of a verification token and the hashed
   *     password.
   * @return The user with the password reset secret.
   * @author Jakob Edmaier
   */
  public UserAuthenticationDTO getUserByPasswordResetSecret(
      UserAuthenticationDTO user, PasswordResetDTO passwordReset) {
    try (Transaction transaction = TransactionFactory.createTransaction()) {
      UserDAO userDAO = transaction.getDAOFactory().createUserDAO();
      UserAuthenticationDTO result = userDAO.getUserByPasswordResetSecret(user, passwordReset);
      transaction.commit();
      return result;
    }
  }

  /**
   * Marks all users as verified by an admin.
   *
   * @author Sturm
   */
  public void setAllUsersAdminVerified() {
    try (Transaction transaction = TransactionFactory.createTransaction()) {
      UserDAO userDAO = transaction.getDAOFactory().createUserDAO();
      userDAO.verifyAllUsers();
      transaction.commit();
    }
  }
}
