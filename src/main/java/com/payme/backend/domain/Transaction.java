package com.payme.backend.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.payme.backend.domain.enumeration.Status;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Transaction.
 */
@Entity
@Table(name = "transaction")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "transaction")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Transaction implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "reference", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String reference;

    @NotNull
    @Column(name = "transaction_date", nullable = false)
    private Instant transactionDate;

    @NotNull
    @Column(name = "sender_number", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String senderNumber;

    @NotNull
    @Column(name = "sender_wallet", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String senderWallet;

    @NotNull
    @Column(name = "receiver_number", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String receiverNumber;

    @NotNull
    @Column(name = "receiver_wallet", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String receiverWallet;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_status", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Keyword)
    private Status transactionStatus;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "pay_in_status", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Keyword)
    private Status payInStatus;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "pay_out_status", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Keyword)
    private Status payOutStatus;

    @NotNull
    @Column(name = "amount", nullable = false)
    private Float amount;

    @Column(name = "object")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String object;

    @Column(name = "pay_in_failure_reason")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String payInFailureReason;

    @Column(name = "pay_out_failure_reason")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String payOutFailureReason;

    @Column(name = "sender_country_name")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String senderCountryName;

    @Column(name = "receiver_country_name")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String receiverCountryName;

    @JsonIgnoreProperties(value = { "transaction", "customer" }, allowSetters = true)
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "transaction")
    @org.springframework.data.annotation.Transient
    private Refund refund;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "profile", "kyc", "country", "transactions", "refunds" }, allowSetters = true)
    private Customer customer;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Transaction id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReference() {
        return this.reference;
    }

    public Transaction reference(String reference) {
        this.setReference(reference);
        return this;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public Instant getTransactionDate() {
        return this.transactionDate;
    }

    public Transaction transactionDate(Instant transactionDate) {
        this.setTransactionDate(transactionDate);
        return this;
    }

    public void setTransactionDate(Instant transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getSenderNumber() {
        return this.senderNumber;
    }

    public Transaction senderNumber(String senderNumber) {
        this.setSenderNumber(senderNumber);
        return this;
    }

    public void setSenderNumber(String senderNumber) {
        this.senderNumber = senderNumber;
    }

    public String getSenderWallet() {
        return this.senderWallet;
    }

    public Transaction senderWallet(String senderWallet) {
        this.setSenderWallet(senderWallet);
        return this;
    }

    public void setSenderWallet(String senderWallet) {
        this.senderWallet = senderWallet;
    }

    public String getReceiverNumber() {
        return this.receiverNumber;
    }

    public Transaction receiverNumber(String receiverNumber) {
        this.setReceiverNumber(receiverNumber);
        return this;
    }

    public void setReceiverNumber(String receiverNumber) {
        this.receiverNumber = receiverNumber;
    }

    public String getReceiverWallet() {
        return this.receiverWallet;
    }

    public Transaction receiverWallet(String receiverWallet) {
        this.setReceiverWallet(receiverWallet);
        return this;
    }

    public void setReceiverWallet(String receiverWallet) {
        this.receiverWallet = receiverWallet;
    }

    public Status getTransactionStatus() {
        return this.transactionStatus;
    }

    public Transaction transactionStatus(Status transactionStatus) {
        this.setTransactionStatus(transactionStatus);
        return this;
    }

    public void setTransactionStatus(Status transactionStatus) {
        this.transactionStatus = transactionStatus;
    }

    public Status getPayInStatus() {
        return this.payInStatus;
    }

    public Transaction payInStatus(Status payInStatus) {
        this.setPayInStatus(payInStatus);
        return this;
    }

    public void setPayInStatus(Status payInStatus) {
        this.payInStatus = payInStatus;
    }

    public Status getPayOutStatus() {
        return this.payOutStatus;
    }

    public Transaction payOutStatus(Status payOutStatus) {
        this.setPayOutStatus(payOutStatus);
        return this;
    }

    public void setPayOutStatus(Status payOutStatus) {
        this.payOutStatus = payOutStatus;
    }

    public Float getAmount() {
        return this.amount;
    }

    public Transaction amount(Float amount) {
        this.setAmount(amount);
        return this;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
    }

    public String getObject() {
        return this.object;
    }

    public Transaction object(String object) {
        this.setObject(object);
        return this;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public String getPayInFailureReason() {
        return this.payInFailureReason;
    }

    public Transaction payInFailureReason(String payInFailureReason) {
        this.setPayInFailureReason(payInFailureReason);
        return this;
    }

    public void setPayInFailureReason(String payInFailureReason) {
        this.payInFailureReason = payInFailureReason;
    }

    public String getPayOutFailureReason() {
        return this.payOutFailureReason;
    }

    public Transaction payOutFailureReason(String payOutFailureReason) {
        this.setPayOutFailureReason(payOutFailureReason);
        return this;
    }

    public void setPayOutFailureReason(String payOutFailureReason) {
        this.payOutFailureReason = payOutFailureReason;
    }

    public String getSenderCountryName() {
        return this.senderCountryName;
    }

    public Transaction senderCountryName(String senderCountryName) {
        this.setSenderCountryName(senderCountryName);
        return this;
    }

    public void setSenderCountryName(String senderCountryName) {
        this.senderCountryName = senderCountryName;
    }

    public String getReceiverCountryName() {
        return this.receiverCountryName;
    }

    public Transaction receiverCountryName(String receiverCountryName) {
        this.setReceiverCountryName(receiverCountryName);
        return this;
    }

    public void setReceiverCountryName(String receiverCountryName) {
        this.receiverCountryName = receiverCountryName;
    }

    public Refund getRefund() {
        return this.refund;
    }

    public void setRefund(Refund refund) {
        if (this.refund != null) {
            this.refund.setTransaction(null);
        }
        if (refund != null) {
            refund.setTransaction(this);
        }
        this.refund = refund;
    }

    public Transaction refund(Refund refund) {
        this.setRefund(refund);
        return this;
    }

    public Customer getCustomer() {
        return this.customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Transaction customer(Customer customer) {
        this.setCustomer(customer);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Transaction)) {
            return false;
        }
        return getId() != null && getId().equals(((Transaction) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Transaction{" +
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
            "}";
    }
}
