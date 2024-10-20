package com.payme.backend.service.mapper;

import com.payme.backend.domain.Country;
import com.payme.backend.domain.Customer;
import com.payme.backend.domain.Kyc;
import com.payme.backend.domain.Profile;
import com.payme.backend.service.dto.CountryDTO;
import com.payme.backend.service.dto.CustomerDTO;
import com.payme.backend.service.dto.KycDTO;
import com.payme.backend.service.dto.ProfileDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Customer} and its DTO {@link CustomerDTO}.
 */
@Mapper(componentModel = "spring")
public interface CustomerMapper extends EntityMapper<CustomerDTO, Customer> {
    @Mapping(target = "profile", source = "profile", qualifiedByName = "profileId")
    @Mapping(target = "kyc", source = "kyc", qualifiedByName = "kycId")
    @Mapping(target = "country", source = "country", qualifiedByName = "countryId")
    CustomerDTO toDto(Customer s);

    @Named("profileId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ProfileDTO toDtoProfileId(Profile profile);

    @Named("kycId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    KycDTO toDtoKycId(Kyc kyc);

    @Named("countryId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CountryDTO toDtoCountryId(Country country);
}
