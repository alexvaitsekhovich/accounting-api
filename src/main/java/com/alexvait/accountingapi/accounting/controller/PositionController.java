package com.alexvait.accountingapi.accounting.controller;

import com.alexvait.accountingapi.accounting.controller.annotated.PositionControllerAnnotated;
import com.alexvait.accountingapi.accounting.entity.enums.PositionPayment;
import com.alexvait.accountingapi.accounting.mapper.PositionMapper;
import com.alexvait.accountingapi.accounting.model.dto.PositionDto;
import com.alexvait.accountingapi.accounting.model.request.PositionCreateRequestModel;
import com.alexvait.accountingapi.accounting.model.response.PositionResponseModel;
import com.alexvait.accountingapi.accounting.model.response.hateoas.PositionHateoasBuilderUtil;
import com.alexvait.accountingapi.accounting.service.PositionService;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(PositionController.BASE_URL)
@CrossOrigin(origins = "*")
public class PositionController implements PositionControllerAnnotated {

    public static final String BASE_URL = "/api/v1/position";

    private final PositionService positionService;
    private final PositionMapper positionMapper = PositionMapper.INSTANCE;

    public PositionController(PositionService positionService) {
        this.positionService = positionService;
    }

    @GetMapping
    public CollectionModel<EntityModel<PositionResponseModel>> getFreePositions() {
        List<PositionDto> positionsDto = positionService.getOpenPositions();

        List<EntityModel<PositionResponseModel>> positions = positionsDto.stream()
                .map(PositionHateoasBuilderUtil::getPositionHateoasFromDtoNoInvoiceLink)
                .collect(Collectors.toList());

        return CollectionModel.of(positions);
    }

    @GetMapping("/{positionId}")
    public EntityModel<PositionResponseModel> getPosition(@PathVariable long positionId) {
        PositionDto positionDto = positionService.getPosition(positionId);
        return PositionHateoasBuilderUtil.getPositionHateoasFromDto(positionDto);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EntityModel<PositionResponseModel> createPosition(@Valid @RequestBody PositionCreateRequestModel positionReqModel) {

        PositionDto newPositionDto = positionMapper.positionRequestModelToDto(positionReqModel);
        PositionDto createdPositionDto = positionService.createPosition(newPositionDto);

        return PositionHateoasBuilderUtil.getPositionHateoasFromDtoNoInvoiceLink(createdPositionDto);
    }

    @GetMapping("/payments")
    public List<String> getPayments() {
        return Arrays.stream(PositionPayment.values())
                .map(Enum::name)
                .map(String::toLowerCase)
                .collect(Collectors.toList());
    }

}
