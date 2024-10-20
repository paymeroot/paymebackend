package com.payme.backend.service.mapper;

import com.payme.backend.domain.Country;
import com.payme.backend.domain.Operator;
import com.payme.backend.service.dto.CountryDTO;
import com.payme.backend.service.dto.OperatorDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Operator} and its DTO {@link OperatorDTO}.
 */
@Mapper(componentModel = "spring")
public interface OperatorMapper extends EntityMapper<OperatorDTO, Operator> {
    @Mapping(target = "country", source = "country", qualifiedByName = "countryId")
    OperatorDTO toDto(Operator s);

    @Named("countryId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CountryDTO toDtoCountryId(Country country);
}
