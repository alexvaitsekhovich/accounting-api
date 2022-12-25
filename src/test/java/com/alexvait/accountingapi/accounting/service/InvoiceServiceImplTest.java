package com.alexvait.accountingapi.accounting.service;

import com.alexvait.accountingapi.accounting.entity.InvoiceEntity;
import com.alexvait.accountingapi.accounting.entity.PositionEntity;
import com.alexvait.accountingapi.accounting.exception.InvoiceNotFoundException;
import com.alexvait.accountingapi.accounting.exception.NotFoundException;
import com.alexvait.accountingapi.accounting.mapper.InvoiceMapper;
import com.alexvait.accountingapi.accounting.mapper.PositionMapper;
import com.alexvait.accountingapi.accounting.model.dto.InvoiceDto;
import com.alexvait.accountingapi.accounting.model.dto.PositionDto;
import com.alexvait.accountingapi.accounting.model.response.hateoas.InvoiceResponseModelPagedList;
import com.alexvait.accountingapi.accounting.repository.InvoiceRepository;
import com.alexvait.accountingapi.security.config.authentication.AuthenticationFacade;
import com.alexvait.accountingapi.security.model.UserPrincipal;
import com.alexvait.accountingapi.usermanagement.entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.alexvait.accountingapi.helpers.TestObjectsGenerator.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Test Invoice service implementation")
class InvoiceServiceImplTest {

    private final UserEntity authenticatedUser = createTestUserEntity();

    @InjectMocks
    private InvoiceServiceImpl invoiceService;

    @Mock
    private InvoiceRepository invoiceRepository;

    @Mock
    private PositionService positionService;

    @Mock
    private AuthenticationFacade authenticationFacade;

    private List<InvoiceEntity> testingInvoiceEntities;

    @BeforeEach
    void setUp() {
        testingInvoiceEntities = IntStream.range(0, 10)
                .mapToObj(i -> createTestInvoiceEntity(authenticatedUser))
                .collect(Collectors.toList());

        // lenient for methods that throw exception and never reach the authenticated user check
        lenient().when(authenticationFacade.getAuthenticatedUser()).thenReturn(new UserPrincipal(authenticatedUser));
    }

    @Test
    @DisplayName("Test getInvoices")
    void testGetInvoices() {
        int pageSize = testingInvoiceEntities.size();

        Page<InvoiceEntity> invoiceEntityPage = new PageImpl<>(testingInvoiceEntities,
                PageRequest.of(0, pageSize),
                pageSize
        );

        // arrange
        PageRequest pageRequest = PageRequest.of(0, pageSize);

        when(invoiceRepository.findAllByUserId(anyLong(), any(PageRequest.class))).thenReturn(invoiceEntityPage);

        // act
        InvoiceResponseModelPagedList invoicesPagedList = invoiceService.getInvoices(pageRequest.getPageNumber(), pageRequest.getPageSize());

        // assert
        assertNotNull(invoicesPagedList);
        assertEquals(pageSize, invoicesPagedList.getSize());

        verify(invoiceRepository).findAllByUserId(anyLong(), any(PageRequest.class));
        verifyNoMoreInteractions(invoiceRepository);
    }

    @Test
    @DisplayName("Test getInvoice")
    void testGetInvoice() {
        // arrange
        InvoiceEntity testInvoiceEntity = testingInvoiceEntities.get(0);
        when(invoiceRepository.findByNumber(anyString())).thenReturn(testInvoiceEntity);

        // act
        InvoiceDto invoiceDto = invoiceService.getInvoice("testNumber");

        // assert
        assertNotNull(invoiceDto);
        InvoiceEntity retrievedEntity = InvoiceMapper.INSTANCE.invoiceDtoToEntity(invoiceDto);
        assertAll(
                () -> assertEquals(testInvoiceEntity.getId(), retrievedEntity.getId()),
                () -> assertEquals(testInvoiceEntity.getNumber(), retrievedEntity.getNumber()),
                () -> assertEquals(testInvoiceEntity.getAmount(), retrievedEntity.getAmount()),
                () -> assertEquals(testInvoiceEntity.getState(), retrievedEntity.getState()),
                () -> assertEquals(testInvoiceEntity.getCustomerId(), retrievedEntity.getCustomerId())
        );
        verify(invoiceRepository).findByNumber(anyString());
        verifyNoMoreInteractions(invoiceRepository);
    }

