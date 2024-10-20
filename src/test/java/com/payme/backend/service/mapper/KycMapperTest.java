package com.payme.backend.service.mapper;

import static com.payme.backend.domain.KycAsserts.*;
import static com.payme.backend.domain.KycTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class KycMapperTest {

    private KycMapper kycMapper;

    @BeforeEach
    void setUp() {
        kycMapper = new KycMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getKycSample1();
        var actual = kycMapper.toEntity(kycMapper.toDto(expected));
        assertKycAllPropertiesEquals(expected, actual);
    }
}
