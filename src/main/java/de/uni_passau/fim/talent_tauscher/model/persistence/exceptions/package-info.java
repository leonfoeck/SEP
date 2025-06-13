/**
 * This package contains custom exception classes for the Talent Tauscher application. These
 * exceptions are used to handle various error conditions that may occur during the application's
 * operation.
 *
 * <p>The classes included in this package are:
 *
 * <ul>
 *   <li><b>ConfigUnreadableException:</b> Thrown when the application configuration cannot be read.
 *   <li><b>EntityNotFoundException:</b> Thrown when a requested entity cannot be found.
 *   <li><b>StoreAccessException:</b> Thrown when there is an access issue with the data store.
 *   <li><b>StoreConnectionException:</b> Thrown when there is a connection issue with the data
 *       store.
 *   <li><b>StoreDeleteException:</b> Thrown when there is an error deleting an entity from the data
 *       store.
 *   <li><b>StoreOperation:</b> Base class for exceptions related to data store operations.
 *   <li><b>StoreReadException:</b> Thrown when there is an error reading from the data store.
 *   <li><b>StoreUpdateException:</b> Thrown when there is an error updating an entity in the data
 *       store.
 *   <li><b>StoreValidationException:</b> Thrown when data validation fails during store operations.
 *   <li><b>TransactionException:</b> Thrown when there is an issue with a transaction.
 * </ul>
 */
package de.uni_passau.fim.talent_tauscher.model.persistence.exceptions;
