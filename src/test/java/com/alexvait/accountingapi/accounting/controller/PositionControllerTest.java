package com.alexvait.accountingapi.accounting.controller;

import com.alexvait.accountingapi.accounting.entity.enums.PositionPayment;
import com.alexvait.accountingapi.accounting.mapper.PositionMapper;
import com.alexvait.accountingapi.accounting.model.dto.PositionDto;
import com.alexvait.accountingapi.accounting.model.request.PositionCreateRequestModel;
import com.alexvait.accountingapi.accounting.model.response.PositionResponseModel;
import com.alexvait.accountingapi.accounting.service.PositionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.alexvait.accountingapi.helpers.TestObjectsGenerator.createTestPositionDto;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Test Position controller")
class PositionControllerTest {

    @InjectMocks
    PositionController positionController;

    @Mock
    PositionService positionService;

    @Test
    @DisplayName("Test getFreePositions")
    void testGetFreePositions() {
        // arrange
        List<PositionDto> positionsDto = IntStream.range(0, 20)
                .mapToObj(i -> createTestPositionDto())
                .collect(Collectors.toList());
        when(positionService.getOpenPositions()).thenReturn(positionsDto);

        // act
        CollectionModel<EntityModel<PositionResponseModel>> collectionModel = positionController.getFreePositions();
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

        verify(positionService).getOpenPositions();
        verifyNoMoreInteractions(positionService);
    }

    @Test
    @DisplayName("Test getPosition without invoice")
    void testGetPositionWithoutInvoice() {
        // arrange
        PositionDto positionDto = createTestPositionDto();
        when(positionService.getPosition(anyLong())).thenReturn(positionDto);

        // act
        EntityModel<PositionResponseModel> positionEntityModel = positionController.getPosition(1);

        //assert
        assertEquals(PositionMapper.INSTANCE.positionDtoToResponseModel(positionDto), positionEntityModel.getContent());

        assertAll(
                "test entity models links",
                () -> assertTrue(positionEntityModel.getLinks().hasSize(0), "hasSize failed"),
                () -> assertNotNull(positionEntityModel.getLink("self"), "self link failed")
        );

        verify(positionService).getPosition(anyLong());
        verifyNoMoreInteractions(positionService);
    }

    @Test
    @DisplayName("Test getPosition with invoice")
    void testGetPositionWithInvoice() {
        // arrange
        PositionDto positionDto = createTestPositionDto();
        positionDto.setInvoiceNumber("A");
        when(positionService.getPosition(anyLong())).thenReturn(positionDto);

        // act
        EntityModel<PositionResponseModel> positionEntityModel = positionController.getPosition(1);

        //assert
        assertEquals(PositionMapper.INSTANCE.positionDtoToResponseModel(positionDto), positionEntityModel.getContent());

        assertAll(
                "test entity models links",
                () -> assertTrue(positionEntityModel.getLinks().hasSize(1), "hasSize failed"),
                () -> assertNotNull(positionEntityModel.getLink("invoice"), "invoice link failed")
        );

        verify(positionService).getPosition(anyLong());
        verifyNoMoreInteractions(positionService);
    }

    @Test
    @DisplayName("Test createPosition")
    void testCreatePosition() {
        // arrange
        PositionDto positionDto = createTestPositionDto();
        when(positionService.createPosition(any(PositionDto.class))).thenReturn(positionDto);
        PositionCreateRequestModel positionCreateRequestModel = PositionMapper.INSTANCE.positionDtoToRequestModel(positionDto);

        // act
        EntityModel<PositionResponseModel> positionEntityModel = positionController.createPosition(positionCreateRequestModel);

        //assert
        assertEquals(PositionMapper.INSTANCE.positionDtoToResponseModel(positionDto), positionEntityModel.getContent());

        assertAll(
                "test entity models links",
                () -> assertTrue(positionEntityModel.getLinks().hasSize(1), "hasSize failed"),
                () -> assertNotNull(positionEntityModel.getLink("self"), "self link failed")
        );

        verify(positionService).createPosition(any(PositionDto.class));
        verifyNoMoreInteractions(positionService);
    }

    @Test
    void getPayments() {
        List<String> payments = positionController.getPayments();
        assertEquals(PositionPayment.values().length, payments.size());

    }
}