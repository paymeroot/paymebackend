package com.payme.backend.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.payme.backend.domain.Kyc} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class KycDTO implements Serializable {

    private Long id;

    @NotNull
    private String reference;

    @NotNull
    private String typePiece;

    @NotNull
    private String numberPiece;

    @NotNull
    private String photoPieceUrl;

    @NotNull
    private String photoSelfieUrl;

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

    public String getTypePiece() {
        return typePiece;
    }

    public void setTypePiece(String typePiece) {
        this.typePiece = typePiece;
    }

    public String getNumberPiece() {
        return numberPiece;
    }

    public void setNumberPiece(String numberPiece) {
        this.numberPiece = numberPiece;
    }

    public String getPhotoPieceUrl() {
        return photoPieceUrl;
    }

    public void setPhotoPieceUrl(String photoPieceUrl) {
        this.photoPieceUrl = photoPieceUrl;
    }

    public String getPhotoSelfieUrl() {
        return photoSelfieUrl;
    }

    public void setPhotoSelfieUrl(String photoSelfieUrl) {
        this.photoSelfieUrl = photoSelfieUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof KycDTO)) {
            return false;
        }

        KycDTO kycDTO = (KycDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, kycDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "KycDTO{" +
            "id=" + getId() +
            ", reference='" + getReference() + "'" +
            ", typePiece='" + getTypePiece() + "'" +
            ", numberPiece='" + getNumberPiece() + "'" +
            ", photoPieceUrl='" + getPhotoPieceUrl() + "'" +
            ", photoSelfieUrl='" + getPhotoSelfieUrl() + "'" +
            "}";
    }
}
