package com.coursework.demo.it;

import com.coursework.demo.dto.BuildingDTO;
import com.coursework.demo.entity.Building;
import com.coursework.demo.repository.BuildingRepository;
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

import static com.coursework.demo.TestData.getBuilding;
import static com.coursework.demo.TestData.getBuildingRequest;
import static com.coursework.demo.it.TestUtils.asJsonString;
import static com.coursework.demo.it.TestUtils.deleteRequest;
import static com.coursework.demo.it.TestUtils.getRequest;
import static com.coursework.demo.it.TestUtils.postRequest;
import static com.coursework.demo.it.TestUtils.putRequest;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BuildingControllerIT {

    private static final String BUILDING_CONTROLLER_PATH = "/v1/buildings/";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BuildingRepository buildingRepository;

    @Test
    @WithMockUser(roles = "OWNER")
    public void testRetrieveBuildingById() throws Exception {
        when(buildingRepository.findById(anyLong())).thenReturn(Optional.of(getBuilding()));

        mockMvc.perform(getRequest(BUILDING_CONTROLLER_PATH + "1"))
                .andExpect(status().isOk())
                .andExpect(content().string(asJsonString(getBuildingRequest())));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testRetrieveBuildingList() throws Exception {
        final Building building = getBuilding();
        final List<Building> buildings = Collections.singletonList(building);
        final Pageable pageable = PageRequest.of(0, 10, Sort.by("id"));
        final Page<Building> buildingPage = new PageImpl<>(buildings, pageable, 10);

        when(buildingRepository.findAll(pageable)).thenReturn(buildingPage);

        mockMvc.perform(getRequest(BUILDING_CONTROLLER_PATH))
                .andExpect(status().isOk())
                .andExpect(content().string(asJsonString(Collections.singletonList(getBuildingRequest()))));
    }

    @Test
    @WithMockUser(roles = "OWNER")
    public void testSaveBuilding() throws Exception {
        final Building building = getBuilding();
        final BuildingDTO request = getBuildingRequest();

        when(buildingRepository.save(any(Building.class))).thenReturn(building);

        mockMvc.perform(postRequest(BUILDING_CONTROLLER_PATH, request))
                .andExpect(status().isCreated())
                .andExpect(content().string(asJsonString(request)));
    }

    @Test
    @WithMockUser(roles = "OWNER")
    public void testUpdateBuilding() throws Exception {
        final Building building = getBuilding();
        final BuildingDTO request = getBuildingRequest();

        when(buildingRepository.save(building)).thenReturn(building);

        mockMvc.perform(putRequest(BUILDING_CONTROLLER_PATH + "1", request))
                .andExpect(status().isOk())
                .andExpect(content().string(asJsonString(request)));
    }

    @Test
    @WithMockUser(roles = "OWNER")
    public void testUpdateBuildingExpectedBadRequest() throws Exception {
        final BuildingDTO request = getBuildingRequest();

        mockMvc.perform(putRequest(BUILDING_CONTROLLER_PATH + "2", request))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "OWNER")
    public void testDeleteBuilding() throws Exception {
        final Building building = getBuilding();

        when(buildingRepository.findById(anyLong())).thenReturn(Optional.of(building));
        doNothing().when(buildingRepository).delete(building);

        mockMvc.perform(deleteRequest(BUILDING_CONTROLLER_PATH + "1"))
                .andExpect(status().isNoContent());
    }
}