package com.coursework.demo.it;

import com.coursework.demo.entity.Equipment;
import com.coursework.demo.repository.EquipmentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.coursework.demo.TestData.getEquipment;
import static com.coursework.demo.TestData.getEquipmentRequest;
import static com.coursework.demo.it.TestUtils.asJsonString;
import static com.coursework.demo.it.TestUtils.getRequest;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class EquipmentControllerIT {

    private static final String EQUIPMENT_CONTROLLER_PATH = "/v1/equipments/";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EquipmentRepository equipmentRepository;

    @Test
    @WithMockUser(roles = "WORKER")
    public void testRetrieveEquipmentById() throws Exception {
        when(equipmentRepository.findById(anyLong())).thenReturn(Optional.of(getEquipment(50L)));

        mockMvc.perform(getRequest(EQUIPMENT_CONTROLLER_PATH + "1"))
                .andExpect(status().isOk())
                .andExpect(content().string(asJsonString(getEquipmentRequest(50L))));
    }

    @Test
    @WithMockUser(roles = "WORKER")
    public void testRetrieveEquipmentList() throws Exception {
        final Equipment equipment = getEquipment(50L);
        final List<Equipment> equipments = Collections.singletonList(equipment);
        final Pageable pageable = PageRequest.of(0, 10, Sort.by("id"));
        final Page<Equipment> equipmentPage = new PageImpl<>(equipments, pageable, 10);

        when(equipmentRepository.findAll(pageable)).thenReturn(equipmentPage);

        mockMvc.perform(getRequest(EQUIPMENT_CONTROLLER_PATH))
                .andExpect(status().isOk())
                .andExpect(content().string(asJsonString(Collections.singletonList(getEquipmentRequest(50L)))));
    }
}
