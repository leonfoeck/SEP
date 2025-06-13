package de.uni_passau.fim.talent_tauscher.model.persistence.dataaccess.interfaces;

import de.uni_passau.fim.talent_tauscher.dto.SystemDataDTO;

/** Interface for data store accesses concerning the system data. */
public interface SystemDataDAO {

  /**
   * Fetches the system configuration data from the data store.
   *
   * @return The system configuration data.
   */
  SystemDataDTO getSystemData();

  /**
   * Updates the system configuration data.
   *
   * @param systemData The new system configuration.
   */
  void updateSystemData(SystemDataDTO systemData);
}
