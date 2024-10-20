package com.payme.backend.service.criteria;

import com.payme.backend.domain.enumeration.Status;
import com.payme.backend.domain.enumeration.Status;
import com.payme.backend.domain.enumeration.Status;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.payme.backend.domain.Transaction} entity. This class is used
 * in {@link com.payme.backend.web.rest.TransactionResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /transactions?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TransactionCriteria implements Serializable, Criteria {

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

    private InstantFilter transactionDate;

    private StringFilter senderNumber;

    private StringFilter senderWallet;

    private StringFilter receiverNumber;

    private StringFilter receiverWallet;

    private StatusFilter transactionStatus;

    private StatusFilter payInStatus;

    private StatusFilter payOutStatus;

    private FloatFilter amount;

    private StringFilter object;

    private StringFilter payInFailureReason;

    private StringFilter payOutFailureReason;

    private StringFilter senderCountryName;

    private StringFilter receiverCountryName;

    private LongFilter refundId;

    private LongFilter customerId;

    private Boolean distinct;

    public TransactionCriteria() {}

    public TransactionCriteria(TransactionCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.reference = other.optionalReference().map(StringFilter::copy).orElse(null);
        this.transactionDate = other.optionalTransactionDate().map(InstantFilter::copy).orElse(null);
        this.senderNumber = other.optionalSenderNumber().map(StringFilter::copy).orElse(null);
        this.senderWallet = other.optionalSenderWallet().map(StringFilter::copy).orElse(null);
        this.receiverNumber = other.optionalReceiverNumber().map(StringFilter::copy).orElse(null);
        this.receiverWallet = other.optionalReceiverWallet().map(StringFilter::copy).orElse(null);
        this.transactionStatus = other.optionalTransactionStatus().map(StatusFilter::copy).orElse(null);
        this.payInStatus = other.optionalPayInStatus().map(StatusFilter::copy).orElse(null);
        this.payOutStatus = other.optionalPayOutStatus().map(StatusFilter::copy).orElse(null);
        this.amount = other.optionalAmount().map(FloatFilter::copy).orElse(null);
        this.object = other.optionalObject().map(StringFilter::copy).orElse(null);
        this.payInFailureReason = other.optionalPayInFailureReason().map(StringFilter::copy).orElse(null);
        this.payOutFailureReason = other.optionalPayOutFailureReason().map(StringFilter::copy).orElse(null);
        this.senderCountryName = other.optionalSenderCountryName().map(StringFilter::copy).orElse(null);
        this.receiverCountryName = other.optionalReceiverCountryName().map(StringFilter::copy).orElse(null);
        this.refundId = other.optionalRefundId().map(LongFilter::copy).orElse(null);
        this.customerId = other.optionalCustomerId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public TransactionCriteria copy() {
        return new TransactionCriteria(this);
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

    public InstantFilter getTransactionDate() {
        return transactionDate;
    }

    public Optional<InstantFilter> optionalTransactionDate() {
        return Optional.ofNullable(transactionDate);
    }

    public InstantFilter transactionDate() {
        if (transactionDate == null) {
            setTransactionDate(new InstantFilter());
        }
        return transactionDate;
    }

    public void setTransactionDate(InstantFilter transactionDate) {
        this.transactionDate = transactionDate;
    }

    public StringFilter getSenderNumber() {
        return senderNumber;
    }

    public Optional<StringFilter> optionalSenderNumber() {
        return Optional.ofNullable(senderNumber);
    }

    public StringFilter senderNumber() {
        if (senderNumber == null) {
            setSenderNumber(new StringFilter());
        }
        return senderNumber;
    }

    public void setSenderNumber(StringFilter senderNumber) {
        this.senderNumber = senderNumber;
    }

    public StringFilter getSenderWallet() {
        return senderWallet;
    }

    public Optional<StringFilter> optionalSenderWallet() {
        return Optional.ofNullable(senderWallet);
    }

    public StringFilter senderWallet() {
        if (senderWallet == null) {
            setSenderWallet(new StringFilter());
        }
        return senderWallet;
    }

    public void setSenderWallet(StringFilter senderWallet) {
        this.senderWallet = senderWallet;
    }

    public StringFilter getReceiverNumber() {
        return receiverNumber;
    }

    public Optional<StringFilter> optionalReceiverNumber() {
        return Optional.ofNullable(receiverNumber);
    }

    public StringFilter receiverNumber() {
        if (receiverNumber == null) {
            setReceiverNumber(new StringFilter());
        }
        return receiverNumber;
    }

    public void setReceiverNumber(StringFilter receiverNumber) {
        this.receiverNumber = receiverNumber;
    }

    public StringFilter getReceiverWallet() {
        return receiverWallet;
    }

    public Optional<StringFilter> optionalReceiverWallet() {
        return Optional.ofNullable(receiverWallet);
    }

    public StringFilter receiverWallet() {
        if (receiverWallet == null) {
            setReceiverWallet(new StringFilter());
        }
        return receiverWallet;
    }

    public void setReceiverWallet(StringFilter receiverWallet) {
        this.receiverWallet = receiverWallet;
    }

    public StatusFilter getTransactionStatus() {
        return transactionStatus;
    }

    public Optional<StatusFilter> optionalTransactionStatus() {
        return Optional.ofNullable(transactionStatus);
    }

    public StatusFilter transactionStatus() {
        if (transactionStatus == null) {
            setTransactionStatus(new StatusFilter());
        }
        return transactionStatus;
    }

    public void setTransactionStatus(StatusFilter transactionStatus) {
        this.transactionStatus = transactionStatus;
    }

    public StatusFilter getPayInStatus() {
        return payInStatus;
    }

    public Optional<StatusFilter> optionalPayInStatus() {
        return Optional.ofNullable(payInStatus);
    }

    public StatusFilter payInStatus() {
        if (payInStatus == null) {
            setPayInStatus(new StatusFilter());
        }
        return payInStatus;
    }

    public void setPayInStatus(StatusFilter payInStatus) {
        this.payInStatus = payInStatus;
    }

    public StatusFilter getPayOutStatus() {
        return payOutStatus;
    }

    public Optional<StatusFilter> optionalPayOutStatus() {
        return Optional.ofNullable(payOutStatus);
    }

    public StatusFilter payOutStatus() {
        if (payOutStatus == null) {
            setPayOutStatus(new StatusFilter());
        }
        return payOutStatus;
    }

    public void setPayOutStatus(StatusFilter payOutStatus) {
        this.payOutStatus = payOutStatus;
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

    public StringFilter getObject() {
        return object;
    }

    public Optional<StringFilter> optionalObject() {
        return Optional.ofNullable(object);
    }

    public StringFilter object() {
        if (object == null) {
            setObject(new StringFilter());
        }
        return object;
    }

    public void setObject(StringFilter object) {
        this.object = object;
    }

    public StringFilter getPayInFailureReason() {
        return payInFailureReason;
    }

    public Optional<StringFilter> optionalPayInFailureReason() {
        return Optional.ofNullable(payInFailureReason);
    }

    public StringFilter payInFailureReason() {
        if (payInFailureReason == null) {
            setPayInFailureReason(new StringFilter());
        }
        return payInFailureReason;
    }

    public void setPayInFailureReason(StringFilter payInFailureReason) {
        this.payInFailureReason = payInFailureReason;
    }

    public StringFilter getPayOutFailureReason() {
        return payOutFailureReason;
    }

    public Optional<StringFilter> optionalPayOutFailureReason() {
        return Optional.ofNullable(payOutFailureReason);
    }

    public StringFilter payOutFailureReason() {
        if (payOutFailureReason == null) {
            setPayOutFailureReason(new StringFilter());
        }
        return payOutFailureReason;
    }

    public void setPayOutFailureReason(StringFilter payOutFailureReason) {
        this.payOutFailureReason = payOutFailureReason;
    }

    public StringFilter getSenderCountryName() {
        return senderCountryName;
    }

    public Optional<StringFilter> optionalSenderCountryName() {
        return Optional.ofNullable(senderCountryName);
    }

    public StringFilter senderCountryName() {
        if (senderCountryName == null) {
            setSenderCountryName(new StringFilter());
        }
        return senderCountryName;
    }

    public void setSenderCountryName(StringFilter senderCountryName) {
        this.senderCountryName = senderCountryName;
    }

    public StringFilter getReceiverCountryName() {
        return receiverCountryName;
    }

    public Optional<StringFilter> optionalReceiverCountryName() {
        return Optional.ofNullable(receiverCountryName);
    }

    public StringFilter receiverCountryName() {
        if (receiverCountryName == null) {
            setReceiverCountryName(new StringFilter());
        }
        return receiverCountryName;
    }

    public void setReceiverCountryName(StringFilter receiverCountryName) {
        this.receiverCountryName = receiverCountryName;
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
        final TransactionCriteria that = (TransactionCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(reference, that.reference) &&
            Objects.equals(transactionDate, that.transactionDate) &&
            Objects.equals(senderNumber, that.senderNumber) &&
            Objects.equals(senderWallet, that.senderWallet) &&
            Objects.equals(receiverNumber, that.receiverNumber) &&
            Objects.equals(receiverWallet, that.receiverWallet) &&
            Objects.equals(transactionStatus, that.transactionStatus) &&
            Objects.equals(payInStatus, that.payInStatus) &&
            Objects.equals(payOutStatus, that.payOutStatus) &&
            Objects.equals(amount, that.amount) &&
            Objects.equals(object, that.object) &&
            Objects.equals(payInFailureReason, that.payInFailureReason) &&
            Objects.equals(payOutFailureReason, that.payOutFailureReason) &&
            Objects.equals(senderCountryName, that.senderCountryName) &&
            Objects.equals(receiverCountryName, that.receiverCountryName) &&
            Objects.equals(refundId, that.refundId) &&
            Objects.equals(customerId, that.customerId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            reference,
            transactionDate,
            senderNumber,
            senderWallet,
            receiverNumber,
            receiverWallet,
            transactionStatus,
            payInStatus,
            payOutStatus,
            amount,
            object,
            payInFailureReason,
            payOutFailureReason,
            senderCountryName,
            receiverCountryName,
            refundId,
            customerId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TransactionCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalReference().map(f -> "reference=" + f + ", ").orElse("") +
            optionalTransactionDate().map(f -> "transactionDate=" + f + ", ").orElse("") +
            optionalSenderNumber().map(f -> "senderNumber=" + f + ", ").orElse("") +
            optionalSenderWallet().map(f -> "senderWallet=" + f + ", ").orElse("") +
            optionalReceiverNumber().map(f -> "receiverNumber=" + f + ", ").orElse("") +
            optionalReceiverWallet().map(f -> "receiverWallet=" + f + ", ").orElse("") +
            optionalTransactionStatus().map(f -> "transactionStatus=" + f + ", ").orElse("") +
            optionalPayInStatus().map(f -> "payInStatus=" + f + ", ").orElse("") +
            optionalPayOutStatus().map(f -> "payOutStatus=" + f + ", ").orElse("") +
            optionalAmount().map(f -> "amount=" + f + ", ").orElse("") +
            optionalObject().map(f -> "object=" + f + ", ").orElse("") +
            optionalPayInFailureReason().map(f -> "payInFailureReason=" + f + ", ").orElse("") +
            optionalPayOutFailureReason().map(f -> "payOutFailureReason=" + f + ", ").orElse("") +
            optionalSenderCountryName().map(f -> "senderCountryName=" + f + ", ").orElse("") +
            optionalReceiverCountryName().map(f -> "receiverCountryName=" + f + ", ").orElse("") +
            optionalRefundId().map(f -> "refundId=" + f + ", ").orElse("") +
            optionalCustomerId().map(f -> "customerId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
