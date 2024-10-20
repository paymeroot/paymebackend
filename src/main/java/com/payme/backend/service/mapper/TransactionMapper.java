package com.payme.backend.service.mapper;

import com.payme.backend.domain.Customer;
import com.payme.backend.domain.Transaction;
import com.payme.backend.service.dto.CustomerDTO;
import com.payme.backend.service.dto.TransactionDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Transaction} and its DTO {@link TransactionDTO}.
 */
@Mapper(componentModel = "spring")
public interface TransactionMapper extends EntityMapper<TransactionDTO, Transaction> {
    @Mapping(target = "customer", source = "customer", qualifiedByName = "customerId")
    TransactionDTO toDto(Transaction s);

    @Named("customerId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CustomerDTO toDtoCustomerId(Customer customer);
}
