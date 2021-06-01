package com.coursework.demo.repository;

import com.coursework.demo.dto.ExpensesDTO;
import com.coursework.demo.entity.enums.ProcurementType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.Arrays;
import java.util.List;

import static com.coursework.demo.TestData.getExpectedExpensesList;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class LedgerRepositoryTest {

    @Autowired
    private LedgerRepository ledgerRepository;

    @Test
    @Sql("/setup.sql")
    public void testLedger() {
        List<ExpensesDTO> expensesDTOList = ledgerRepository.findExpensesName("Ozzy");

        assertFalse(expensesDTOList.isEmpty());
        assertTrue(expensesDTOList.containsAll(getExpectedExpensesList()));
    }
}
