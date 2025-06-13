package de.uni_passau.fim.talent_tauscher.model.backing.beans;

import de.uni_passau.fim.talent_tauscher.dto.SystemDataDTO;
import de.uni_passau.fim.talent_tauscher.dto.UserDTO;
import de.uni_passau.fim.talent_tauscher.model.backing.util.JFUtils;
import de.uni_passau.fim.talent_tauscher.model.backing.util.PaginatedDataModel;
import de.uni_passau.fim.talent_tauscher.model.business.services.SystemDataService;
import de.uni_passau.fim.talent_tauscher.model.business.services.UserService;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serial;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/** Backing bean for the user administration page. */
@Named
@ViewScoped
public class UserAdministrationBean implements Serializable {

  @Serial private static final long serialVersionUID = 1L;

  private SystemDataDTO systemData;
  @Inject private UserService userService;

  @Inject private SystemDataService systemDataService;

  @Inject private JFUtils jfUtils;

  private PaginatedDataModel<UserDTO> dataModel;

  /** Default constructor. */
  public UserAdministrationBean() {
    // Default constructor
  }

  /** Initializes the data model for the user table. */
  @PostConstruct
  public void init() {
    systemData = systemDataService.get();
    dataModel =
        new PaginatedDataModel<UserDTO>(systemData.getSumPaginatedItems(), "has_admin_verified") {
          @Override
          public Collection<UserDTO> fetchData() {
            Map<String, String> filterMap =
                this.getPagination()
                    .getFilters(); // replace visual text with the corresponding boolean
            boolean containsAdminVerified = filterMap.containsKey("has_admin_verified");
            boolean containsEmailVerified = filterMap.containsKey("is_email_verified");
            String adminSearchString = "";
            String emailSearchString = "";

            if (containsAdminVerified && !filterMap.get("has_admin_verified").isEmpty()) {
              adminSearchString = filterMap.get("has_admin_verified");
              convertVerifiedSearch(filterMap, "has_admin_verified");
            }
            if (containsEmailVerified && !filterMap.get("is_email_verified").isEmpty()) {
              emailSearchString = filterMap.get("is_email_verified");
              convertVerifiedSearch(filterMap, "is_email_verified");
            }
            List<UserDTO> users = userService.getUsers(this.getPagination());

            if (containsAdminVerified) {
              filterMap.put("has_admin_verified", adminSearchString);
            }
            if (containsEmailVerified) {
              filterMap.put("is_email_verified", emailSearchString);
            }
            return users;
          }

          @Override
          public int fetchCount() {
            return userService.getUserCount(this.getPagination());
          }
        };
  }

  /**
   * Returns the data model for the user table.
   *
   * @return The data model.
   */
  public PaginatedDataModel<UserDTO> getDataModel() {
    return dataModel;
  }

  /**
   * Gets the system configuration data.
   *
   * @return The system configuration data.
   */
  public SystemDataDTO getSystemData() {
    return systemData;
  }

  // A massive overcomplification to match the default behavior with the resource bundle terms
  private void convertVerifiedSearch(Map<String, String> filterMap, String verificationHader) {
    String searchQuery = filterMap.get(verificationHader).toLowerCase();
    if (searchQuery.endsWith("%")) {
      searchQuery = searchQuery.substring(0, searchQuery.length() - 1);
      if (jfUtils
          .getMessageFromResourceBundle("f_verified")
          .toLowerCase()
          .startsWith(searchQuery)) {
        filterMap.put(verificationHader, "true");
      } else if (jfUtils
          .getMessageFromResourceBundle("f_not_verified")
          .toLowerCase()
          .startsWith(searchQuery)) {
        filterMap.put(verificationHader, "false");
      }
    } else if (jfUtils
        .getMessageFromResourceBundle("f_verified")
        .toLowerCase()
        .equals(searchQuery)) {
      filterMap.put(verificationHader, "true");
    } else if (jfUtils
        .getMessageFromResourceBundle("f_not_verified")
        .toLowerCase()
        .equals(searchQuery)) {
      filterMap.put(verificationHader, "false");
    }
  }
}
