package de.uni_passau.fim.talent_tauscher.model.persistence.lifecycle;

/**
 * Interface for setting up the datastore of the application.
 *
 * @author Leon Föckersperger
 */
public interface StoreSetup {

  /**
   * Checks if the data store schema already exists.
   *
   * @return {@code true} if and only if the schema already exists.
   */
  boolean schemaExists();

  /** Initializes the data store schema of the application. */
  void createSchema();

  /** Seeds the data store with the initial data required to run the application. */
  void initializeData();
}
