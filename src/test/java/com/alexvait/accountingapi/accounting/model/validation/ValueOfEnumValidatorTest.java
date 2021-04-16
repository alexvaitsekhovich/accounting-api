package com.alexvait.accountingapi.accounting.model.validation;

import com.alexvait.accountingapi.accounting.entity.enums.PositionPayment;
import com.alexvait.accountingapi.accounting.mapper.PositionMapper;
import com.alexvait.accountingapi.accounting.model.request.PositionCreateRequestModel;
import com.alexvait.accountingapi.helpers.TestObjectsGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.metadata.ConstraintDescriptor;

import java.lang.annotation.Annotation;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.endsWith;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Test ValueOfEnumValidator")
class ValueOfEnumValidatorTest {

    private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Test validate success")
    void testValidateSuccess() {
        PositionCreateRequestModel positionCreateRequestModel =
                PositionMapper.INSTANCE.positionDtoToRequestModel(TestObjectsGenerator.createTestPositionDto());
        Set<ConstraintViolation<PositionCreateRequestModel>> violations = validator.validate(positionCreateRequestModel);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Test validate empty payment failure")
    void testValidateNullSuccess() {
        PositionCreateRequestModel positionCreateRequestModel =
                PositionMapper.INSTANCE.positionDtoToRequestModel(TestObjectsGenerator.createTestPositionDto());
        positionCreateRequestModel.setPayment(null);
        Set<ConstraintViolation<PositionCreateRequestModel>> violations = validator.validate(positionCreateRequestModel);
        assertEquals(2, violations.size());
    }

    @Test
    @DisplayName("Test validate failure")
    void testValidateFailure() {
        PositionCreateRequestModel positionCreateRequestModel =
                PositionMapper.INSTANCE.positionDtoToRequestModel(TestObjectsGenerator.createTestPositionDto());
        positionCreateRequestModel.setPayment("INVALID");
        Set<ConstraintViolation<PositionCreateRequestModel>> violations = validator.validate(positionCreateRequestModel);

        assertFalse(violations.isEmpty());
        ConstraintDescriptor<?> cd = violations.iterator().next().getConstraintDescriptor();
        assertEquals(ValueOfEnum.class, cd.getAnnotation().annotationType());
        assertEquals(PositionPayment.class, cd.getAttributes().get("enumClass"));
    }

}