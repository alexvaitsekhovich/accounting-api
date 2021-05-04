package com.alexvait.accountingapi.accounting.service;

import com.alexvait.accountingapi.accounting.entity.InvoiceEntity;
import com.alexvait.accountingapi.accounting.entity.PositionEntity;
import com.alexvait.accountingapi.accounting.exception.PositionNotFoundException;
import com.alexvait.accountingapi.accounting.mapper.PositionMapper;
import com.alexvait.accountingapi.accounting.model.dto.PositionDto;
import com.alexvait.accountingapi.accounting.repository.PositionRepository;
import com.alexvait.accountingapi.security.config.authentication.AuthenticationFacade;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class PositionServiceImpl implements PositionService {
    private final PositionRepository positionRepository;
    private final AuthenticationFacade authenticationFacade;
    private final PositionMapper positionMapper;

    public PositionServiceImpl(PositionRepository positionRepository, AuthenticationFacade authenticationFacade) {
        this.positionRepository = positionRepository;
        this.authenticationFacade = authenticationFacade;
        this.positionMapper = PositionMapper.INSTANCE;
    }

    @Override
    public List<PositionDto> getOpenPositions() {
        List<PositionEntity> positionEntities = getOpenPositionEntities();

        return positionEntities.stream()
                .map(positionMapper::positionEntityToDto)
                .collect(Collectors.toList());
    }

    @Override
    public PositionDto getPosition(long id) {
        PositionEntity positionEntity = positionRepository.findById(id).orElseThrow(() ->
                new PositionNotFoundException("Position not found: " + id));

        if (positionEntity.getUser().getId() != getAuthenticatedUserId())
            throw new AccessDeniedException("User has no access to the position");

        return positionMapper.positionEntityToDto(positionEntity);
    }

    @Override
    public void billPositions(List<PositionEntity> openPositions, InvoiceEntity invoiceEntity) {
        openPositions.forEach(
                p -> {
                    p.setInvoice(invoiceEntity);
                    positionRepository.save(p);
                });
    }

    @Override
    public PositionDto createPosition(PositionDto newPositionDto) {
        Objects.requireNonNull(newPositionDto);

        PositionEntity positionEntity = positionMapper.positionDtoToEntity(newPositionDto);
        positionEntity.setUser(authenticationFacade.getAuthenticatedUser().getUserEntity());
        return positionMapper.positionEntityToDto(positionRepository.save(positionEntity));
    }

    public List<PositionEntity> getOpenPositionEntities() {
        return positionRepository.findAllByUserIdAndInvoiceIdIsNull(getAuthenticatedUserId());
    }

    private long getAuthenticatedUserId() {
        return authenticationFacade.getAuthenticatedUser().getId();
    }

}
