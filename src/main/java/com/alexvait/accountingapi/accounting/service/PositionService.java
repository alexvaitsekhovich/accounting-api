package com.alexvait.accountingapi.accounting.service;

import com.alexvait.accountingapi.accounting.entity.InvoiceEntity;
import com.alexvait.accountingapi.accounting.entity.PositionEntity;
import com.alexvait.accountingapi.accounting.model.dto.PositionDto;

import java.util.List;

public interface PositionService {
    List<PositionDto> getOpenPositions();

    List<PositionEntity> getOpenPositionEntities();

    PositionDto getPosition(long id);

    void billPositions(List<PositionEntity> openPositions, InvoiceEntity invoiceEntity);

    PositionDto createPosition(PositionDto newPositionDto);
}
