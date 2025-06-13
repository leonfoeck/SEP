package de.uni_passau.fim.talent_tauscher.model.persistence.exceptions;

/** Represents different types of data store operations. */
public enum StoreOperation {
  /** A create operation. */
  CREATE,

  /** A read operation. */
  READ,

  /** An update operation. */
  UPDATE,

  /** A delete operation. */
  DELETE
}
