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
 * A Refund.
 */
@Entity
@Table(name = "refund")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "refund")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Refund implements Serializable {

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
    @Column(name = "transaction_ref", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String transactionRef;

    @NotNull
    @Column(name = "refund_date", nullable = false)
    private Instant refundDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "refund_status")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Keyword)
    private Status refundStatus;

    @NotNull
    @Column(name = "amount", nullable = false)
    private Float amount;

    @JsonIgnoreProperties(value = { "refund", "customer" }, allowSetters = true)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(unique = true)
    private Transaction transaction;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "profile", "kyc", "country", "transactions", "refunds" }, allowSetters = true)
    private Customer customer;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Refund id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReference() {
        return this.reference;
    }

    public Refund reference(String reference) {
        this.setReference(reference);
        return this;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getTransactionRef() {
        return this.transactionRef;
    }

    public Refund transactionRef(String transactionRef) {
        this.setTransactionRef(transactionRef);
        return this;
    }

    public void setTransactionRef(String transactionRef) {
        this.transactionRef = transactionRef;
    }

    public Instant getRefundDate() {
        return this.refundDate;
    }

    public Refund refundDate(Instant refundDate) {
        this.setRefundDate(refundDate);
        return this;
    }

    public void setRefundDate(Instant refundDate) {
        this.refundDate = refundDate;
    }

    public Status getRefundStatus() {
        return this.refundStatus;
    }

    public Refund refundStatus(Status refundStatus) {
        this.setRefundStatus(refundStatus);
        return this;
    }

    public void setRefundStatus(Status refundStatus) {
        this.refundStatus = refundStatus;
    }

    public Float getAmount() {
        return this.amount;
    }

    public Refund amount(Float amount) {
        this.setAmount(amount);
        return this;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
    }

    public Transaction getTransaction() {
        return this.transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    public Refund transaction(Transaction transaction) {
        this.setTransaction(transaction);
        return this;
    }

    public Customer getCustomer() {
        return this.customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Refund customer(Customer customer) {
        this.setCustomer(customer);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Refund)) {
            return false;
        }
        return getId() != null && getId().equals(((Refund) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Refund{" +
            "id=" + getId() +
            ", reference='" + getReference() + "'" +
            ", transactionRef='" + getTransactionRef() + "'" +
            ", refundDate='" + getRefundDate() + "'" +
            ", refundStatus='" + getRefundStatus() + "'" +
            ", amount=" + getAmount() +
            "}";
    }
}
