package com.payme.backend.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class RefundCriteriaTest {

    @Test
    void newRefundCriteriaHasAllFiltersNullTest() {
        var refundCriteria = new RefundCriteria();
        assertThat(refundCriteria).is(criteriaFiltersAre(filter -> filter == null));
    }

    @Test
    void refundCriteriaFluentMethodsCreatesFiltersTest() {
        var refundCriteria = new RefundCriteria();

        setAllFilters(refundCriteria);

        assertThat(refundCriteria).is(criteriaFiltersAre(filter -> filter != null));
    }

    @Test
    void refundCriteriaCopyCreatesNullFilterTest() {
        var refundCriteria = new RefundCriteria();
        var copy = refundCriteria.copy();

        assertThat(refundCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter == null)),
            criteria -> assertThat(criteria).isEqualTo(refundCriteria)
        );
    }

    @Test
    void refundCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var refundCriteria = new RefundCriteria();
        setAllFilters(refundCriteria);

        var copy = refundCriteria.copy();

        assertThat(refundCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter != null)),
            criteria -> assertThat(criteria).isEqualTo(refundCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var refundCriteria = new RefundCriteria();

        assertThat(refundCriteria).hasToString("RefundCriteria{}");
    }

    private static void setAllFilters(RefundCriteria refundCriteria) {
        refundCriteria.id();
        refundCriteria.reference();
        refundCriteria.transactionRef();
        refundCriteria.refundDate();
        refundCriteria.refundStatus();
        refundCriteria.amount();
        refundCriteria.transactionId();
        refundCriteria.customerId();
        refundCriteria.distinct();
    }

    private static Condition<RefundCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getReference()) &&
                condition.apply(criteria.getTransactionRef()) &&
                condition.apply(criteria.getRefundDate()) &&
                condition.apply(criteria.getRefundStatus()) &&
                condition.apply(criteria.getAmount()) &&
                condition.apply(criteria.getTransactionId()) &&
                condition.apply(criteria.getCustomerId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<RefundCriteria> copyFiltersAre(RefundCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getReference(), copy.getReference()) &&
                condition.apply(criteria.getTransactionRef(), copy.getTransactionRef()) &&
                condition.apply(criteria.getRefundDate(), copy.getRefundDate()) &&
                condition.apply(criteria.getRefundStatus(), copy.getRefundStatus()) &&
                condition.apply(criteria.getAmount(), copy.getAmount()) &&
                condition.apply(criteria.getTransactionId(), copy.getTransactionId()) &&
                condition.apply(criteria.getCustomerId(), copy.getCustomerId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
