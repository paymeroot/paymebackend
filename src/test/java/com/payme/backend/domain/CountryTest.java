package com.payme.backend.domain;

import static com.payme.backend.domain.CountryTestSamples.*;
import static com.payme.backend.domain.CustomerTestSamples.*;
import static com.payme.backend.domain.OperatorTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.payme.backend.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CountryTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Country.class);
        Country country1 = getCountrySample1();
        Country country2 = new Country();
        assertThat(country1).isNotEqualTo(country2);

        country2.setId(country1.getId());
        assertThat(country1).isEqualTo(country2);

        country2 = getCountrySample2();
        assertThat(country1).isNotEqualTo(country2);
    }

    @Test
    void customerTest() {
        Country country = getCountryRandomSampleGenerator();
        Customer customerBack = getCustomerRandomSampleGenerator();

        country.setCustomer(customerBack);
        assertThat(country.getCustomer()).isEqualTo(customerBack);
        assertThat(customerBack.getCountry()).isEqualTo(country);

        country.customer(null);
        assertThat(country.getCustomer()).isNull();
        assertThat(customerBack.getCountry()).isNull();
    }

    @Test
    void operatorTest() {
        Country country = getCountryRandomSampleGenerator();
        Operator operatorBack = getOperatorRandomSampleGenerator();

        country.setOperator(operatorBack);
        assertThat(country.getOperator()).isEqualTo(operatorBack);
        assertThat(operatorBack.getCountry()).isEqualTo(country);

        country.operator(null);
        assertThat(country.getOperator()).isNull();
        assertThat(operatorBack.getCountry()).isNull();
    }
}
