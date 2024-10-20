package com.payme.backend.service.mapper;

import com.payme.backend.domain.Kyc;
import com.payme.backend.service.dto.KycDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Kyc} and its DTO {@link KycDTO}.
 */
@Mapper(componentModel = "spring")
public interface KycMapper extends EntityMapper<KycDTO, Kyc> {}
