package com.coursework.demo.service;

import com.coursework.demo.entity.Building;
import com.coursework.demo.repository.BuildingRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.coursework.demo.TestData.getBuilding;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
public class BuildingServiceImplTest {

    @MockBean
    private BuildingRepository buildingRepository;

    @Autowired
    private BuildingService buildingService;

    @Test
    public void testGetById() {
        final Building building = getBuilding();

        when(buildingRepository.findById(anyLong())).thenReturn(Optional.of(building));

        final Building result = buildingService.getById(1L);

        assertEquals(building, result);
        verify(buildingRepository).findById(anyLong());
    }

    @Test
    public void testGetAll() {
        final Building building = getBuilding();
        final List<Building> buildingList = Collections.singletonList(building);
        final Pageable pageable = PageRequest.of(0, 5);
        final Page<Building> buildingPage = new PageImpl<>(buildingList, pageable, 5);

        when(buildingRepository.findAll(pageable)).thenReturn(buildingPage);

        final List<Building> result = buildingService.getAll(pageable);

        assertEquals(buildingList, result);
        verify(buildingRepository).findAll(pageable);
    }

    @Test
    public void testSave() {
        final Building building = getBuilding();

        when(buildingRepository.save(building)).thenReturn(building);

        final Building result = buildingService.save(building);

        assertEquals(building, result);
        verify(buildingRepository).save(building);
    }

    @Test
    public void testDelete() {
        final Building building = getBuilding();

        doNothing().when(buildingRepository).delete(building);

        final Building result = buildingService.delete(building);

        assertEquals(building, result);
        verify(buildingRepository).delete(building);
    }
}
