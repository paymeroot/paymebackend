package com.payme.backend.domain;

import static com.payme.backend.domain.CustomerTestSamples.*;
import static com.payme.backend.domain.RefundTestSamples.*;
import static com.payme.backend.domain.TransactionTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.payme.backend.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TransactionTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Transaction.class);
        Transaction transaction1 = getTransactionSample1();
        Transaction transaction2 = new Transaction();
        assertThat(transaction1).isNotEqualTo(transaction2);

        transaction2.setId(transaction1.getId());
        assertThat(transaction1).isEqualTo(transaction2);

        transaction2 = getTransactionSample2();
        assertThat(transaction1).isNotEqualTo(transaction2);
    }

    @Test
    void refundTest() {
        Transaction transaction = getTransactionRandomSampleGenerator();
        Refund refundBack = getRefundRandomSampleGenerator();

        transaction.setRefund(refundBack);
        assertThat(transaction.getRefund()).isEqualTo(refundBack);
        assertThat(refundBack.getTransaction()).isEqualTo(transaction);

        transaction.refund(null);
        assertThat(transaction.getRefund()).isNull();
        assertThat(refundBack.getTransaction()).isNull();
    }

    @Test
    void customerTest() {
        Transaction transaction = getTransactionRandomSampleGenerator();
        Customer customerBack = getCustomerRandomSampleGenerator();

        transaction.setCustomer(customerBack);
        assertThat(transaction.getCustomer()).isEqualTo(customerBack);

        transaction.customer(null);
        assertThat(transaction.getCustomer()).isNull();
    }
}
