package com.coursework.demo.service.impl;

import com.coursework.demo.entity.Equipment;
import com.coursework.demo.entity.enums.Bookkeeping;
import com.coursework.demo.exception.BookKeepingException;
import com.coursework.demo.exception.EntityNotFoundException;
import com.coursework.demo.exception.EntityWithQuantityException;
import com.coursework.demo.repository.EquipmentRepository;
import com.coursework.demo.service.EquipmentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Slf4j
@Transactional
@Service
public class EquipmentServiceImpl implements EquipmentService {

    private EquipmentRepository equipmentRepository;

    @Autowired
    public EquipmentServiceImpl(EquipmentRepository equipmentRepository) {
        this.equipmentRepository = equipmentRepository;
    }

    @Override
    public Equipment getById(Long id) {
        return equipmentRepository.findById(id).get();
    }

    @Override
    public List<Equipment> getAll(Pageable pageable) {
        return equipmentRepository.findAll(pageable).getContent();
    }

    @Override
    public Equipment update(Equipment jsonEquipment, Bookkeeping bookkeeping) {
        log.info("In update(entity = [{}]", jsonEquipment);
        Equipment dbEquipment = getEquipmentByName(jsonEquipment.getName(), jsonEquipment.getBuilding().getName());

        if (bookkeeping == Bookkeeping.EXPENSES) {
            dbEquipment = changeEquipmentQuantity(dbEquipment, dbEquipment.getQuantity() + jsonEquipment.getQuantity());
        } else if (bookkeeping == Bookkeeping.INCOME) {
            if (dbEquipment.getQuantity() < jsonEquipment.getQuantity()) {
                throw new EntityWithQuantityException("you ary trying to sell more equipment than you have");
            } else if (dbEquipment.getQuantity().equals(jsonEquipment.getQuantity())) {
                dbEquipment = delete(dbEquipment);
            } else {
                dbEquipment = changeEquipmentQuantity(dbEquipment, dbEquipment.getQuantity() - jsonEquipment.getQuantity());
            }
        }

        return dbEquipment;
    }

    @Override
    public Equipment getEquipmentByName(String title, String buildingName) {
        return equipmentRepository.findEquipmentByNameAndBuildingName(title, buildingName).orElseThrow(
                () -> new EntityNotFoundException(Equipment.class, "title", title)
        );
    }

    @Override
    public Equipment save(Equipment object, Bookkeeping bookkeeping) {
        log.info("In save(entity = [{}]", object);
        if (isEquipmentExistsWithTitle(object.getName())) {
            return update(object, bookkeeping);
        } else if (bookkeeping == Bookkeeping.EXPENSES) {
            return equipmentRepository.save(object);
        } else {
            throw new BookKeepingException("There is no such item in equipment");
        }
    }

    @Override
    public Equipment delete(Equipment object) {
        equipmentRepository.delete(object);
        return object;
    }

    /**
     * Method finds if Equipment with title already exists
     *
     * @param title
     * @return true if Equipment with such title already exist
     */
    @Override
    public boolean isEquipmentExistsWithTitle(String title) {
        return equipmentRepository.countEquipmentWithName(title) != 0;
    }

    private Equipment changeEquipmentQuantity(Equipment dbEquipment, long l) {
        dbEquipment.setQuantity(l);
        dbEquipment = equipmentRepository.save(dbEquipment);
        return dbEquipment;
    }
}
