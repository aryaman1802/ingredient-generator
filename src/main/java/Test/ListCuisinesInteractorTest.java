package Test;

import use_case.cuisines.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import use_case.gateway.MealDbGateway;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ListCuisinesInteractor Tests")
class ListCuisinesInteractorTest {

    @Mock
    private MealDbGateway mockGateway;

    @Mock
    private ListCuisinesOutputBoundary mockPresenter;

    private ListCuisinesInteractor interactor;

    @BeforeEach
    void setUp() {
        interactor = new ListCuisinesInteractor(mockGateway, mockPresenter);
    }

    @Test
    @DisplayName("test load success")
    void testLoadSuccess() throws Exception {
        // Arrange
        List<String> areas = Arrays.asList("Italian", "Chinese", "Mexican", "Indian");
        when(mockGateway.listAreas()).thenReturn(areas);

        // Act
        interactor.load();

        // Assert
        ArgumentCaptor<ListCuisinesResponseModel> captor = 
            ArgumentCaptor.forClass(ListCuisinesResponseModel.class);
        verify(mockPresenter, times(1)).present(captor.capture());
        verify(mockPresenter, never()).fail(anyString());
        
        ListCuisinesResponseModel response = captor.getValue();
        assertEquals(areas, response.getAreas());
        
        verify(mockGateway, times(1)).listAreas();
    }

    @Test
    @DisplayName("test load with null response")
    void testLoadWithNullResponse() throws Exception {
        // Arrange
        when(mockGateway.listAreas()).thenReturn(null);

        // Act
        interactor.load();

        // Assert
        ArgumentCaptor<ListCuisinesResponseModel> captor = 
            ArgumentCaptor.forClass(ListCuisinesResponseModel.class);
        verify(mockPresenter, times(1)).present(captor.capture());
        
        ListCuisinesResponseModel response = captor.getValue();
        assertNotNull(response.getAreas());
        assertFalse(response.getAreas().isEmpty());
        assertTrue(response.getAreas().contains("Italian"));
        assertTrue(response.getAreas().contains("Chinese"));
        assertEquals(26, response.getAreas().size()); // 检查备用列表大小
        
        verify(mockGateway, times(1)).listAreas();
    }

    @Test
    @DisplayName("test load with empty response")
    void testLoadWithEmptyResponse() throws Exception {
        // Arrange
        when(mockGateway.listAreas()).thenReturn(new ArrayList<>());

        // Act
        interactor.load();

        // Assert
        ArgumentCaptor<ListCuisinesResponseModel> captor = 
            ArgumentCaptor.forClass(ListCuisinesResponseModel.class);
        verify(mockPresenter, times(1)).present(captor.capture());
        
        ListCuisinesResponseModel response = captor.getValue();
        assertNotNull(response.getAreas());
        assertFalse(response.getAreas().isEmpty());
        assertEquals(26, response.getAreas().size());

        List<String> expectedAreas = Arrays.asList(
            "Any","American","British","Canadian","Chinese","Dutch","Egyptian","French","Greek",
            "Indian","Irish","Italian","Jamaican","Japanese","Kenyan","Malaysian","Mexican","Moroccan",
            "Polish","Portuguese","Russian","Spanish","Thai","Tunisian","Turkish","Vietnamese"
        );
        assertTrue(response.getAreas().containsAll(expectedAreas));
        
        verify(mockGateway, times(1)).listAreas();
    }

    @Test
    @DisplayName("test load with exception")
    void testLoadWithException() throws Exception {
        // Arrange
        when(mockGateway.listAreas()).thenThrow(new RuntimeException("Network error"));

        // Act
        interactor.load();

        // Assert
        ArgumentCaptor<ListCuisinesResponseModel> captor = 
            ArgumentCaptor.forClass(ListCuisinesResponseModel.class);
        verify(mockPresenter, times(1)).present(captor.capture());
        verify(mockPresenter, never()).fail(anyString());
        
        ListCuisinesResponseModel response = captor.getValue();
        assertNotNull(response.getAreas());
        assertEquals(26, response.getAreas().size());
        
        verify(mockGateway, times(1)).listAreas();
    }

    @Test
    @DisplayName("test load with various exceptions")
    void testLoadWithVariousExceptions() throws Exception {
        // Test with NullPointerException
        when(mockGateway.listAreas()).thenThrow(new NullPointerException("Null error"));
        interactor.load();
        verify(mockPresenter, times(1)).present(any(ListCuisinesResponseModel.class));
        
        // Reset mocks
        reset(mockPresenter);
        
        // Test with IllegalStateException
        when(mockGateway.listAreas()).thenThrow(new IllegalStateException("State error"));
        interactor.load();
        verify(mockPresenter, times(1)).present(any(ListCuisinesResponseModel.class));
        
        // Reset mocks
        reset(mockPresenter);
        
        // Test with RuntimeException (wrapped Exception)
        when(mockGateway.listAreas()).thenThrow(new RuntimeException("Generic error"));
        interactor.load();
        verify(mockPresenter, times(1)).present(any(ListCuisinesResponseModel.class));
    }

