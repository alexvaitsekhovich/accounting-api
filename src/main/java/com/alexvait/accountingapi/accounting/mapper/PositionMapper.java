package com.alexvait.accountingapi.accounting.mapper;

import com.alexvait.accountingapi.accounting.entity.PositionEntity;
import com.alexvait.accountingapi.accounting.model.dto.PositionDto;
import com.alexvait.accountingapi.accounting.model.response.PositionResponseModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = InvoiceMapper.class)
public interface PositionMapper {
    PositionMapper INSTANCE = Mappers.getMapper(PositionMapper.class);

    @Mapping(source = "invoice.number", target = "invoiceNumber")
    PositionDto positionEntityToDto(PositionEntity positionEntity);

    PositionResponseModel positionDtoToResponseModel(PositionDto positionDto);
}
