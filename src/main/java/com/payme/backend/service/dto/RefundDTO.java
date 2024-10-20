package com.payme.backend.service.dto;

import com.payme.backend.domain.enumeration.Status;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.payme.backend.domain.Refund} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class RefundDTO implements Serializable {

    private Long id;

    @NotNull
    private String reference;

    @NotNull
    private String transactionRef;

    @NotNull
    private Instant refundDate;

    private Status refundStatus;

    @NotNull
    private Float amount;

    private TransactionDTO transaction;

    private CustomerDTO customer;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getTransactionRef() {
        return transactionRef;
    }

    public void setTransactionRef(String transactionRef) {
        this.transactionRef = transactionRef;
    }

    public Instant getRefundDate() {
        return refundDate;
    }

    public void setRefundDate(Instant refundDate) {
        this.refundDate = refundDate;
    }

    public Status getRefundStatus() {
        return refundStatus;
    }

    public void setRefundStatus(Status refundStatus) {
        this.refundStatus = refundStatus;
    }

    public Float getAmount() {
        return amount;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
    }

    public TransactionDTO getTransaction() {
        return transaction;
    }

    public void setTransaction(TransactionDTO transaction) {
        this.transaction = transaction;
    }

    public CustomerDTO getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerDTO customer) {
        this.customer = customer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RefundDTO)) {
            return false;
        }

        RefundDTO refundDTO = (RefundDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, refundDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "RefundDTO{" +
            "id=" + getId() +
            ", reference='" + getReference() + "'" +
            ", transactionRef='" + getTransactionRef() + "'" +
            ", refundDate='" + getRefundDate() + "'" +
            ", refundStatus='" + getRefundStatus() + "'" +
            ", amount=" + getAmount() +
            ", transaction=" + getTransaction() +
            ", customer=" + getCustomer() +
            "}";
    }
}
