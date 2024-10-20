package com.payme.backend.domain;

import static com.payme.backend.domain.CountryTestSamples.*;
import static com.payme.backend.domain.CustomerTestSamples.*;
import static com.payme.backend.domain.KycTestSamples.*;
import static com.payme.backend.domain.ProfileTestSamples.*;
import static com.payme.backend.domain.RefundTestSamples.*;
import static com.payme.backend.domain.TransactionTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.payme.backend.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class CustomerTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Customer.class);
        Customer customer1 = getCustomerSample1();
        Customer customer2 = new Customer();
        assertThat(customer1).isNotEqualTo(customer2);

        customer2.setId(customer1.getId());
        assertThat(customer1).isEqualTo(customer2);

        customer2 = getCustomerSample2();
        assertThat(customer1).isNotEqualTo(customer2);
    }

    @Test
    void profileTest() {
        Customer customer = getCustomerRandomSampleGenerator();
        Profile profileBack = getProfileRandomSampleGenerator();

        customer.setProfile(profileBack);
        assertThat(customer.getProfile()).isEqualTo(profileBack);

        customer.profile(null);
        assertThat(customer.getProfile()).isNull();
    }

    @Test
    void kycTest() {
        Customer customer = getCustomerRandomSampleGenerator();
        Kyc kycBack = getKycRandomSampleGenerator();

        customer.setKyc(kycBack);
        assertThat(customer.getKyc()).isEqualTo(kycBack);

        customer.kyc(null);
        assertThat(customer.getKyc()).isNull();
    }

    @Test
    void countryTest() {
        Customer customer = getCustomerRandomSampleGenerator();
        Country countryBack = getCountryRandomSampleGenerator();

        customer.setCountry(countryBack);
        assertThat(customer.getCountry()).isEqualTo(countryBack);

        customer.country(null);
        assertThat(customer.getCountry()).isNull();
    }

    @Test
    void transactionTest() {
        Customer customer = getCustomerRandomSampleGenerator();
        Transaction transactionBack = getTransactionRandomSampleGenerator();

        customer.addTransaction(transactionBack);
        assertThat(customer.getTransactions()).containsOnly(transactionBack);
        assertThat(transactionBack.getCustomer()).isEqualTo(customer);

        customer.removeTransaction(transactionBack);
        assertThat(customer.getTransactions()).doesNotContain(transactionBack);
        assertThat(transactionBack.getCustomer()).isNull();

        customer.transactions(new HashSet<>(Set.of(transactionBack)));
        assertThat(customer.getTransactions()).containsOnly(transactionBack);
        assertThat(transactionBack.getCustomer()).isEqualTo(customer);

        customer.setTransactions(new HashSet<>());
        assertThat(customer.getTransactions()).doesNotContain(transactionBack);
        assertThat(transactionBack.getCustomer()).isNull();
    }

    @Test
    void refundTest() {
        Customer customer = getCustomerRandomSampleGenerator();
        Refund refundBack = getRefundRandomSampleGenerator();

        customer.addRefund(refundBack);
        assertThat(customer.getRefunds()).containsOnly(refundBack);
        assertThat(refundBack.getCustomer()).isEqualTo(customer);

        customer.removeRefund(refundBack);
        assertThat(customer.getRefunds()).doesNotContain(refundBack);
        assertThat(refundBack.getCustomer()).isNull();

        customer.refunds(new HashSet<>(Set.of(refundBack)));
        assertThat(customer.getRefunds()).containsOnly(refundBack);
        assertThat(refundBack.getCustomer()).isEqualTo(customer);

        customer.setRefunds(new HashSet<>());
        assertThat(customer.getRefunds()).doesNotContain(refundBack);
        assertThat(refundBack.getCustomer()).isNull();
    }
}
