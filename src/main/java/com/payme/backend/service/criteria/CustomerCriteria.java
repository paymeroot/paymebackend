package com.payme.backend.service.criteria;

import com.payme.backend.domain.enumeration.Status;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.payme.backend.domain.Customer} entity. This class is used
 * in {@link com.payme.backend.web.rest.CustomerResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /customers?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CustomerCriteria implements Serializable, Criteria {

    /**
     * Class for filtering Status
     */
    public static class StatusFilter extends Filter<Status> {

        public StatusFilter() {}

        public StatusFilter(StatusFilter filter) {
            super(filter);
        }

        @Override
        public StatusFilter copy() {
            return new StatusFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter lastname;

    private StringFilter firstname;

    private StringFilter phone;

    private StringFilter countryCode;

    private StatusFilter statusKyc;

    private LongFilter profileId;

    private LongFilter kycId;

    private LongFilter countryId;

    private LongFilter transactionId;

    private LongFilter refundId;

    private Boolean distinct;

    public CustomerCriteria() {}

    public CustomerCriteria(CustomerCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.lastname = other.optionalLastname().map(StringFilter::copy).orElse(null);
        this.firstname = other.optionalFirstname().map(StringFilter::copy).orElse(null);
        this.phone = other.optionalPhone().map(StringFilter::copy).orElse(null);
        this.countryCode = other.optionalCountryCode().map(StringFilter::copy).orElse(null);
        this.statusKyc = other.optionalStatusKyc().map(StatusFilter::copy).orElse(null);
        this.profileId = other.optionalProfileId().map(LongFilter::copy).orElse(null);
        this.kycId = other.optionalKycId().map(LongFilter::copy).orElse(null);
        this.countryId = other.optionalCountryId().map(LongFilter::copy).orElse(null);
        this.transactionId = other.optionalTransactionId().map(LongFilter::copy).orElse(null);
        this.refundId = other.optionalRefundId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public CustomerCriteria copy() {
        return new CustomerCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public Optional<LongFilter> optionalId() {
        return Optional.ofNullable(id);
    }

    public LongFilter id() {
        if (id == null) {
            setId(new LongFilter());
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getLastname() {
        return lastname;
    }

    public Optional<StringFilter> optionalLastname() {
        return Optional.ofNullable(lastname);
    }

    public StringFilter lastname() {
        if (lastname == null) {
            setLastname(new StringFilter());
        }
        return lastname;
    }

    public void setLastname(StringFilter lastname) {
        this.lastname = lastname;
    }

    public StringFilter getFirstname() {
        return firstname;
    }

    public Optional<StringFilter> optionalFirstname() {
        return Optional.ofNullable(firstname);
    }

    public StringFilter firstname() {
        if (firstname == null) {
            setFirstname(new StringFilter());
        }
        return firstname;
    }

    public void setFirstname(StringFilter firstname) {
        this.firstname = firstname;
    }

    public StringFilter getPhone() {
        return phone;
    }

    public Optional<StringFilter> optionalPhone() {
        return Optional.ofNullable(phone);
    }

    public StringFilter phone() {
        if (phone == null) {
            setPhone(new StringFilter());
        }
        return phone;
    }

    public void setPhone(StringFilter phone) {
        this.phone = phone;
    }

    public StringFilter getCountryCode() {
        return countryCode;
    }

    public Optional<StringFilter> optionalCountryCode() {
        return Optional.ofNullable(countryCode);
    }

    public StringFilter countryCode() {
        if (countryCode == null) {
            setCountryCode(new StringFilter());
        }
        return countryCode;
    }

    public void setCountryCode(StringFilter countryCode) {
        this.countryCode = countryCode;
    }

    public StatusFilter getStatusKyc() {
        return statusKyc;
    }

    public Optional<StatusFilter> optionalStatusKyc() {
        return Optional.ofNullable(statusKyc);
    }

    public StatusFilter statusKyc() {
        if (statusKyc == null) {
            setStatusKyc(new StatusFilter());
        }
        return statusKyc;
    }

    public void setStatusKyc(StatusFilter statusKyc) {
        this.statusKyc = statusKyc;
    }

    public LongFilter getProfileId() {
        return profileId;
    }

    public Optional<LongFilter> optionalProfileId() {
        return Optional.ofNullable(profileId);
    }

    public LongFilter profileId() {
        if (profileId == null) {
            setProfileId(new LongFilter());
        }
        return profileId;
    }

    public void setProfileId(LongFilter profileId) {
        this.profileId = profileId;
    }

    public LongFilter getKycId() {
        return kycId;
    }

    public Optional<LongFilter> optionalKycId() {
        return Optional.ofNullable(kycId);
    }

    public LongFilter kycId() {
        if (kycId == null) {
            setKycId(new LongFilter());
        }
        return kycId;
    }

    public void setKycId(LongFilter kycId) {
        this.kycId = kycId;
    }

    public LongFilter getCountryId() {
        return countryId;
    }

    public Optional<LongFilter> optionalCountryId() {
        return Optional.ofNullable(countryId);
    }

    public LongFilter countryId() {
        if (countryId == null) {
            setCountryId(new LongFilter());
        }
        return countryId;
    }

    public void setCountryId(LongFilter countryId) {
        this.countryId = countryId;
    }

    public LongFilter getTransactionId() {
        return transactionId;
    }

    public Optional<LongFilter> optionalTransactionId() {
        return Optional.ofNullable(transactionId);
    }

    public LongFilter transactionId() {
        if (transactionId == null) {
            setTransactionId(new LongFilter());
        }
        return transactionId;
    }

    public void setTransactionId(LongFilter transactionId) {
        this.transactionId = transactionId;
    }

    public LongFilter getRefundId() {
        return refundId;
    }

    public Optional<LongFilter> optionalRefundId() {
        return Optional.ofNullable(refundId);
    }

    public LongFilter refundId() {
        if (refundId == null) {
            setRefundId(new LongFilter());
        }
        return refundId;
    }

    public void setRefundId(LongFilter refundId) {
        this.refundId = refundId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public Optional<Boolean> optionalDistinct() {
        return Optional.ofNullable(distinct);
    }

    public Boolean distinct() {
        if (distinct == null) {
            setDistinct(true);
        }
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final CustomerCriteria that = (CustomerCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(lastname, that.lastname) &&
            Objects.equals(firstname, that.firstname) &&
            Objects.equals(phone, that.phone) &&
            Objects.equals(countryCode, that.countryCode) &&
            Objects.equals(statusKyc, that.statusKyc) &&
            Objects.equals(profileId, that.profileId) &&
            Objects.equals(kycId, that.kycId) &&
            Objects.equals(countryId, that.countryId) &&
            Objects.equals(transactionId, that.transactionId) &&
            Objects.equals(refundId, that.refundId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            lastname,
            firstname,
            phone,
            countryCode,
            statusKyc,
            profileId,
            kycId,
            countryId,
            transactionId,
            refundId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CustomerCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalLastname().map(f -> "lastname=" + f + ", ").orElse("") +
            optionalFirstname().map(f -> "firstname=" + f + ", ").orElse("") +
            optionalPhone().map(f -> "phone=" + f + ", ").orElse("") +
            optionalCountryCode().map(f -> "countryCode=" + f + ", ").orElse("") +
            optionalStatusKyc().map(f -> "statusKyc=" + f + ", ").orElse("") +
            optionalProfileId().map(f -> "profileId=" + f + ", ").orElse("") +
            optionalKycId().map(f -> "kycId=" + f + ", ").orElse("") +
            optionalCountryId().map(f -> "countryId=" + f + ", ").orElse("") +
            optionalTransactionId().map(f -> "transactionId=" + f + ", ").orElse("") +
            optionalRefundId().map(f -> "refundId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
