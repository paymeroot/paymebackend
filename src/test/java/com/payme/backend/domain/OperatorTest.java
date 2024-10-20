package com.payme.backend.domain;

import static com.payme.backend.domain.CountryTestSamples.*;
import static com.payme.backend.domain.OperatorTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.payme.backend.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class OperatorTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Operator.class);
        Operator operator1 = getOperatorSample1();
        Operator operator2 = new Operator();
        assertThat(operator1).isNotEqualTo(operator2);

        operator2.setId(operator1.getId());
        assertThat(operator1).isEqualTo(operator2);

        operator2 = getOperatorSample2();
        assertThat(operator1).isNotEqualTo(operator2);
    }

    @Test
    void countryTest() {
        Operator operator = getOperatorRandomSampleGenerator();
        Country countryBack = getCountryRandomSampleGenerator();

        operator.setCountry(countryBack);
        assertThat(operator.getCountry()).isEqualTo(countryBack);

        operator.country(null);
        assertThat(operator.getCountry()).isNull();
    }
}
