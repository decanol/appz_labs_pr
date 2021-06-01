package com.coursework.demo.repository;

import com.coursework.demo.dto.ExpensesDTO;
import com.coursework.demo.entity.Ledger;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LedgerRepository extends PagingAndSortingRepository<Ledger, Long> {
    @Query("SELECT new com.coursework.demo.dto.ExpensesDTO(l.procurementType, SUM(l.price)) FROM Ledger l " +
           "JOIN l.building b " +
           "WHERE b.name = :buildingName AND l.bookkeeping = 'EXPENSES' " +
           "GROUP BY l.procurementType ")
    List<ExpensesDTO> findExpensesName(@Param("buildingName") String buildingName);
}

