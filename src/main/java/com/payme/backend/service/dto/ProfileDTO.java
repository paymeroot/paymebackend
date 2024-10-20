package com.payme.backend.service.dto;

import com.payme.backend.domain.enumeration.Gender;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.payme.backend.domain.Profile} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ProfileDTO implements Serializable {

    private Long id;

    private String email;

    private String address;

    private String ville;

    private String countryCode;

    private String avatarUrl;

    private Gender genre;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getVille() {
        return ville;
    }

    public void setVille(String ville) {
        this.ville = ville;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public Gender getGenre() {
        return genre;
    }

    public void setGenre(Gender genre) {
        this.genre = genre;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProfileDTO)) {
            return false;
        }

        ProfileDTO profileDTO = (ProfileDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, profileDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProfileDTO{" +
            "id=" + getId() +
            ", email='" + getEmail() + "'" +
            ", address='" + getAddress() + "'" +
            ", ville='" + getVille() + "'" +
            ", countryCode='" + getCountryCode() + "'" +
            ", avatarUrl='" + getAvatarUrl() + "'" +
            ", genre='" + getGenre() + "'" +
            "}";
    }
}
