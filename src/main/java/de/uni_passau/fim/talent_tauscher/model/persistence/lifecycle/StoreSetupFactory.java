package de.uni_passau.fim.talent_tauscher.model.persistence.lifecycle;

import de.uni_passau.fim.talent_tauscher.model.persistence.util.ResourceReader;

/**
 * Factory class for creating instances of the {@link StoreSetup} interface.
 *
 * @author Leon Föckersperger
 */
public final class StoreSetupFactory {

  private StoreSetupFactory() {
    // utility class
  }

  /**
   * Creates an instance of the {@link StoreSetup} interface.
   *
   * @param reader The reader for the setup files.
   * @return An instance of the {@link StoreSetup} interface.
   */
  public static StoreSetup createStoreSetup(ResourceReader reader) {
    return new PostgreSQLSetup(reader);
  }
}
