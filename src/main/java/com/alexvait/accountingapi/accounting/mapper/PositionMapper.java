package com.alexvait.accountingapi.accounting.mapper;

import com.alexvait.accountingapi.accounting.entity.PositionEntity;
import com.alexvait.accountingapi.accounting.model.dto.PositionDto;
import com.alexvait.accountingapi.accounting.model.response.PositionResponseModel;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface PositionMapper {
    PositionMapper INSTANCE = Mappers.getMapper(PositionMapper.class);

    PositionDto positionEntityToDto(PositionEntity positionEntity);

    PositionResponseModel positionDtoToResponseModel(PositionDto positionDto);
}
