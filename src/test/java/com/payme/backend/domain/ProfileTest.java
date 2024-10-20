package com.payme.backend.domain;

import static com.payme.backend.domain.CustomerTestSamples.*;
import static com.payme.backend.domain.ProfileTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.payme.backend.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ProfileTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Profile.class);
        Profile profile1 = getProfileSample1();
        Profile profile2 = new Profile();
        assertThat(profile1).isNotEqualTo(profile2);

        profile2.setId(profile1.getId());
        assertThat(profile1).isEqualTo(profile2);

        profile2 = getProfileSample2();
        assertThat(profile1).isNotEqualTo(profile2);
    }

    @Test
    void customerTest() {
        Profile profile = getProfileRandomSampleGenerator();
        Customer customerBack = getCustomerRandomSampleGenerator();

        profile.setCustomer(customerBack);
        assertThat(profile.getCustomer()).isEqualTo(customerBack);
        assertThat(customerBack.getProfile()).isEqualTo(profile);

        profile.customer(null);
        assertThat(profile.getCustomer()).isNull();
        assertThat(customerBack.getProfile()).isNull();
    }
}
