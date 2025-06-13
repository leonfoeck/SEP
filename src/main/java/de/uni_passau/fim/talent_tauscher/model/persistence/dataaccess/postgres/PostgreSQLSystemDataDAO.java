package de.uni_passau.fim.talent_tauscher.model.persistence.dataaccess.postgres;

import de.uni_passau.fim.talent_tauscher.dto.SystemDataDTO;
import de.uni_passau.fim.talent_tauscher.logging.LoggerProducer;
import de.uni_passau.fim.talent_tauscher.model.persistence.dataaccess.interfaces.SystemDataDAO;
import de.uni_passau.fim.talent_tauscher.model.persistence.exceptions.StoreReadException;
import de.uni_passau.fim.talent_tauscher.model.persistence.exceptions.StoreUpdateException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/** PostgreSQL's implementation of the {@link SystemDataDAO} interface. */
public class PostgreSQLSystemDataDAO extends PostgreSQLDAO implements SystemDataDAO {

  private static final Logger LOGGER = LoggerProducer.get(PostgreSQLSystemDataDAO.class);

  /**
   * Constructs a new {@link PostgreSQLSystemDataDAO} using the given database connection.
   *
   * @param connection The connection.
   */
  PostgreSQLSystemDataDAO(Connection connection) {
    super(connection);
  }

  /** {@inheritDoc} */
  @Override
  public SystemDataDTO getSystemData() {
    String query = "SELECT * FROM system_settings LIMIT 1;";
    try (Statement stmt = getConnection().createStatement();
        ResultSet rs = stmt.executeQuery(query)) {
      if (rs.next()) {
        SystemDataDTO dto = new SystemDataDTO();
        dto.setContact(rs.getString("contact_info"));
        dto.setImprint(rs.getString("imprint"));
        dto.setPrivacyPolicy(rs.getString("data_protection"));
        dto.setAdminConfirmationRequiredForRegistration(
            rs.getBoolean("is_admin_confirmation_needed_registration"));
        dto.setMaxImageSize(rs.getLong("max_pic_size"));
        dto.setLogo(rs.getBytes("logo"));
        dto.setCssStylesheetName(rs.getString("css_name"));
        dto.setSumPaginatedItems(rs.getInt("sum_paginated_items"));
        return dto;
      }
    } catch (SQLException e) {
      LOGGER.log(Level.SEVERE, "Error while fetching system data", e);
      throw new StoreReadException("Error while fetching system data.", e);
    }
    return null;
  }

  /** {@inheritDoc} */
  @SuppressWarnings("checkstyle:MagicNumber")
  @Override
  public void updateSystemData(SystemDataDTO systemData) {
    String query =
        "UPDATE system_settings SET contact_info=?, imprint=?, data_protection=?, "
            + "is_admin_confirmation_needed_registration=?, max_pic_size=?, logo=?, sum_paginated_items=?, css_name=? WHERE id = 1;";
    try (PreparedStatement pstmt = getConnection().prepareStatement(query)) {
      pstmt.setString(1, systemData.getContact());
      pstmt.setString(2, systemData.getImprint());
      pstmt.setString(3, systemData.getPrivacyPolicy());
      pstmt.setBoolean(4, systemData.isAdminConfirmationRequiredForRegistration());
      pstmt.setLong(5, systemData.getMaxImageSize());
      pstmt.setBytes(6, systemData.getLogo());
      pstmt.setInt(7, systemData.getSumPaginatedItems());
      pstmt.setString(8, systemData.getCssStylesheetName());
      pstmt.executeUpdate();

    } catch (SQLException e) {
      LOGGER.log(Level.SEVERE, "Error while updating system data", e);
      throw new StoreUpdateException("Error while updating system data.", e);
    }
  }
}
