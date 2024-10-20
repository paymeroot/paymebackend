package com.payme.backend.service.mapper;

import static com.payme.backend.domain.ProfileAsserts.*;
import static com.payme.backend.domain.ProfileTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProfileMapperTest {

    private ProfileMapper profileMapper;

    @BeforeEach
    void setUp() {
        profileMapper = new ProfileMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getProfileSample1();
        var actual = profileMapper.toEntity(profileMapper.toDto(expected));
        assertProfileAllPropertiesEquals(expected, actual);
    }
}
