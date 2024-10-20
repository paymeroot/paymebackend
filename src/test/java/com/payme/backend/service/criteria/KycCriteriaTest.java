package com.payme.backend.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class KycCriteriaTest {

    @Test
    void newKycCriteriaHasAllFiltersNullTest() {
        var kycCriteria = new KycCriteria();
        assertThat(kycCriteria).is(criteriaFiltersAre(filter -> filter == null));
    }

    @Test
    void kycCriteriaFluentMethodsCreatesFiltersTest() {
        var kycCriteria = new KycCriteria();

        setAllFilters(kycCriteria);

        assertThat(kycCriteria).is(criteriaFiltersAre(filter -> filter != null));
    }

    @Test
    void kycCriteriaCopyCreatesNullFilterTest() {
        var kycCriteria = new KycCriteria();
        var copy = kycCriteria.copy();

        assertThat(kycCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter == null)),
            criteria -> assertThat(criteria).isEqualTo(kycCriteria)
        );
    }

    @Test
    void kycCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var kycCriteria = new KycCriteria();
        setAllFilters(kycCriteria);

        var copy = kycCriteria.copy();

        assertThat(kycCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter != null)),
            criteria -> assertThat(criteria).isEqualTo(kycCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var kycCriteria = new KycCriteria();

        assertThat(kycCriteria).hasToString("KycCriteria{}");
    }

    private static void setAllFilters(KycCriteria kycCriteria) {
        kycCriteria.id();
        kycCriteria.reference();
        kycCriteria.typePiece();
        kycCriteria.numberPiece();
        kycCriteria.photoPieceUrl();
        kycCriteria.photoSelfieUrl();
        kycCriteria.customerId();
        kycCriteria.distinct();
    }

    private static Condition<KycCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getReference()) &&
                condition.apply(criteria.getTypePiece()) &&
                condition.apply(criteria.getNumberPiece()) &&
                condition.apply(criteria.getPhotoPieceUrl()) &&
                condition.apply(criteria.getPhotoSelfieUrl()) &&
                condition.apply(criteria.getCustomerId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<KycCriteria> copyFiltersAre(KycCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getReference(), copy.getReference()) &&
                condition.apply(criteria.getTypePiece(), copy.getTypePiece()) &&
                condition.apply(criteria.getNumberPiece(), copy.getNumberPiece()) &&
                condition.apply(criteria.getPhotoPieceUrl(), copy.getPhotoPieceUrl()) &&
                condition.apply(criteria.getPhotoSelfieUrl(), copy.getPhotoSelfieUrl()) &&
                condition.apply(criteria.getCustomerId(), copy.getCustomerId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
