package com.coursework.demo.service.impl;

import com.coursework.demo.dto.ExpensesDTO;
import com.coursework.demo.entity.Ledger;
import com.coursework.demo.entity.enums.ProcurementType;
import com.coursework.demo.mapper.TransferMapper;
import com.coursework.demo.repository.LedgerRepository;
import com.coursework.demo.service.EquipmentService;
import com.coursework.demo.service.LedgerService;
import com.coursework.demo.service.WarehouseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Slf4j
@Transactional
@Service
public class LedgerServiceImpl implements LedgerService {

    private final LedgerRepository ledgerRepository;

    private final TransferMapper transferMapper;

    private final WarehouseService warehouseService;
    private final EquipmentService equipmentService;

    @Autowired
    public LedgerServiceImpl(LedgerRepository ledgerRepository, TransferMapper transferMapper,
                             WarehouseService warehouseService, EquipmentService equipmentService) {
        this.ledgerRepository = ledgerRepository;
        this.transferMapper = transferMapper;
        this.warehouseService = warehouseService;
        this.equipmentService = equipmentService;
    }

    @Override
    public Ledger getById(Long id) {
        return ledgerRepository.findById(id).get();
    }

    @Override
    public List<Ledger> getAll(Pageable pageable) {
        return ledgerRepository.findAll(pageable).getContent();
    }

    @Override
    public Ledger save(Ledger object) {
        log.info("In save(entity = [{}]", object);
        transferLedger(object);
        return ledgerRepository.save(object);
    }

    @Override
    public Ledger delete(Ledger object) {
        ledgerRepository.delete(object);
        return object;
    }

    @Override
    public List<ExpensesDTO> getExpensesName(String name) {
        return ledgerRepository.findExpensesName(name);
    }

    /**
     * Method transfer ledger either to warehouse or equipment
     *
     * @param object
     */
    private void transferLedger(Ledger object) {
        if (object.getProcurementType() == ProcurementType.WAREHOUSE) {
            warehouseService.save(transferMapper.toWarehouse(object), object.getBookkeeping());
        } else if (object.getProcurementType() == ProcurementType.EQUIPMENT) {
            equipmentService.save(transferMapper.toEquipment(object), object.getBookkeeping());
        }
    }
}
