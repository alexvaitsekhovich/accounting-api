package com.alexvait.accountingapi.accounting.mapper;

import com.alexvait.accountingapi.accounting.entity.PositionEntity;
import com.alexvait.accountingapi.accounting.model.dto.PositionDto;
import com.alexvait.accountingapi.accounting.model.request.PositionCreateRequestModel;
import com.alexvait.accountingapi.accounting.model.response.PositionResponseModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = InvoiceMapper.class)
public interface PositionMapper {
    PositionMapper INSTANCE = Mappers.getMapper(PositionMapper.class);

    @Mappings({
            @Mapping(target = "created", source = "createdDate", dateFormat = "yyyy-MM-dd HH:mm:ss"),
            @Mapping(source = "invoice.number", target = "invoiceNumber")
    })
    PositionDto positionEntityToDto(PositionEntity positionEntity);

    PositionEntity positionDtoToEntity(PositionDto positionDto);

    PositionResponseModel positionDtoToResponseModel(PositionDto positionDto);

    PositionDto positionRequestModelToDto(PositionCreateRequestModel positionRequestModel);

    PositionCreateRequestModel positionDtoToRequestModel(PositionDto positionDto);
}
