package com.payme.backend.service.dto;

import com.payme.backend.domain.enumeration.Status;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.payme.backend.domain.Transaction} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TransactionDTO implements Serializable {

    private Long id;

    @NotNull
    private String reference;

    @NotNull
    private Instant transactionDate;

    @NotNull
    private String senderNumber;

    @NotNull
    private String senderWallet;

    @NotNull
    private String receiverNumber;

    @NotNull
    private String receiverWallet;

    @NotNull
    private Status transactionStatus;

    @NotNull
    private Status payInStatus;

    @NotNull
    private Status payOutStatus;

    @NotNull
    private Float amount;

    private String object;

    private String payInFailureReason;

    private String payOutFailureReason;

    private String senderCountryName;

    private String receiverCountryName;

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

    public Instant getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(Instant transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getSenderNumber() {
        return senderNumber;
    }

    public void setSenderNumber(String senderNumber) {
        this.senderNumber = senderNumber;
    }

    public String getSenderWallet() {
        return senderWallet;
    }

    public void setSenderWallet(String senderWallet) {
        this.senderWallet = senderWallet;
    }

    public String getReceiverNumber() {
        return receiverNumber;
    }

    public void setReceiverNumber(String receiverNumber) {
        this.receiverNumber = receiverNumber;
    }

    public String getReceiverWallet() {
        return receiverWallet;
    }

    public void setReceiverWallet(String receiverWallet) {
        this.receiverWallet = receiverWallet;
    }

    public Status getTransactionStatus() {
        return transactionStatus;
    }

    public void setTransactionStatus(Status transactionStatus) {
        this.transactionStatus = transactionStatus;
    }

    public Status getPayInStatus() {
        return payInStatus;
    }

    public void setPayInStatus(Status payInStatus) {
        this.payInStatus = payInStatus;
    }

    public Status getPayOutStatus() {
        return payOutStatus;
    }

    public void setPayOutStatus(Status payOutStatus) {
        this.payOutStatus = payOutStatus;
    }

    public Float getAmount() {
        return amount;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public String getPayInFailureReason() {
        return payInFailureReason;
    }

    public void setPayInFailureReason(String payInFailureReason) {
        this.payInFailureReason = payInFailureReason;
    }

    public String getPayOutFailureReason() {
        return payOutFailureReason;
    }

    public void setPayOutFailureReason(String payOutFailureReason) {
        this.payOutFailureReason = payOutFailureReason;
    }

    public String getSenderCountryName() {
        return senderCountryName;
    }

    public void setSenderCountryName(String senderCountryName) {
        this.senderCountryName = senderCountryName;
    }

    public String getReceiverCountryName() {
        return receiverCountryName;
    }

    public void setReceiverCountryName(String receiverCountryName) {
        this.receiverCountryName = receiverCountryName;
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
        if (!(o instanceof TransactionDTO)) {
            return false;
        }

        TransactionDTO transactionDTO = (TransactionDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, transactionDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TransactionDTO{" +
            "id=" + getId() +
            ", reference='" + getReference() + "'" +
            ", transactionDate='" + getTransactionDate() + "'" +
            ", senderNumber='" + getSenderNumber() + "'" +
            ", senderWallet='" + getSenderWallet() + "'" +
            ", receiverNumber='" + getReceiverNumber() + "'" +
            ", receiverWallet='" + getReceiverWallet() + "'" +
            ", transactionStatus='" + getTransactionStatus() + "'" +
            ", payInStatus='" + getPayInStatus() + "'" +
            ", payOutStatus='" + getPayOutStatus() + "'" +
            ", amount=" + getAmount() +
            ", object='" + getObject() + "'" +
            ", payInFailureReason='" + getPayInFailureReason() + "'" +
            ", payOutFailureReason='" + getPayOutFailureReason() + "'" +
            ", senderCountryName='" + getSenderCountryName() + "'" +
            ", receiverCountryName='" + getReceiverCountryName() + "'" +
            ", customer=" + getCustomer() +
            "}";
    }
}
