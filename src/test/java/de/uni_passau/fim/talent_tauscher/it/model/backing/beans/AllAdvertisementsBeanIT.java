package de.uni_passau.fim.talent_tauscher.it.model.backing.beans;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.uni_passau.fim.talent_tauscher.dto.AdvertisementDTO;
import de.uni_passau.fim.talent_tauscher.dto.PaginationDTO;
import de.uni_passau.fim.talent_tauscher.dto.SystemDataDTO;
import de.uni_passau.fim.talent_tauscher.model.backing.beans.AllAdvertisementsBean;
import de.uni_passau.fim.talent_tauscher.model.backing.util.PaginatedDataModel;
import de.uni_passau.fim.talent_tauscher.model.business.services.AdvertisementService;
import de.uni_passau.fim.talent_tauscher.model.business.services.SystemDataService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** Unit tests for {@link AllAdvertisementsBean} class. */
@SuppressWarnings({"checkstyle:MagicNumber", "checkstyle:VisibilityModifier"})
@ExtendWith(MockitoExtension.class)
class AllAdvertisementsBeanIT {

  @Mock private AdvertisementService advertisementService;
  @Mock private SystemDataService systemDataService;
  @InjectMocks private AllAdvertisementsBean allAdvertisementsBean;
  private PaginatedDataModel<AdvertisementDTO> dataModel;

  /**
   * Initializes the {@link AllAdvertisementsBean} and retrieves the data model before each test.
   */
  @BeforeEach
  void init() {
    // Mock SystemDataDTO and its return from systemDataService.get()
    SystemDataDTO systemDataDTO = new SystemDataDTO();
    systemDataDTO.setSumPaginatedItems(5); // or whatever value is appropriate for the test
    when(systemDataService.get()).thenReturn(systemDataDTO);

    allAdvertisementsBean.init();
    dataModel = allAdvertisementsBean.getDataModel();
  }

  /**
   * Tests the data fetching mechanism to ensure the service retrieves the correct advertisements.
   */
  @Test
  void testFetchData() {
    AdvertisementDTO ad1 = new AdvertisementDTO();
    ad1.setTitle("Ad 1");
    AdvertisementDTO ad2 = new AdvertisementDTO();
    ad2.setTitle("Ad 2");
    List<AdvertisementDTO> ads = List.of(ad1, ad2);

    when(advertisementService.getAdvertisements(any(PaginationDTO.class))).thenReturn(ads);

    List<AdvertisementDTO> fetchedAds = (List<AdvertisementDTO>) dataModel.fetchData();

    assertEquals(ads, fetchedAds);
    verify(advertisementService, times(2)).getAdvertisements(any(PaginationDTO.class));
  }

  /** Tests the mechanism to fetch the total number of advertisements. */
  @Test
  void testFetchCount() {
    when(advertisementService.getAdvertisementCount(any(PaginationDTO.class))).thenReturn(2);

    int count = dataModel.fetchCount();

    assertEquals(2, count);
    verify(advertisementService, times(2)).getAdvertisementCount(any(PaginationDTO.class));
  }

  /** Tests the functionality of moving to the next page. */
  @Test
  void testNextPage() {
    lenient()
        .when(advertisementService.getAdvertisementCount(any(PaginationDTO.class)))
        .thenReturn(10);
    lenient()
        .when(advertisementService.getAdvertisements(any(PaginationDTO.class)))
        .thenReturn(List.of(new AdvertisementDTO(), new AdvertisementDTO()));

    dataModel.nextPage();

    assertEquals(1, dataModel.getPagination().getPage());
    verify(advertisementService, times(1)).getAdvertisements(any(PaginationDTO.class));
    verify(advertisementService, times(1)).getAdvertisementCount(any(PaginationDTO.class));
  }

  /** Tests the functionality of moving to the previous page. */
  @Test
  void testPreviousPage() {
    lenient()
        .when(advertisementService.getAdvertisementCount(any(PaginationDTO.class)))
        .thenReturn(10);
    lenient()
        .when(advertisementService.getAdvertisements(any(PaginationDTO.class)))
        .thenReturn(List.of(new AdvertisementDTO(), new AdvertisementDTO()));

    dataModel.nextPage(); // move to page 2
    dataModel.previousPage(); // move back to page 1

    assertEquals(1, dataModel.getPagination().getPage());
    verify(advertisementService, times(1)).getAdvertisements(any(PaginationDTO.class));
    verify(advertisementService, times(1)).getAdvertisementCount(any(PaginationDTO.class));
  }

  /** Tests the sorting functionality to ensure advertisements are sorted correctly. */
  @Test
  void testSetSorting() {
    when(advertisementService.getAdvertisements(any(PaginationDTO.class)))
        .thenReturn(List.of(new AdvertisementDTO(), new AdvertisementDTO()));
    when(advertisementService.getAdvertisementCount(any(PaginationDTO.class))).thenReturn(10);

    dataModel.setSorting("title");

    assertEquals("title", dataModel.getPagination().getSortBy());
    assertTrue(dataModel.getPagination().isAscending());
    verify(advertisementService, times(2)).getAdvertisements(any(PaginationDTO.class));
    verify(advertisementService, times(2)).getAdvertisementCount(any(PaginationDTO.class));

    dataModel.setSorting("title"); // change sort order to descending

    assertEquals("title", dataModel.getPagination().getSortBy());
    assertFalse(dataModel.getPagination().isAscending());
    verify(advertisementService, times(3)).getAdvertisements(any(PaginationDTO.class));

    dataModel.setSorting("title"); // reset sorting

    assertEquals(
        dataModel.getPagination().getDefaultSortBy(), dataModel.getPagination().getSortBy());
    verify(advertisementService, times(4)).getAdvertisements(any(PaginationDTO.class));
  }

  /** Tests the functionality to check if there is a next page available. */
  @Test
  void testHasNextPage() {
    when(advertisementService.getAdvertisementCount(any(PaginationDTO.class))).thenReturn(10);
    dataModel.refresh();

    assertTrue(dataModel.hasNextPage());
  }

  /** Tests the refresh functionality to ensure data and count are updated correctly. */
  @Test
  void testRefresh() {
    when(advertisementService.getAdvertisementCount(any(PaginationDTO.class))).thenReturn(2);
    when(advertisementService.getAdvertisements(any(PaginationDTO.class)))
        .thenReturn(List.of(new AdvertisementDTO(), new AdvertisementDTO()));

    dataModel.refresh();

    assertEquals(1, dataModel.getPagination().getTotalPages());
    verify(advertisementService, times(2)).getAdvertisementCount(any(PaginationDTO.class));
    verify(advertisementService, times(2)).getAdvertisements(any(PaginationDTO.class));
  }

  /** Tests the functionality to validate the current page number. */
  @Test
  void testValidatePageNumber() {
    when(advertisementService.getAdvertisementCount(any(PaginationDTO.class))).thenReturn(10);
    dataModel.refresh();

    assertTrue(dataModel.validatePageNumber());

    dataModel.getPagination().setPage(11);
    assertFalse(dataModel.validatePageNumber());
  }
}
