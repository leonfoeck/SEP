package de.uni_passau.fim.talent_tauscher.dto;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/** Represents the filters, sorting, and pagination applied to a data collection. */
public class PaginationDTO implements Serializable {
  @Serial private static final long serialVersionUID = 1L;
  private int itemsPerPage;
  private int page;
  private final Map<String, String> filters;
  private String sortBy;
  private boolean isAscending;
  private int totalPages;
  private String defaultSortBy;

  /** Constructs a new {@link PaginationDTO} with initially unlimited items per page. */
  public PaginationDTO() {
    itemsPerPage = Integer.MAX_VALUE;
    page = 1;
    filters = new HashMap<>();
    sortBy = null;
    this.defaultSortBy = null;
    isAscending = true;
    this.totalPages = 1;
  }

  /**
   * Constructs a new {@link PaginationDTO} with the given initial number of items per page.
   *
   * @param itemsPerPage The initial number of items per page.
   * @param defaultSortBy The default sort field.
   */
  public PaginationDTO(int itemsPerPage, String defaultSortBy) {
    this();
    this.itemsPerPage = itemsPerPage;
    this.defaultSortBy = defaultSortBy;
  }

  /**
   * Gets the number of items per page.
   *
   * @return The number of items per page.
   */
  public int getItemsPerPage() {
    return itemsPerPage;
  }

  /**
   * Sets the number of items per page.
   *
   * @param itemsPerPage The number of items per page.
   */
  public void setItemsPerPage(int itemsPerPage) {
    this.itemsPerPage = itemsPerPage;
  }

  /**
   * Returns the page number.
   *
   * @return The page number.
   */
  public int getPage() {
    return page;
  }

  /**
   * Sets the page number.
   *
   * @param page The page number.
   */
  public void setPage(int page) {
    this.page = page;
  }

  /**
   * Returns all filters.
   *
   * @return All filters.
   */
  public Map<String, String> getFilters() {
    return filters;
  }

  /**
   * Returns the name of the property by which the data is sorted.
   *
   * @return The name of the property by which the data is sorted.
   */
  public String getSortBy() {
    return sortBy;
  }

  /**
   * Sets the sort field.
   *
   * @param sortBy The sort field.
   */
  public void setSortBy(String sortBy) {
    this.sortBy = sortBy;
  }

  /**
   * Gets the total number of pages.
   *
   * @return The total number of pages.
   */
  public int getTotalPages() {
    return totalPages;
  }

  /**
   * Sets the total number of pages.
   *
   * @param totalPages The total number of pages.
   */
  public void setTotalPages(int totalPages) {
    this.totalPages = Math.max(totalPages, 1); // Ensure total pages is at least 1
  }

  /**
   * Checks if the data is sorted in ascending order.
   *
   * @return True if the data is sorted in ascending order, false otherwise.
   */
  public boolean isAscending() {
    return isAscending;
  }

  /**
   * Sets the sort order.
   *
   * @param ascending True to sort in ascending order, false otherwise.
   */
  public void setAscending(boolean ascending) {
    isAscending = ascending;
  }

  /**
   * Gets the default sort field.
   *
   * @return The default sort field.
   */
  public String getDefaultSortBy() {
    return defaultSortBy;
  }

  /**
   * Sets the default sort field.
   *
   * @param defaultSortBy The column to sort by.
   */
  public void setDefaultSortBy(String defaultSortBy) {
    this.defaultSortBy = defaultSortBy;
  }
}
