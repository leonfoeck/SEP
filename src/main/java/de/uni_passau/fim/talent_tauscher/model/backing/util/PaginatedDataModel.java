package de.uni_passau.fim.talent_tauscher.model.backing.util;

import de.uni_passau.fim.talent_tauscher.dto.PaginationDTO;
import jakarta.faces.model.IterableDataModel;
import java.io.Serial;
import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Wraps a collection of entities to be displayed in a paginated datatable and handles filtering,
 * sorting, and pagination. Implements the {@link IterableDataModel} interface, so that it can be
 * used as a value parameter of the {@code p:dataTable} component.
 *
 * @param <T> The type of the entities displayed.
 */
public abstract class PaginatedDataModel<T> extends IterableDataModel<T> implements Serializable {

  @Serial private static final long serialVersionUID = 1L;
  private final PaginationDTO pagination;
  private int totalCount;
  private Map<String, String> lastFilters;

  /**
   * Constructs a new {@link PaginatedDataModel} with the specified number of items per page and a
   * default sort order.
   *
   * @param initialItemsPerPage The number of items per page.
   * @param defaultSortBy The default property to sort by.
   */
  protected PaginatedDataModel(int initialItemsPerPage, String defaultSortBy) {
    this.pagination = new PaginationDTO(initialItemsPerPage, defaultSortBy);
    refresh();
  }

  /**
   * Fetches the data to be displayed on the current page.
   *
   * @return The fetched data for the current page.
   */
  public abstract Collection<T> fetchData();

  /**
   * Fetches the total number of entities that match the current filters.
   *
   * @return The total number of entities.
   */
  public abstract int fetchCount();

  /** Updates the total number of pages based on the total count and items per page. */
  private void updateTotalPages() {
    int itemsPerPage = pagination.getItemsPerPage();
    pagination.setTotalPages((int) Math.ceil((double) totalCount / itemsPerPage));
  }

  /** Refreshes the data model by re-fetching the total count and the current page data. */
  public void refresh() {
    if (filtersChanged()) {
      pagination.setPage(1);
    }
    totalCount = fetchCount(); // Re-fetch the total count with any updated filters
    updateTotalPages();
    setWrappedData(fetchData());
    lastFilters = cleanFilters(pagination.getFilters());
  }

  /** Moves to the next page if there is a next page available. */
  public void nextPage() {
    if (hasNextPage()) {
      pagination.setPage(pagination.getPage() + 1);
      refresh();
    }
  }

  /** Moves to the previous page if there is a previous page available. */
  public void previousPage() {
    if (hasPreviousPage()) {
      pagination.setPage(pagination.getPage() - 1);
      refresh();
    }
  }

  /**
   * Checks if there is a next page available.
   *
   * @return {@code true} if there is a next page, {@code false} otherwise.
   */
  public boolean hasNextPage() {
    return pagination.getPage() < pagination.getTotalPages();
  }

  /**
   * Checks if there is a previous page available.
   *
   * @return {@code true} if there is a previous page, {@code false} otherwise.
   */
  public boolean hasPreviousPage() {
    return pagination.getPage() > 1;
  }

  /**
   * Sets the sorting property and direction. Toggles the sorting direction if the same property is
   * set again.
   *
   * @param propertyName The property to sort by.
   */
  public void setSorting(String propertyName) {
    if (propertyName.equals(pagination.getSortBy())) {
      if (pagination.isAscending()) {
        pagination.setSortBy(propertyName);
        pagination.setAscending(false);
      } else {
        pagination.setSortBy(pagination.getDefaultSortBy());
      }
    } else {
      pagination.setSortBy(propertyName);
      pagination.setAscending(true);
    }
    refresh();
  }

  /**
   * Gets the pagination details including current page, items per page, and sorting information.
   *
   * @return The pagination details.
   */
  public PaginationDTO getPagination() {
    return pagination;
  }

  /**
   * Validates if the current page number is within the valid range.
   *
   * @return {@code true} if the page number is valid, {@code false} otherwise.
   */
  public boolean validatePageNumber() {
    return pagination.getPage() > 0 && pagination.getPage() < pagination.getTotalPages();
  }

  /**
   * Checks whether the filters applied to the data have changed since the last refresh.
   *
   * @return {@code true} if the filters have changed, {@code false} otherwise.
   */
  private boolean filtersChanged() {
    Map<String, String> filters = cleanFilters(pagination.getFilters());
    return !filters.equals(lastFilters);
  }

  /**
   * Removes empty and null values from the filter map.
   *
   * @param filters The filter map to clean up.
   * @return The cleaned-up filters.
   */
  private Map<String, String> cleanFilters(Map<String, String> filters) {
    return filters.entrySet().stream()
        .filter(entry -> entry.getValue() != null && !entry.getValue().trim().isEmpty())
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
  }
}
