package com.payme.backend.service.criteria;

import com.payme.backend.domain.enumeration.Status;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.payme.backend.domain.Refund} entity. This class is used
 * in {@link com.payme.backend.web.rest.RefundResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /refunds?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class RefundCriteria implements Serializable, Criteria {

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

    private StringFilter reference;

    private StringFilter transactionRef;

    private InstantFilter refundDate;

    private StatusFilter refundStatus;

    private FloatFilter amount;

    private LongFilter transactionId;

    private LongFilter customerId;

    private Boolean distinct;

    public RefundCriteria() {}

    public RefundCriteria(RefundCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.reference = other.optionalReference().map(StringFilter::copy).orElse(null);
        this.transactionRef = other.optionalTransactionRef().map(StringFilter::copy).orElse(null);
        this.refundDate = other.optionalRefundDate().map(InstantFilter::copy).orElse(null);
        this.refundStatus = other.optionalRefundStatus().map(StatusFilter::copy).orElse(null);
        this.amount = other.optionalAmount().map(FloatFilter::copy).orElse(null);
        this.transactionId = other.optionalTransactionId().map(LongFilter::copy).orElse(null);
        this.customerId = other.optionalCustomerId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public RefundCriteria copy() {
        return new RefundCriteria(this);
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

    public StringFilter getReference() {
        return reference;
    }

    public Optional<StringFilter> optionalReference() {
        return Optional.ofNullable(reference);
    }

    public StringFilter reference() {
        if (reference == null) {
            setReference(new StringFilter());
        }
        return reference;
    }

    public void setReference(StringFilter reference) {
        this.reference = reference;
    }

    public StringFilter getTransactionRef() {
        return transactionRef;
    }

    public Optional<StringFilter> optionalTransactionRef() {
        return Optional.ofNullable(transactionRef);
    }

    public StringFilter transactionRef() {
        if (transactionRef == null) {
            setTransactionRef(new StringFilter());
        }
        return transactionRef;
    }

    public void setTransactionRef(StringFilter transactionRef) {
        this.transactionRef = transactionRef;
    }

    public InstantFilter getRefundDate() {
        return refundDate;
    }

    public Optional<InstantFilter> optionalRefundDate() {
        return Optional.ofNullable(refundDate);
    }

    public InstantFilter refundDate() {
        if (refundDate == null) {
            setRefundDate(new InstantFilter());
        }
        return refundDate;
    }

    public void setRefundDate(InstantFilter refundDate) {
        this.refundDate = refundDate;
    }

    public StatusFilter getRefundStatus() {
        return refundStatus;
    }

    public Optional<StatusFilter> optionalRefundStatus() {
        return Optional.ofNullable(refundStatus);
    }

    public StatusFilter refundStatus() {
        if (refundStatus == null) {
            setRefundStatus(new StatusFilter());
        }
        return refundStatus;
    }

    public void setRefundStatus(StatusFilter refundStatus) {
        this.refundStatus = refundStatus;
    }

    public FloatFilter getAmount() {
        return amount;
    }

    public Optional<FloatFilter> optionalAmount() {
        return Optional.ofNullable(amount);
    }

    public FloatFilter amount() {
        if (amount == null) {
            setAmount(new FloatFilter());
        }
        return amount;
    }

    public void setAmount(FloatFilter amount) {
        this.amount = amount;
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

    public LongFilter getCustomerId() {
        return customerId;
    }

    public Optional<LongFilter> optionalCustomerId() {
        return Optional.ofNullable(customerId);
    }

    public LongFilter customerId() {
        if (customerId == null) {
            setCustomerId(new LongFilter());
        }
        return customerId;
    }

    public void setCustomerId(LongFilter customerId) {
        this.customerId = customerId;
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
        final RefundCriteria that = (RefundCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(reference, that.reference) &&
            Objects.equals(transactionRef, that.transactionRef) &&
            Objects.equals(refundDate, that.refundDate) &&
            Objects.equals(refundStatus, that.refundStatus) &&
            Objects.equals(amount, that.amount) &&
            Objects.equals(transactionId, that.transactionId) &&
            Objects.equals(customerId, that.customerId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, reference, transactionRef, refundDate, refundStatus, amount, transactionId, customerId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "RefundCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalReference().map(f -> "reference=" + f + ", ").orElse("") +
            optionalTransactionRef().map(f -> "transactionRef=" + f + ", ").orElse("") +
            optionalRefundDate().map(f -> "refundDate=" + f + ", ").orElse("") +
            optionalRefundStatus().map(f -> "refundStatus=" + f + ", ").orElse("") +
            optionalAmount().map(f -> "amount=" + f + ", ").orElse("") +
            optionalTransactionId().map(f -> "transactionId=" + f + ", ").orElse("") +
            optionalCustomerId().map(f -> "customerId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
