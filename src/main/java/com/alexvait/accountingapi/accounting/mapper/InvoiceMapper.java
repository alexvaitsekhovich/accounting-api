package com.alexvait.accountingapi.accounting.mapper;

import com.alexvait.accountingapi.accounting.entity.InvoiceEntity;
import com.alexvait.accountingapi.accounting.model.dto.InvoiceDto;
import com.alexvait.accountingapi.accounting.model.response.InvoiceResponseModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface InvoiceMapper {
    InvoiceMapper INSTANCE = Mappers.getMapper(InvoiceMapper.class);

    @Mapping(target = "created", source = "createdDate", dateFormat = "yyyy-MM-dd HH:mm:ss")
    InvoiceDto invoiceEntityToDto(InvoiceEntity invoiceEntity);

    InvoiceEntity invoiceDtoToEntity(InvoiceDto invoiceDto);

    InvoiceResponseModel invoiceDtoToResponseModel(InvoiceDto invoiceDto);

}
