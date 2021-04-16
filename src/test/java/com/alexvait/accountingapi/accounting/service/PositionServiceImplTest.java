package com.alexvait.accountingapi.accounting.service;

import com.alexvait.accountingapi.accounting.entity.InvoiceEntity;
import com.alexvait.accountingapi.accounting.entity.PositionEntity;
import com.alexvait.accountingapi.accounting.exception.AccessDeniedException;
import com.alexvait.accountingapi.accounting.exception.PositionNotFoundException;
import com.alexvait.accountingapi.accounting.mapper.PositionMapper;
import com.alexvait.accountingapi.accounting.model.dto.PositionDto;
import com.alexvait.accountingapi.accounting.repository.PositionRepository;
import com.alexvait.accountingapi.security.config.authentication.AuthenticationFacade;
import com.alexvait.accountingapi.security.model.UserPrincipal;
import com.alexvait.accountingapi.usermanagement.entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.alexvait.accountingapi.helpers.TestObjectsGenerator.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Test Position service implementation")
class PositionServiceImplTest {

    private final UserEntity authenticatedUser = createTestUserEntity();
    @InjectMocks
    private PositionServiceImpl positionService;
    @Mock
    private PositionRepository positionRepository;
    @Mock
    private AuthenticationFacade authenticationFacade;
    @Captor
    private ArgumentCaptor<PositionEntity> entityArgumentCaptor;
    private InvoiceEntity testInvoice;
    private List<PositionEntity> testingPositionEntities;

    @BeforeEach
    void setUp() {
        testInvoice = createTestInvoiceEntity(authenticatedUser);
        testingPositionEntities = IntStream.range(0, 20)
                .mapToObj(i -> createTestPositionEntity(testInvoice))
                .collect(Collectors.toList());

        // lenient for methods that throw exception and never reach the authenticated user check
        lenient().when(authenticationFacade.getAuthenticatedUser()).thenReturn(new UserPrincipal(authenticatedUser));
    }

    @Test
    @DisplayName("Test getOpenPositions")
    void testGetOpenPositions() {

        // arrange
        when(positionRepository.findAllByUserIdAndInvoiceIdIsNull(authenticatedUser.getId()))
                .thenReturn(testingPositionEntities);

        // act
        List<PositionDto> positionsDto = positionService.getOpenPositions();

        // assert
        List<PositionDto> positionsDtoExpected = testingPositionEntities.stream()
                .map(PositionMapper.INSTANCE::positionEntityToDto)
                .collect(Collectors.toList());

        assertNotNull(positionsDto, "not null test failed");
        assertEquals(testingPositionEntities.size(), positionsDto.size(), "positions amount test failed");
        assertThat("DTO list equality failed", positionsDtoExpected, containsInAnyOrder(positionsDto.toArray()));

        verify(positionRepository).findAllByUserIdAndInvoiceIdIsNull(authenticatedUser.getId());
        verifyNoMoreInteractions(positionRepository);
    }

    @Test
    @DisplayName("Test getPosition")
    void testGetPosition() {

        // arrange
        PositionEntity testPositionEntity = testingPositionEntities.get(0);
        when(positionRepository.findById(anyLong())).thenReturn(Optional.of(testPositionEntity));

        // act
        PositionDto positionDto = positionService.getPosition(1);

        // assert
        assertNotNull(positionDto, "not null test failed");
        assertEquals(PositionMapper.INSTANCE.positionEntityToDto(testPositionEntity), positionDto, "DTO equality failed");

        verify(positionRepository).findById(anyLong());
        verifyNoMoreInteractions(positionRepository);
    }

    @Test
    @DisplayName("Test getPosition not found")
    void testGetPositionNotFound() {

        // arrange
        when(positionRepository.findById(anyLong())).thenReturn(Optional.empty());

        // act, assert
        assertThrows(PositionNotFoundException.class, () -> positionService.getPosition(1), "Throwing exception test failed");

        verify(positionRepository).findById(anyLong());
        verifyNoMoreInteractions(positionRepository);
    }

    @Test
    @DisplayName("Test getPosition with wrong user")
    void testGetPositionWrongUser() {

        // arrange
        PositionEntity testPositionEntity = testingPositionEntities.get(0);
        testPositionEntity.getInvoice().getUser().setId(testInvoice.getUser().getId() + 1);

        when(positionRepository.findById(anyLong())).thenReturn(Optional.of(testPositionEntity));

        // act, assert
        assertThrows(AccessDeniedException.class, () -> positionService.getPosition(1), "Throwing exception test failed");

        verify(positionRepository).findById(anyLong());
        verifyNoMoreInteractions(positionRepository);
    }

    @Test
    @DisplayName("Test billPositions")
    void testBillPositions() {

        // arrange
        testingPositionEntities.forEach(p -> p.setInvoice(null));
        InvoiceEntity testInvoiceNew = createTestInvoiceEntity(authenticatedUser);

        // act
        positionService.billPositions(testingPositionEntities, testInvoiceNew);

        // assert
        testingPositionEntities.forEach(p ->
                assertEquals(testInvoiceNew, p.getInvoice(), "Entity equality test failed")
        );

        verify(positionRepository, times(testingPositionEntities.size())).save(any(PositionEntity.class));
        verifyNoMoreInteractions(positionRepository);
    }

    @Test
    @DisplayName("Test createPosition")
    void testCreatePosition() {
        // arrange
        PositionEntity testPositionEntity = createTestPositionEntity();
        PositionDto testPositionDto = PositionMapper.INSTANCE.positionEntityToDto(testPositionEntity);
        when(positionRepository.save(entityArgumentCaptor.capture())).thenReturn(testPositionEntity);

        // act
        PositionDto createdPositionDto = positionService.createPosition(testPositionDto);

        // assert
        assertNotNull(createdPositionDto, "not null test failed");
        assertEquals(createdPositionDto, testPositionDto, "DTO equality test failed");

        PositionEntity createPositionEntityCaptor = entityArgumentCaptor.getValue();
        assertEquals(authenticatedUser, createPositionEntityCaptor.getUser(), "User equality test failed");

        verify(positionRepository).save(any(PositionEntity.class));
        verifyNoMoreInteractions(positionRepository);
    }

    @Test
    @DisplayName("Test createPosition with null value as parameter")
    void testCreatePositionWithNull() {
        // arrange, act

        assertThrows(NullPointerException.class, () -> positionService.createPosition(null), "Throwing NPE test failed");

        verifyNoInteractions(positionRepository);
    }

    @Test
    @DisplayName("Test getOpenPositionEntities")
    void testGetOpenPositionEntities() {
        // arrange
        when(positionRepository.findAllByUserIdAndInvoiceIdIsNull(authenticatedUser.getId()))
                .thenReturn(testingPositionEntities);

        // act
        List<PositionEntity> returnedPositionEntities = positionService.getOpenPositionEntities();

        // assert
        assertEquals(testingPositionEntities, returnedPositionEntities, "positions list equality test failed");

        verify(positionRepository).findAllByUserIdAndInvoiceIdIsNull(authenticatedUser.getId());
        verifyNoMoreInteractions(positionRepository);
    }
}