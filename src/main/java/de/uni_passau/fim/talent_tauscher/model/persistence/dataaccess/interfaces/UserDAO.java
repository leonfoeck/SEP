package de.uni_passau.fim.talent_tauscher.model.persistence.dataaccess.interfaces;

import de.uni_passau.fim.talent_tauscher.dto.PaginationDTO;
import de.uni_passau.fim.talent_tauscher.dto.PasswordResetDTO;
import de.uni_passau.fim.talent_tauscher.dto.RequestDTO;
import de.uni_passau.fim.talent_tauscher.dto.UserAuthenticationDTO;
import de.uni_passau.fim.talent_tauscher.dto.UserDTO;
import de.uni_passau.fim.talent_tauscher.dto.UserDetailsDTO;
import de.uni_passau.fim.talent_tauscher.dto.VerificationTokenDTO;
import java.util.List;
import java.util.Optional;

/** Interface for data store accesses concerning user management. */
public interface UserDAO {

  /**
   * Fetches the total number of users that match the filter constraints in {@code pagination}.
   *
   * @param pagination The pagination information.
   * @return The total number of matching users.
   */
  int getCount(PaginationDTO pagination);

  /**
   * Fetches the user with a given nickname.
   *
   * @param user The user to fetch. The nickname must be set.
   * @return The user wrapped into an {@link Optional}, or {@code Optional.empty()} if no user with
   *     that nickname exists.
   */
  Optional<UserAuthenticationDTO> findUserByNickname(UserAuthenticationDTO user);

  /**
   * Fetches the user with a given email address.
   *
   * @param user The user to fetch. The email address must be set.
   * @return The user wrapped into an {@link Optional}, or {@code Optional.empty()} if no user with
   *     that email exists.
   */
  Optional<UserAuthenticationDTO> findUserByEmail(UserAuthenticationDTO user);

  /**
   * Fetches the user with a given id, returning only the data relevant for authentication and
   * authorization. In particular, this method does not fetch the user's avatar image.
   *
   * @param user The user to fetch. The id must be set.
   * @return The fetched data.
   */
  UserAuthenticationDTO getAuthDataOfUser(UserAuthenticationDTO user);

  /**
   * Fetches detailed data about the user with a given id.
   *
   * @param user The user to fetch. The id must be set.
   * @return The user.
   */
  UserDetailsDTO getUserDetails(UserDetailsDTO user);

  /**
   * Fetches the user with a given id.
   *
   * @param user The user to fetch. The id must be set.
   * @return The user.
   */
  UserDTO getUser(UserDTO user);

  /**
   * Fetches a list of users. The results are filtered, sorted and paginated according to the given
   * pagination information.
   *
   * @param pagination The pagination information.
   * @return The list of users.
   */
  List<UserDTO> getUsers(PaginationDTO pagination);

  /**
   * Stores a new user to the data store.
   *
   * @param user The user to store.
   * @return The id of the newly created user.
   */
  long createUser(UserDetailsDTO user);

  /**
   * Updates the given user in the data store.
   *
   * @param user The user to update.
   */
  void updateUser(UserDetailsDTO user);

  /**
   * Deletes the given user from the data store.
   *
   * @param user The user to delete.
   */
  void deleteUser(UserDTO user);

  /**
   * Stores an email verification token to the data store.
   *
   * @param token The verification token.
   */
  void putEmailVerificationToken(VerificationTokenDTO token);

  /**
   * Stores a password-reset verification token to the data store.
   *
   * @param token The verification token.
   */
  void putPasswordResetToken(VerificationTokenDTO token);

  /**
   * Verifies the email of the user with the provided verification token.
   *
   * @param token The verification token.
   * @return {@code true} if the verification was successful, {@code false} otherwise.
   */
  boolean verifyEmail(VerificationTokenDTO token);

  /**
   * Resets the password of a user.
   *
   * @param passwordReset The password reset data, consisting of a verification token and the hashed
   *     password.
   * @return {@code true} if the reset was successful, {@code false} otherwise.
   */
  boolean resetPassword(PasswordResetDTO passwordReset);

  /**
   * Transfers the talent points for an accepted request from the user requesting the service to the
   * user providing it.
   *
   * @param request The request (must be accepted).
   */
  void transferTalentPoints(RequestDTO request);

  /** Sets the admin-verified status of all users to {@code true}. */
  void verifyAllUsers();

  /**
   * Fetches the user identified by the password reset verification token given in {@code
   * passwordReset}.
   *
   * @param user The {@link UserAuthenticationDTO} to fill.
   * @param passwordReset The password reset object containing the verification token.
   * @return The user.
   */
  UserAuthenticationDTO getUserByPasswordResetSecret(
      UserAuthenticationDTO user, PasswordResetDTO passwordReset);
}
