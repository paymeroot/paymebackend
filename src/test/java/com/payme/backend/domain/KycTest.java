package com.payme.backend.domain;

import static com.payme.backend.domain.CustomerTestSamples.*;
import static com.payme.backend.domain.KycTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.payme.backend.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class KycTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Kyc.class);
        Kyc kyc1 = getKycSample1();
        Kyc kyc2 = new Kyc();
        assertThat(kyc1).isNotEqualTo(kyc2);

        kyc2.setId(kyc1.getId());
        assertThat(kyc1).isEqualTo(kyc2);

        kyc2 = getKycSample2();
        assertThat(kyc1).isNotEqualTo(kyc2);
    }

    @Test
    void customerTest() {
        Kyc kyc = getKycRandomSampleGenerator();
        Customer customerBack = getCustomerRandomSampleGenerator();

        kyc.setCustomer(customerBack);
        assertThat(kyc.getCustomer()).isEqualTo(customerBack);
        assertThat(customerBack.getKyc()).isEqualTo(kyc);

        kyc.customer(null);
        assertThat(kyc.getCustomer()).isNull();
        assertThat(customerBack.getKyc()).isNull();
    }
}
