package de.uni_passau.fim.talent_tauscher.model.business.services;

import de.uni_passau.fim.talent_tauscher.dto.SystemDataDTO;
import de.uni_passau.fim.talent_tauscher.model.persistence.dataaccess.TransactionFactory;
import de.uni_passau.fim.talent_tauscher.model.persistence.dataaccess.interfaces.SystemDataDAO;
import de.uni_passau.fim.talent_tauscher.model.persistence.dataaccess.interfaces.Transaction;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import java.io.File;
import java.io.Serial;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Manages interactions with {@link SystemDataDAO}. */
@ApplicationScoped
public class SystemDataService implements Serializable {

  @Serial private static final long serialVersionUID = 1L;

  @Inject private transient Logger logger;

  /**
   * Updates the system configuration.
   *
   * @param data The new system configuration data.
   * @author Leon Föckersperger
   */
  public void update(SystemDataDTO data) {
    try (Transaction transaction = TransactionFactory.createTransaction()) {
      SystemDataDAO systemDataDAO = transaction.getDAOFactory().createSystemDataDAO();
      systemDataDAO.updateSystemData(data);
      transaction.commit();
    }
  }

  /**
   * Retrieves the system configuration data.
   *
   * @return The system configuration data.
   * @author Leon Föckersperger
   */
  public SystemDataDTO get() {
    try (Transaction transaction = TransactionFactory.createTransaction()) {
      SystemDataDAO systemDataDAO = transaction.getDAOFactory().createSystemDataDAO();
      SystemDataDTO result = systemDataDAO.getSystemData();
      transaction.commit();
      return result;
    }
  }

  /**
   * Gets the name of all .css files in the resources/css folder.
   *
   * @return The name of all .css files in the resources/css folder.
   * @author Sturm
   */
  public List<String> getStylesheetNames() {
    List<String> fileList = new ArrayList<>();
    try {
      URL url =
          FacesContext.getCurrentInstance().getExternalContext().getResource("/resources/css");
      File cssFolder = new File(url.toURI());
      for (final File file : Objects.requireNonNull(cssFolder.listFiles())) {
        if (file.isFile() && file.getName().endsWith(".css")) {
          fileList.add(file.getName());
        }
      }
      return fileList;
    } catch (URISyntaxException | MalformedURLException e) {
      logger.log(Level.SEVERE, "error loading css folder");
      return fileList;
    }
  }
}
