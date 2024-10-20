package com.payme.backend.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.payme.backend.domain.enumeration.Status;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Customer.
 */
@Entity
@Table(name = "customer")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "customer")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Customer implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "lastname", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String lastname;

    @NotNull
    @Column(name = "firstname", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String firstname;

    @NotNull
    @Column(name = "phone", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String phone;

    @NotNull
    @Column(name = "country_code", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String countryCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_kyc")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Keyword)
    private Status statusKyc;

    @JsonIgnoreProperties(value = { "customer" }, allowSetters = true)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(unique = true)
    private Profile profile;

    @JsonIgnoreProperties(value = { "customer" }, allowSetters = true)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(unique = true)
    private Kyc kyc;

    @JsonIgnoreProperties(value = { "customer", "operator" }, allowSetters = true)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(unique = true)
    private Country country;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "customer")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "refund", "customer" }, allowSetters = true)
    private Set<Transaction> transactions = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "customer")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "transaction", "customer" }, allowSetters = true)
    private Set<Refund> refunds = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Customer id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLastname() {
        return this.lastname;
    }

    public Customer lastname(String lastname) {
        this.setLastname(lastname);
        return this;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getFirstname() {
        return this.firstname;
    }

    public Customer firstname(String firstname) {
        this.setFirstname(firstname);
        return this;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getPhone() {
        return this.phone;
    }

    public Customer phone(String phone) {
        this.setPhone(phone);
        return this;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCountryCode() {
        return this.countryCode;
    }

    public Customer countryCode(String countryCode) {
        this.setCountryCode(countryCode);
        return this;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public Status getStatusKyc() {
        return this.statusKyc;
    }

    public Customer statusKyc(Status statusKyc) {
        this.setStatusKyc(statusKyc);
        return this;
    }

    public void setStatusKyc(Status statusKyc) {
        this.statusKyc = statusKyc;
    }

    public Profile getProfile() {
        return this.profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public Customer profile(Profile profile) {
        this.setProfile(profile);
        return this;
    }

    public Kyc getKyc() {
        return this.kyc;
    }

    public void setKyc(Kyc kyc) {
        this.kyc = kyc;
    }

    public Customer kyc(Kyc kyc) {
        this.setKyc(kyc);
        return this;
    }

    public Country getCountry() {
        return this.country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public Customer country(Country country) {
        this.setCountry(country);
        return this;
    }

    public Set<Transaction> getTransactions() {
        return this.transactions;
    }

    public void setTransactions(Set<Transaction> transactions) {
        if (this.transactions != null) {
            this.transactions.forEach(i -> i.setCustomer(null));
        }
        if (transactions != null) {
            transactions.forEach(i -> i.setCustomer(this));
        }
        this.transactions = transactions;
    }

    public Customer transactions(Set<Transaction> transactions) {
        this.setTransactions(transactions);
        return this;
    }

    public Customer addTransaction(Transaction transaction) {
        this.transactions.add(transaction);
        transaction.setCustomer(this);
        return this;
    }

    public Customer removeTransaction(Transaction transaction) {
        this.transactions.remove(transaction);
        transaction.setCustomer(null);
        return this;
    }

    public Set<Refund> getRefunds() {
        return this.refunds;
    }

    public void setRefunds(Set<Refund> refunds) {
        if (this.refunds != null) {
            this.refunds.forEach(i -> i.setCustomer(null));
        }
        if (refunds != null) {
            refunds.forEach(i -> i.setCustomer(this));
        }
        this.refunds = refunds;
    }

    public Customer refunds(Set<Refund> refunds) {
        this.setRefunds(refunds);
        return this;
    }

    public Customer addRefund(Refund refund) {
        this.refunds.add(refund);
        refund.setCustomer(this);
        return this;
    }

    public Customer removeRefund(Refund refund) {
        this.refunds.remove(refund);
        refund.setCustomer(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Customer)) {
            return false;
        }
        return getId() != null && getId().equals(((Customer) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Customer{" +
            "id=" + getId() +
            ", lastname='" + getLastname() + "'" +
            ", firstname='" + getFirstname() + "'" +
            ", phone='" + getPhone() + "'" +
            ", countryCode='" + getCountryCode() + "'" +
            ", statusKyc='" + getStatusKyc() + "'" +
            "}";
    }
}
