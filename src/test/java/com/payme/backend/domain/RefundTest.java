package com.payme.backend.domain;

import static com.payme.backend.domain.CustomerTestSamples.*;
import static com.payme.backend.domain.RefundTestSamples.*;
import static com.payme.backend.domain.TransactionTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.payme.backend.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class RefundTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Refund.class);
        Refund refund1 = getRefundSample1();
        Refund refund2 = new Refund();
        assertThat(refund1).isNotEqualTo(refund2);

        refund2.setId(refund1.getId());
        assertThat(refund1).isEqualTo(refund2);

        refund2 = getRefundSample2();
        assertThat(refund1).isNotEqualTo(refund2);
    }

    @Test
    void transactionTest() {
        Refund refund = getRefundRandomSampleGenerator();
        Transaction transactionBack = getTransactionRandomSampleGenerator();

        refund.setTransaction(transactionBack);
        assertThat(refund.getTransaction()).isEqualTo(transactionBack);

        refund.transaction(null);
        assertThat(refund.getTransaction()).isNull();
    }

    @Test
    void customerTest() {
        Refund refund = getRefundRandomSampleGenerator();
        Customer customerBack = getCustomerRandomSampleGenerator();

        refund.setCustomer(customerBack);
        assertThat(refund.getCustomer()).isEqualTo(customerBack);

        refund.customer(null);
        assertThat(refund.getCustomer()).isNull();
    }
}
