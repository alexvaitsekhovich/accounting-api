package com.alexvait.accountingapi.helpers;

import com.alexvait.accountingapi.accounting.entity.InvoiceEntity;
import com.alexvait.accountingapi.accounting.entity.PositionEntity;
import com.alexvait.accountingapi.accounting.entity.enums.PositionPayment;
import com.alexvait.accountingapi.usermanagement.entity.UserEntity;
import com.alexvait.accountingapi.usermanagement.mapper.UserMapper;
import com.alexvait.accountingapi.usermanagement.model.dto.UserDto;

import java.security.SecureRandom;
import java.util.Random;

import static com.alexvait.accountingapi.security.utils.RandomStringUtils.randomAlphabetic;

public class TestObjectsGenerator {

    private final static Random random = new SecureRandom();

    public static UserEntity createTestUserEntity() {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(random.nextInt(1000));
        userEntity.setPublicId(randomAlphabetic(40));
        userEntity.setFirstName(randomAlphabetic(20));
        userEntity.setLastName(randomAlphabetic(20));
        userEntity.setEmail(randomAlphabetic(20) + "@api.com");
        userEntity.setEncryptedPassword(randomAlphabetic(40));
        return userEntity;
    }

    public static UserDto createTestUserDto() {
        UserDto userDto = UserMapper.INSTANCE.userEntityToDto(TestObjectsGenerator.createTestUserEntity());
        userDto.setPassword(randomAlphabetic(20));
        return userDto;
    }

    public static InvoiceEntity createTestInvoiceEntity() {
        return createTestInvoiceEntity(null);
    }

    public static InvoiceEntity createTestInvoiceEntity(UserEntity user) {
        InvoiceEntity invoiceEntity = new InvoiceEntity();
        invoiceEntity.setNumber(randomAlphabetic(20));
        invoiceEntity.setAmount(random.nextInt(1000));
        invoiceEntity.setUser(user);
        return invoiceEntity;
    }

    public static PositionEntity createTestPositionEntity(InvoiceEntity invoice) {
        PositionEntity positionEntity = new PositionEntity();
        positionEntity.setAmount(random.nextInt(1000));
        positionEntity.setPayment(PositionPayment.values()[random.nextInt(PositionPayment.values().length)]);
        positionEntity.setCustomerId(random.nextInt(1000));
        positionEntity.setInvoice(invoice);
        positionEntity.setLabel(randomAlphabetic(20));

        if (invoice != null)
            positionEntity.setUser(invoice.getUser());

        return positionEntity;
    }

    public static PositionEntity createTestPositionEntity() {
        return createTestPositionEntity(null);
    }
}