    @Test
    @DisplayName("Test getInvoice not found")
    void testGetInvoiceNotFound() {
        // arrange
        when(invoiceRepository.findByNumber(anyString())).thenReturn(null);

        // act, assert
        assertThrows(InvoiceNotFoundException.class, () -> invoiceService.getInvoice("testNumber"));
        verify(invoiceRepository).findByNumber(anyString());
        verifyNoMoreInteractions(invoiceRepository);
    }

    @Test
    @DisplayName("Test getInvoice with wrong user")
    void testGetInvoiceWrongUser() {
        // arrange
        InvoiceEntity testInvoiceEntity = testingInvoiceEntities.get(0);
        UserEntity user = createTestUserEntity();
        user.setId(testInvoiceEntity.getUser().getId() + 1);
        testInvoiceEntity.setUser(user);

        when(invoiceRepository.findByNumber(anyString())).thenReturn(testInvoiceEntity);

        // act, assert
        assertThrows(AccessDeniedException.class, () -> invoiceService.getInvoice("testNumber"));
        verify(invoiceRepository).findByNumber(anyString());
        verifyNoMoreInteractions(invoiceRepository);
    }

    @Test
    @DisplayName("Test getPositions")
    void testGetPositions() {
        // arrange
        InvoiceEntity testInvoiceEntity = testingInvoiceEntities.get(0);

        List<PositionEntity> testingPositionEntities = IntStream.range(0, 20)
                .mapToObj(i -> createTestPositionEntity(testingInvoiceEntities.get(0)))
                .collect(Collectors.toList());

        testInvoiceEntity.setPositions(testingPositionEntities);

        when(invoiceRepository.findByNumber(anyString())).thenReturn(testInvoiceEntity);

        // act
        List<PositionDto> positionsDto = invoiceService.getPositions("testNumber");

        // assert
        assertNotNull(positionsDto);

        List<PositionDto> positionsDtoExpected = testingPositionEntities.stream()
                .map(PositionMapper.INSTANCE::positionEntityToDto)
                .collect(Collectors.toList());

        assertThat(positionsDtoExpected, containsInAnyOrder(positionsDto.toArray()));

        verify(invoiceRepository).findByNumber(anyString());
        verifyNoMoreInteractions(invoiceRepository);
    }

    @Test
    @DisplayName("Test generateInvoice")
    void testGenerateInvoice() {
        // arrange
        List<PositionEntity> testingPositionEntities = IntStream.range(0, 20)
                .mapToObj(i -> createTestPositionEntity())
                .collect(Collectors.toList());
        when(positionService.getOpenPositionEntities()).thenReturn(testingPositionEntities);
        long positionsSum = testingPositionEntities.stream().mapToLong(PositionEntity::getAmount).sum();

        // act
        InvoiceDto invoiceDto = invoiceService.generateInvoice();

        // assert
        assertNotNull(invoiceDto);
        assertEquals(authenticatedUser.getId(), invoiceDto.getUser().getId());
        assertEquals(positionsSum, invoiceDto.getAmount());
        verify(positionService).getOpenPositionEntities();
        verify(positionService).billPositions(anyList(), any(InvoiceEntity.class));
        verifyNoMoreInteractions(positionService);

        verify(invoiceRepository).save(any(InvoiceEntity.class));
        verifyNoMoreInteractions(invoiceRepository);
    }

    @Test
    @DisplayName("Test generateInvoice when no positions found")
    void testGenerateInvoiceNoPositions() {
        // arrange
        when(positionService.getOpenPositionEntities()).thenReturn(new ArrayList<>());

        // act, assert
        assertThrows(NotFoundException.class, () -> invoiceService.generateInvoice());
        verify(positionService).getOpenPositionEntities();
        verifyNoMoreInteractions(positionService);

        verifyNoMoreInteractions(invoiceRepository);
    }
}