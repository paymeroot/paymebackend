package com.payme.backend.service.mapper;

import com.payme.backend.domain.Customer;
import com.payme.backend.domain.Refund;
import com.payme.backend.domain.Transaction;
import com.payme.backend.service.dto.CustomerDTO;
import com.payme.backend.service.dto.RefundDTO;
import com.payme.backend.service.dto.TransactionDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Refund} and its DTO {@link RefundDTO}.
 */
@Mapper(componentModel = "spring")
public interface RefundMapper extends EntityMapper<RefundDTO, Refund> {
    @Mapping(target = "transaction", source = "transaction", qualifiedByName = "transactionId")
    @Mapping(target = "customer", source = "customer", qualifiedByName = "customerId")
    RefundDTO toDto(Refund s);

    @Named("transactionId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    TransactionDTO toDtoTransactionId(Transaction transaction);

    @Named("customerId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CustomerDTO toDtoCustomerId(Customer customer);
}
