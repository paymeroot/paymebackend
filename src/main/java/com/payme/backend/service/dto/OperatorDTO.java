package com.payme.backend.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.payme.backend.domain.Operator} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class OperatorDTO implements Serializable {

    private Long id;

    @NotNull
    private String nom;

    @NotNull
    private String code;

    @NotNull
    private String countryCode;

    private String logoUrl;

    private Float taxPayIn;

    private Float taxPayOut;

    private CountryDTO country;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public Float getTaxPayIn() {
        return taxPayIn;
    }

    public void setTaxPayIn(Float taxPayIn) {
        this.taxPayIn = taxPayIn;
    }

    public Float getTaxPayOut() {
        return taxPayOut;
    }

    public void setTaxPayOut(Float taxPayOut) {
        this.taxPayOut = taxPayOut;
    }

    public CountryDTO getCountry() {
        return country;
    }

    public void setCountry(CountryDTO country) {
        this.country = country;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OperatorDTO)) {
            return false;
        }

        OperatorDTO operatorDTO = (OperatorDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, operatorDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "OperatorDTO{" +
            "id=" + getId() +
            ", nom='" + getNom() + "'" +
            ", code='" + getCode() + "'" +
            ", countryCode='" + getCountryCode() + "'" +
            ", logoUrl='" + getLogoUrl() + "'" +
            ", taxPayIn=" + getTaxPayIn() +
            ", taxPayOut=" + getTaxPayOut() +
            ", country=" + getCountry() +
            "}";
    }
}
