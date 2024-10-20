package com.payme.backend.service.dto;

import com.payme.backend.domain.enumeration.Status;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.payme.backend.domain.Customer} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CustomerDTO implements Serializable {

    private Long id;

    @NotNull
    private String lastname;

    @NotNull
    private String firstname;

    @NotNull
    private String phone;

    @NotNull
    private String countryCode;

    private Status statusKyc;

    private ProfileDTO profile;

    private KycDTO kyc;

    private CountryDTO country;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public Status getStatusKyc() {
        return statusKyc;
    }

    public void setStatusKyc(Status statusKyc) {
        this.statusKyc = statusKyc;
    }

    public ProfileDTO getProfile() {
        return profile;
    }

    public void setProfile(ProfileDTO profile) {
        this.profile = profile;
    }

    public KycDTO getKyc() {
        return kyc;
    }

    public void setKyc(KycDTO kyc) {
        this.kyc = kyc;
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
        if (!(o instanceof CustomerDTO)) {
            return false;
        }

        CustomerDTO customerDTO = (CustomerDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, customerDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CustomerDTO{" +
            "id=" + getId() +
            ", lastname='" + getLastname() + "'" +
            ", firstname='" + getFirstname() + "'" +
            ", phone='" + getPhone() + "'" +
            ", countryCode='" + getCountryCode() + "'" +
            ", statusKyc='" + getStatusKyc() + "'" +
            ", profile=" + getProfile() +
            ", kyc=" + getKyc() +
            ", country=" + getCountry() +
            "}";
    }
}
