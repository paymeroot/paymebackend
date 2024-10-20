package com.payme.backend.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.payme.backend.domain.Kyc} entity. This class is used
 * in {@link com.payme.backend.web.rest.KycResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /kycs?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class KycCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter reference;

    private StringFilter typePiece;

    private StringFilter numberPiece;

    private StringFilter photoPieceUrl;

    private StringFilter photoSelfieUrl;

    private LongFilter customerId;

    private Boolean distinct;

    public KycCriteria() {}

    public KycCriteria(KycCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.reference = other.optionalReference().map(StringFilter::copy).orElse(null);
        this.typePiece = other.optionalTypePiece().map(StringFilter::copy).orElse(null);
        this.numberPiece = other.optionalNumberPiece().map(StringFilter::copy).orElse(null);
        this.photoPieceUrl = other.optionalPhotoPieceUrl().map(StringFilter::copy).orElse(null);
        this.photoSelfieUrl = other.optionalPhotoSelfieUrl().map(StringFilter::copy).orElse(null);
        this.customerId = other.optionalCustomerId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public KycCriteria copy() {
        return new KycCriteria(this);
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

    public StringFilter getTypePiece() {
        return typePiece;
    }

    public Optional<StringFilter> optionalTypePiece() {
        return Optional.ofNullable(typePiece);
    }

    public StringFilter typePiece() {
        if (typePiece == null) {
            setTypePiece(new StringFilter());
        }
        return typePiece;
    }

    public void setTypePiece(StringFilter typePiece) {
        this.typePiece = typePiece;
    }

    public StringFilter getNumberPiece() {
        return numberPiece;
    }

    public Optional<StringFilter> optionalNumberPiece() {
        return Optional.ofNullable(numberPiece);
    }

    public StringFilter numberPiece() {
        if (numberPiece == null) {
            setNumberPiece(new StringFilter());
        }
        return numberPiece;
    }

    public void setNumberPiece(StringFilter numberPiece) {
        this.numberPiece = numberPiece;
    }

    public StringFilter getPhotoPieceUrl() {
        return photoPieceUrl;
    }

    public Optional<StringFilter> optionalPhotoPieceUrl() {
        return Optional.ofNullable(photoPieceUrl);
    }

    public StringFilter photoPieceUrl() {
        if (photoPieceUrl == null) {
            setPhotoPieceUrl(new StringFilter());
        }
        return photoPieceUrl;
    }

    public void setPhotoPieceUrl(StringFilter photoPieceUrl) {
        this.photoPieceUrl = photoPieceUrl;
    }

    public StringFilter getPhotoSelfieUrl() {
        return photoSelfieUrl;
    }

    public Optional<StringFilter> optionalPhotoSelfieUrl() {
        return Optional.ofNullable(photoSelfieUrl);
    }

    public StringFilter photoSelfieUrl() {
        if (photoSelfieUrl == null) {
            setPhotoSelfieUrl(new StringFilter());
        }
        return photoSelfieUrl;
    }

    public void setPhotoSelfieUrl(StringFilter photoSelfieUrl) {
        this.photoSelfieUrl = photoSelfieUrl;
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
        final KycCriteria that = (KycCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(reference, that.reference) &&
            Objects.equals(typePiece, that.typePiece) &&
            Objects.equals(numberPiece, that.numberPiece) &&
            Objects.equals(photoPieceUrl, that.photoPieceUrl) &&
            Objects.equals(photoSelfieUrl, that.photoSelfieUrl) &&
            Objects.equals(customerId, that.customerId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, reference, typePiece, numberPiece, photoPieceUrl, photoSelfieUrl, customerId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "KycCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalReference().map(f -> "reference=" + f + ", ").orElse("") +
            optionalTypePiece().map(f -> "typePiece=" + f + ", ").orElse("") +
            optionalNumberPiece().map(f -> "numberPiece=" + f + ", ").orElse("") +
            optionalPhotoPieceUrl().map(f -> "photoPieceUrl=" + f + ", ").orElse("") +
            optionalPhotoSelfieUrl().map(f -> "photoSelfieUrl=" + f + ", ").orElse("") +
            optionalCustomerId().map(f -> "customerId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
