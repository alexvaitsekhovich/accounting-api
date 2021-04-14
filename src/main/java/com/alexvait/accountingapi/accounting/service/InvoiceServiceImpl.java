package com.alexvait.accountingapi.accounting.service;

import com.alexvait.accountingapi.accounting.entity.InvoiceEntity;
import com.alexvait.accountingapi.accounting.entity.PositionEntity;
import com.alexvait.accountingapi.accounting.exception.AccessDeniedException;
import com.alexvait.accountingapi.accounting.exception.InvoiceNotFoundException;
import com.alexvait.accountingapi.accounting.exception.NotFoundException;
import com.alexvait.accountingapi.accounting.mapper.InvoiceMapper;
import com.alexvait.accountingapi.accounting.mapper.PositionMapper;
import com.alexvait.accountingapi.accounting.model.dto.InvoiceDto;
import com.alexvait.accountingapi.accounting.model.dto.PositionDto;
import com.alexvait.accountingapi.accounting.repository.InvoiceRepository;
import com.alexvait.accountingapi.security.config.authentication.AuthenticationFacade;
import com.alexvait.accountingapi.security.utils.RandomStringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final PositionService positionService;
    private final InvoiceMapper invoiceMapper;
    private final PositionMapper positionMapper;
    private final AuthenticationFacade authenticationFacade;

    public InvoiceServiceImpl(InvoiceRepository invoiceRepository, PositionService positionService,
                              AuthenticationFacade authenticationFacade) {
        this.invoiceRepository = invoiceRepository;
        this.positionService = positionService;

        this.authenticationFacade = authenticationFacade;

        this.invoiceMapper = InvoiceMapper.INSTANCE;
        this.positionMapper = PositionMapper.INSTANCE;
    }

    @Override
    public List<InvoiceDto> getInvoices(int page, int size) {
        List<InvoiceEntity> entitiesPage = invoiceRepository.findAllByUserId(getAuthenticatedUserId(), PageRequest.of(page, size));

        return entitiesPage.stream()
                .map(invoiceMapper::invoiceEntityToDto)
                .collect(Collectors.toList());
    }

    @Override
    public InvoiceDto getInvoice(String number) {
        return convertInvoiceEntityToDto(findByNumber(number));
    }

    @Override
    public List<PositionDto> getPositions(String number) {
        InvoiceEntity invoiceEntity = findByNumber(number);
        return invoiceEntity.getPositions().stream()
                .map(positionMapper::positionEntityToDto)
                .collect(Collectors.toList());
    }

    @Override
    public InvoiceDto generateInvoice() {
        List<PositionEntity> openPositions = positionService.getOpenPositionEntities();

        if (openPositions.size() == 0) {
            throw new NotFoundException("No open positions found");
        }

        InvoiceEntity invoiceEntity = new InvoiceEntity();
        invoiceEntity.setNumber(RandomStringUtils.randomAlphabetic(20));

        long positionsSum = openPositions.stream().mapToLong(PositionEntity::getAmount).sum();
        invoiceEntity.setAmount(positionsSum);
        invoiceEntity.setUser(authenticationFacade.getAuthenticatedUser().getUserEntity());

        invoiceRepository.save(invoiceEntity);

        positionService.billPositions(openPositions, invoiceEntity);

        return invoiceMapper.invoiceEntityToDto(invoiceEntity);
    }

    private InvoiceDto convertInvoiceEntityToDto(InvoiceEntity invoiceEntity) {
        return invoiceMapper.invoiceEntityToDto(invoiceEntity);
    }

    private InvoiceEntity findByNumber(String number) {
        InvoiceEntity invoiceEntity = invoiceRepository.findByNumber(number);

        if (invoiceEntity == null)
            throw new InvoiceNotFoundException(number);

        if (invoiceEntity.getUser().getId() != getAuthenticatedUserId())
            throw new AccessDeniedException("User has no access to the invoice");

        return invoiceEntity;
    }

    private long getAuthenticatedUserId() {
        return authenticationFacade.getAuthenticatedUser().getId();
    }
}
