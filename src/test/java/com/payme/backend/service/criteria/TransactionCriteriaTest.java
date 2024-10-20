package com.payme.backend.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class TransactionCriteriaTest {

    @Test
    void newTransactionCriteriaHasAllFiltersNullTest() {
        var transactionCriteria = new TransactionCriteria();
        assertThat(transactionCriteria).is(criteriaFiltersAre(filter -> filter == null));
    }

    @Test
    void transactionCriteriaFluentMethodsCreatesFiltersTest() {
        var transactionCriteria = new TransactionCriteria();

        setAllFilters(transactionCriteria);

        assertThat(transactionCriteria).is(criteriaFiltersAre(filter -> filter != null));
    }

    @Test
    void transactionCriteriaCopyCreatesNullFilterTest() {
        var transactionCriteria = new TransactionCriteria();
        var copy = transactionCriteria.copy();

        assertThat(transactionCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter == null)),
            criteria -> assertThat(criteria).isEqualTo(transactionCriteria)
        );
    }

    @Test
    void transactionCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var transactionCriteria = new TransactionCriteria();
        setAllFilters(transactionCriteria);

        var copy = transactionCriteria.copy();

        assertThat(transactionCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter != null)),
            criteria -> assertThat(criteria).isEqualTo(transactionCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var transactionCriteria = new TransactionCriteria();

        assertThat(transactionCriteria).hasToString("TransactionCriteria{}");
    }

    private static void setAllFilters(TransactionCriteria transactionCriteria) {
        transactionCriteria.id();
        transactionCriteria.reference();
        transactionCriteria.transactionDate();
        transactionCriteria.senderNumber();
        transactionCriteria.senderWallet();
        transactionCriteria.receiverNumber();
        transactionCriteria.receiverWallet();
        transactionCriteria.transactionStatus();
        transactionCriteria.payInStatus();
        transactionCriteria.payOutStatus();
        transactionCriteria.amount();
        transactionCriteria.object();
        transactionCriteria.payInFailureReason();
        transactionCriteria.payOutFailureReason();
        transactionCriteria.senderCountryName();
        transactionCriteria.receiverCountryName();
        transactionCriteria.refundId();
        transactionCriteria.customerId();
        transactionCriteria.distinct();
    }

    private static Condition<TransactionCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getReference()) &&
                condition.apply(criteria.getTransactionDate()) &&
                condition.apply(criteria.getSenderNumber()) &&
                condition.apply(criteria.getSenderWallet()) &&
                condition.apply(criteria.getReceiverNumber()) &&
                condition.apply(criteria.getReceiverWallet()) &&
                condition.apply(criteria.getTransactionStatus()) &&
                condition.apply(criteria.getPayInStatus()) &&
                condition.apply(criteria.getPayOutStatus()) &&
                condition.apply(criteria.getAmount()) &&
                condition.apply(criteria.getObject()) &&
                condition.apply(criteria.getPayInFailureReason()) &&
                condition.apply(criteria.getPayOutFailureReason()) &&
                condition.apply(criteria.getSenderCountryName()) &&
                condition.apply(criteria.getReceiverCountryName()) &&
                condition.apply(criteria.getRefundId()) &&
                condition.apply(criteria.getCustomerId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<TransactionCriteria> copyFiltersAre(TransactionCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getReference(), copy.getReference()) &&
                condition.apply(criteria.getTransactionDate(), copy.getTransactionDate()) &&
                condition.apply(criteria.getSenderNumber(), copy.getSenderNumber()) &&
                condition.apply(criteria.getSenderWallet(), copy.getSenderWallet()) &&
                condition.apply(criteria.getReceiverNumber(), copy.getReceiverNumber()) &&
                condition.apply(criteria.getReceiverWallet(), copy.getReceiverWallet()) &&
                condition.apply(criteria.getTransactionStatus(), copy.getTransactionStatus()) &&
                condition.apply(criteria.getPayInStatus(), copy.getPayInStatus()) &&
                condition.apply(criteria.getPayOutStatus(), copy.getPayOutStatus()) &&
                condition.apply(criteria.getAmount(), copy.getAmount()) &&
                condition.apply(criteria.getObject(), copy.getObject()) &&
                condition.apply(criteria.getPayInFailureReason(), copy.getPayInFailureReason()) &&
                condition.apply(criteria.getPayOutFailureReason(), copy.getPayOutFailureReason()) &&
                condition.apply(criteria.getSenderCountryName(), copy.getSenderCountryName()) &&
                condition.apply(criteria.getReceiverCountryName(), copy.getReceiverCountryName()) &&
                condition.apply(criteria.getRefundId(), copy.getRefundId()) &&
                condition.apply(criteria.getCustomerId(), copy.getCustomerId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
