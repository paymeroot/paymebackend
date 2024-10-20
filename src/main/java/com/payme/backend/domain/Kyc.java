package com.payme.backend.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Kyc.
 */
@Entity
@Table(name = "kyc")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "kyc")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Kyc implements Serializable {

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
    @Column(name = "type_piece", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String typePiece;

    @NotNull
    @Column(name = "number_piece", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String numberPiece;

    @NotNull
    @Column(name = "photo_piece_url", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String photoPieceUrl;

    @NotNull
    @Column(name = "photo_selfie_url", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String photoSelfieUrl;

    @JsonIgnoreProperties(value = { "profile", "kyc", "country", "transactions", "refunds" }, allowSetters = true)
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "kyc")
    @org.springframework.data.annotation.Transient
    private Customer customer;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Kyc id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReference() {
        return this.reference;
    }

    public Kyc reference(String reference) {
        this.setReference(reference);
        return this;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getTypePiece() {
        return this.typePiece;
    }

    public Kyc typePiece(String typePiece) {
        this.setTypePiece(typePiece);
        return this;
    }

    public void setTypePiece(String typePiece) {
        this.typePiece = typePiece;
    }

    public String getNumberPiece() {
        return this.numberPiece;
    }

    public Kyc numberPiece(String numberPiece) {
        this.setNumberPiece(numberPiece);
        return this;
    }

    public void setNumberPiece(String numberPiece) {
        this.numberPiece = numberPiece;
    }

    public String getPhotoPieceUrl() {
        return this.photoPieceUrl;
    }

    public Kyc photoPieceUrl(String photoPieceUrl) {
        this.setPhotoPieceUrl(photoPieceUrl);
        return this;
    }

    public void setPhotoPieceUrl(String photoPieceUrl) {
        this.photoPieceUrl = photoPieceUrl;
    }

    public String getPhotoSelfieUrl() {
        return this.photoSelfieUrl;
    }

    public Kyc photoSelfieUrl(String photoSelfieUrl) {
        this.setPhotoSelfieUrl(photoSelfieUrl);
        return this;
    }

    public void setPhotoSelfieUrl(String photoSelfieUrl) {
        this.photoSelfieUrl = photoSelfieUrl;
    }

    public Customer getCustomer() {
        return this.customer;
    }

    public void setCustomer(Customer customer) {
        if (this.customer != null) {
            this.customer.setKyc(null);
        }
        if (customer != null) {
            customer.setKyc(this);
        }
        this.customer = customer;
    }

    public Kyc customer(Customer customer) {
        this.setCustomer(customer);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Kyc)) {
            return false;
        }
        return getId() != null && getId().equals(((Kyc) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Kyc{" +
            "id=" + getId() +
            ", reference='" + getReference() + "'" +
            ", typePiece='" + getTypePiece() + "'" +
            ", numberPiece='" + getNumberPiece() + "'" +
            ", photoPieceUrl='" + getPhotoPieceUrl() + "'" +
            ", photoSelfieUrl='" + getPhotoSelfieUrl() + "'" +
            "}";
    }
}
