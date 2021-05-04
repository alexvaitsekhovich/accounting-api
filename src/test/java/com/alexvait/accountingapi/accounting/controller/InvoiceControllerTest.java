package com.alexvait.accountingapi.accounting.controller;

import com.alexvait.accountingapi.accounting.mapper.InvoiceMapper;
import com.alexvait.accountingapi.accounting.mapper.PositionMapper;
import com.alexvait.accountingapi.accounting.model.dto.InvoiceDto;
import com.alexvait.accountingapi.accounting.model.dto.PositionDto;
import com.alexvait.accountingapi.accounting.model.response.InvoiceResponseModel;
import com.alexvait.accountingapi.accounting.model.response.PositionResponseModel;
import com.alexvait.accountingapi.accounting.model.response.hateoas.InvoiceHateoasBuilderUtil;
import com.alexvait.accountingapi.accounting.model.response.hateoas.InvoiceResponseModelPagedList;
import com.alexvait.accountingapi.accounting.service.InvoiceService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.alexvait.accountingapi.helpers.TestObjectsGenerator.createTestInvoiceDto;
import static com.alexvait.accountingapi.helpers.TestObjectsGenerator.createTestPositionDto;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Test Invoice controller")
class InvoiceControllerTest {

    @InjectMocks
    private InvoiceController invoiceController;

    @Mock
    private InvoiceService invoiceService;

    @Test
    @DisplayName("Test getInvoices with default pagination")
    void testGetInvoicesWithDefaultPagination() {

        // act
        invoiceController.getInvoices();

        // assert
        verify(invoiceService).getInvoices(
                Integer.parseInt(InvoiceController.defaultInvoicesPageNumber),
                Integer.parseInt(InvoiceController.defaultInvoicesPageSize));

        verifyNoMoreInteractions(invoiceService);
    }

    @Test
    @DisplayName("Test getInvoice")
    void testGetInvoice() {

        // arrange
        InvoiceDto invoiceDto = createTestInvoiceDto();
        when(invoiceService.getInvoice(anyString())).thenReturn(invoiceDto);

        // act
        EntityModel<InvoiceResponseModel> invoiceEntityModel = invoiceController.getInvoice("1");

        //assert
        assertEquals(InvoiceMapper.INSTANCE.invoiceDtoToResponseModel(invoiceDto), invoiceEntityModel.getContent());

        assertAll(
                "test entity models links",
                () -> assertTrue(invoiceEntityModel.getLinks().hasSize(2), "hasSize failed"),
                () -> assertNotNull(invoiceEntityModel.getLink("self"), "self link failed"),
                () -> assertNotNull(invoiceEntityModel.getLink("positions"), "positions link failed")
        );

        verify(invoiceService).getInvoice(anyString());
        verifyNoMoreInteractions(invoiceService);
    }

    @Test
    @DisplayName("Test getBilledPositions")
    void testGetBilledPositions() {

        // arrange
        List<PositionDto> positionsDto = IntStream.range(0, 20)
                .mapToObj(i -> createTestPositionDto())
                .collect(Collectors.toList());
        when(invoiceService.getPositions(anyString())).thenReturn(positionsDto);

        // act
        CollectionModel<EntityModel<PositionResponseModel>> collectionModel = invoiceController.getBilledPositions("1");
        Collection<EntityModel<PositionResponseModel>> responseModels = collectionModel.getContent();

        //assert
        assertAll(
                "test entity models",
                () -> assertEquals(positionsDto.size(), responseModels.size(), "size failed"),
                () -> assertThat(
                        "contains element failed",
                        positionsDto.stream().map(PositionMapper.INSTANCE::positionDtoToResponseModel).collect(Collectors.toList())
                        , containsInAnyOrder(responseModels.stream().map(EntityModel::getContent).toArray())
                )
        );

        responseModels.forEach(responseModel ->
                assertAll(
                        "test entity models links for id #" + responseModel.getContent().getId(),
                        () -> assertTrue(responseModel.getLinks().hasSize(1), "hasSize failed"),
                        () -> assertNotNull(responseModel.getLink("self"), "self link failed")
                )
        );

        verify(invoiceService).getPositions(anyString());
        verifyNoMoreInteractions(invoiceService);
    }

    @Test
    @DisplayName("Test createInvoice")
    void createInvoice() {
        // arrange
        InvoiceDto invoiceDto = createTestInvoiceDto();
        when(invoiceService.generateInvoice()).thenReturn(invoiceDto);

        // act
        EntityModel<InvoiceResponseModel> invoiceEntityModel = invoiceController.createInvoice();

        //assert
        assertEquals(InvoiceMapper.INSTANCE.invoiceDtoToResponseModel(invoiceDto), invoiceEntityModel.getContent());

        assertAll(
                "test entity models links",
                () -> assertTrue(invoiceEntityModel.getLinks().hasSize(2), "hasSize failed"),
                () -> assertNotNull(invoiceEntityModel.getLink("self"), "self link failed"),
                () -> assertNotNull(invoiceEntityModel.getLink("positions"), "positions link failed")
        );

        verify(invoiceService).generateInvoice();
        verifyNoMoreInteractions(invoiceService);
    }

}