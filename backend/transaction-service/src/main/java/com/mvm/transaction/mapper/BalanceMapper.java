package com.mvm.transaction.mapper;

import com.mvm.transaction.dto.BalanceDTO;
import com.mvm.transaction.model.Balance;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BalanceMapper {

    BalanceMapper INSTANCE = Mappers.getMapper(BalanceMapper.class);

    BalanceDTO toDTO(Balance balance);

    Balance toEntity(BalanceDTO dto);
}