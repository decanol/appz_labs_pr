package com.coursework.demo.it;

import com.coursework.demo.entity.Warehouse;
import com.coursework.demo.repository.WarehouseRepository;
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

import static com.coursework.demo.TestData.getWarehouse;
import static com.coursework.demo.TestData.getWarehouseRequest;
import static com.coursework.demo.it.TestUtils.asJsonString;
import static com.coursework.demo.it.TestUtils.getRequest;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class WarehouseControllerIT {

    private static final String WAREHOUSE_CONTROLLER_PATH = "/v1/warehouses/";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WarehouseRepository warehouseRepository;

    @Test
    @WithMockUser(roles = "WORKER")
    public void testRetrieveWarehouseById() throws Exception {
        when(warehouseRepository.findById(anyLong())).thenReturn(Optional.of(getWarehouse(50L)));

        mockMvc.perform(getRequest(WAREHOUSE_CONTROLLER_PATH + "1"))
                .andExpect(status().isOk())
                .andExpect(content().string(asJsonString(getWarehouseRequest(50L))));
    }

    @Test
    @WithMockUser(roles = "WORKER")
    public void testRetrieveWarehouseList() throws Exception {
        final Warehouse warehouse = getWarehouse(50L);
        final List<Warehouse> warehouses = Collections.singletonList(warehouse);
        final Pageable pageable = PageRequest.of(0, 10, Sort.by("id"));
        final Page<Warehouse> warehousePage = new PageImpl<>(warehouses, pageable, 10);

        when(warehouseRepository.findAll(pageable)).thenReturn(warehousePage);

        mockMvc.perform(getRequest(WAREHOUSE_CONTROLLER_PATH))
                .andExpect(status().isOk())
                .andExpect(content().string(asJsonString(Collections.singletonList(getWarehouseRequest(50L)))));
    }
}