    @Test
    @DisplayName("test fall back list content")
    void testFallbackListContent() throws Exception {
        // Arrange
        when(mockGateway.listAreas()).thenReturn(null);

        // Act
        interactor.load();

        // Assert
        ArgumentCaptor<ListCuisinesResponseModel> captor = 
            ArgumentCaptor.forClass(ListCuisinesResponseModel.class);
        verify(mockPresenter).present(captor.capture());
        
        List<String> areas = captor.getValue().getAreas();
        
        // 验证备用列表的具体内容
        assertEquals("Any", areas.get(0));
        assertTrue(areas.contains("American"));
        assertTrue(areas.contains("British"));
        assertTrue(areas.contains("Canadian"));
        assertTrue(areas.contains("Chinese"));
        assertTrue(areas.contains("Dutch"));
        assertTrue(areas.contains("Egyptian"));
        assertTrue(areas.contains("French"));
        assertTrue(areas.contains("Greek"));
        assertTrue(areas.contains("Indian"));
        assertTrue(areas.contains("Irish"));
        assertTrue(areas.contains("Italian"));
        assertTrue(areas.contains("Jamaican"));
        assertTrue(areas.contains("Japanese"));
        assertTrue(areas.contains("Kenyan"));
        assertTrue(areas.contains("Malaysian"));
        assertTrue(areas.contains("Mexican"));
        assertTrue(areas.contains("Moroccan"));
        assertTrue(areas.contains("Polish"));
        assertTrue(areas.contains("Portuguese"));
        assertTrue(areas.contains("Russian"));
        assertTrue(areas.contains("Spanish"));
        assertTrue(areas.contains("Thai"));
        assertTrue(areas.contains("Tunisian"));
        assertTrue(areas.contains("Turkish"));
        assertTrue(areas.contains("Vietnamese"));
    }

    @Test
    @DisplayName("test never calls fail")
    void testNeverCallsFail() throws Exception {
        // Test with successful response
        when(mockGateway.listAreas()).thenReturn(Arrays.asList("Italian", "Chinese"));
        interactor.load();
        verify(mockPresenter, never()).fail(anyString());
        
        // Reset
        reset(mockPresenter);
        
        // Test with null response
        when(mockGateway.listAreas()).thenReturn(null);
        interactor.load();
        verify(mockPresenter, never()).fail(anyString());
        
        // Reset
        reset(mockPresenter);
        
        // Test with exception
        when(mockGateway.listAreas()).thenThrow(new RuntimeException());
        interactor.load();
        verify(mockPresenter, never()).fail(anyString());
    }

    @Test
    @DisplayName("test single area")
    void testSingleArea() throws Exception {
        // Arrange
        List<String> singleArea = Collections.singletonList("Italian");
        when(mockGateway.listAreas()).thenReturn(singleArea);

        // Act
        interactor.load();

        // Assert
        ArgumentCaptor<ListCuisinesResponseModel> captor = 
            ArgumentCaptor.forClass(ListCuisinesResponseModel.class);
        verify(mockPresenter).present(captor.capture());
        
        ListCuisinesResponseModel response = captor.getValue();
        assertEquals(1, response.getAreas().size());
        assertEquals("Italian", response.getAreas().get(0));
    }

    @Test
    @DisplayName("test large number of areas")
    void testLargeNumberOfAreas() throws Exception {
        // Arrange
        List<String> manyAreas = new ArrayList<>();
        for (int i = 1; i <= 100; i++) {
            manyAreas.add("Area" + i);
        }
        when(mockGateway.listAreas()).thenReturn(manyAreas);

        // Act
        interactor.load();

        // Assert
        ArgumentCaptor<ListCuisinesResponseModel> captor = 
            ArgumentCaptor.forClass(ListCuisinesResponseModel.class);
        verify(mockPresenter).present(captor.capture());
        
        ListCuisinesResponseModel response = captor.getValue();
        assertEquals(100, response.getAreas().size());
        assertEquals("Area1", response.getAreas().get(0));
        assertEquals("Area100", response.getAreas().get(99));
    }

    @Test
    @DisplayName("test multiple load calls")
    void testMultipleLoadCalls() throws Exception {
        // Arrange
        List<String> areas = Arrays.asList("Italian", "Chinese");
        when(mockGateway.listAreas()).thenReturn(areas);

        // Act
        interactor.load();
        interactor.load();
        interactor.load();

        // Assert
        verify(mockPresenter, times(3)).present(any(ListCuisinesResponseModel.class));
        verify(mockGateway, times(3)).listAreas();
    }

    @Test
    @DisplayName("test constructor parameters")
    void testConstructorParameters() {

        ListCuisinesInteractor validInteractor = 
            new ListCuisinesInteractor(mockGateway, mockPresenter);
        assertNotNull(validInteractor);
        

        ListCuisinesInteractor nullGatewayInteractor = 
            new ListCuisinesInteractor(null, mockPresenter);
        assertNotNull(nullGatewayInteractor);
        
        ListCuisinesInteractor nullPresenterInteractor = 
            new ListCuisinesInteractor(mockGateway, null);
        assertNotNull(nullPresenterInteractor);
    }

    @Test
    @DisplayName("test fall back list immutability")
    void testFallbackListImmutability() throws Exception {
        // Arrange
        when(mockGateway.listAreas()).thenReturn(null);

        // Act
        interactor.load();

        // Assert
        ArgumentCaptor<ListCuisinesResponseModel> captor1 = 
            ArgumentCaptor.forClass(ListCuisinesResponseModel.class);
        verify(mockPresenter).present(captor1.capture());
        List<String> firstList = captor1.getValue().getAreas();

        reset(mockPresenter);
        interactor.load();
        
        // Assert
        ArgumentCaptor<ListCuisinesResponseModel> captor2 = 
            ArgumentCaptor.forClass(ListCuisinesResponseModel.class);
        verify(mockPresenter).present(captor2.capture());
        List<String> secondList = captor2.getValue().getAreas();
        

        assertEquals(firstList, secondList);

        if (!firstList.isEmpty()) {
            String original = firstList.get(0);
            firstList.set(0, "Modified");
            assertNotEquals(firstList.get(0), secondList.get(0));
            assertEquals(original, secondList.get(0));
        }
    }
}