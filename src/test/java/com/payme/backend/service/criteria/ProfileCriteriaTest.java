package com.payme.backend.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class ProfileCriteriaTest {

    @Test
    void newProfileCriteriaHasAllFiltersNullTest() {
        var profileCriteria = new ProfileCriteria();
        assertThat(profileCriteria).is(criteriaFiltersAre(filter -> filter == null));
    }

    @Test
    void profileCriteriaFluentMethodsCreatesFiltersTest() {
        var profileCriteria = new ProfileCriteria();

        setAllFilters(profileCriteria);

        assertThat(profileCriteria).is(criteriaFiltersAre(filter -> filter != null));
    }

    @Test
    void profileCriteriaCopyCreatesNullFilterTest() {
        var profileCriteria = new ProfileCriteria();
        var copy = profileCriteria.copy();

        assertThat(profileCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter == null)),
            criteria -> assertThat(criteria).isEqualTo(profileCriteria)
        );
    }

    @Test
    void profileCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var profileCriteria = new ProfileCriteria();
        setAllFilters(profileCriteria);

        var copy = profileCriteria.copy();

        assertThat(profileCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter != null)),
            criteria -> assertThat(criteria).isEqualTo(profileCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var profileCriteria = new ProfileCriteria();

        assertThat(profileCriteria).hasToString("ProfileCriteria{}");
    }

    private static void setAllFilters(ProfileCriteria profileCriteria) {
        profileCriteria.id();
        profileCriteria.email();
        profileCriteria.address();
        profileCriteria.ville();
        profileCriteria.countryCode();
        profileCriteria.avatarUrl();
        profileCriteria.genre();
        profileCriteria.customerId();
        profileCriteria.distinct();
    }

    private static Condition<ProfileCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getEmail()) &&
                condition.apply(criteria.getAddress()) &&
                condition.apply(criteria.getVille()) &&
                condition.apply(criteria.getCountryCode()) &&
                condition.apply(criteria.getAvatarUrl()) &&
                condition.apply(criteria.getGenre()) &&
                condition.apply(criteria.getCustomerId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<ProfileCriteria> copyFiltersAre(ProfileCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getEmail(), copy.getEmail()) &&
                condition.apply(criteria.getAddress(), copy.getAddress()) &&
                condition.apply(criteria.getVille(), copy.getVille()) &&
                condition.apply(criteria.getCountryCode(), copy.getCountryCode()) &&
                condition.apply(criteria.getAvatarUrl(), copy.getAvatarUrl()) &&
                condition.apply(criteria.getGenre(), copy.getGenre()) &&
                condition.apply(criteria.getCustomerId(), copy.getCustomerId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
