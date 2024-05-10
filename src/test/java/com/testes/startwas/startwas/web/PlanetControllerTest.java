package com.testes.startwas.startwas.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.testes.startwas.startwas.domain.Planet;
import com.testes.startwas.startwas.domain.PlanetService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.testes.startwas.startwas.common.PlanetConstants.*;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PlanetController.class)
public class PlanetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PlanetService planetService;

    @Test
    public void createPlanet_WithValidData_ReturnCreate() throws Exception {

        when(planetService.create(PLANET)).thenReturn(PLANET);

        mockMvc.perform(post("/planets").
                        content(objectMapper.writeValueAsString(PLANET)).contentType(MediaType.APPLICATION_JSON)).
                andExpect(status().isCreated()).
                andExpect(jsonPath("$").value(PLANET));
    }

    @Test
    public void createPlanet_WithInvalidData_ReturnsBadRequest() throws Exception {
        Planet emptyPlanet = new Planet();

        Planet invalidPlanet = new Planet("", "", "");

        mockMvc.perform(post("/planets").
                        content(objectMapper.writeValueAsString(emptyPlanet)).contentType(MediaType.APPLICATION_JSON)).
                andExpect(status().isUnprocessableEntity());

        mockMvc.perform(post("/planets").
                        content(objectMapper.writeValueAsString(invalidPlanet)).contentType(MediaType.APPLICATION_JSON)).
                andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void createPlanet_WithInvalidExistingName_ReturnsConflict() throws Exception {
        when(planetService.create(any())).thenThrow(DataIntegrityViolationException.class);

        mockMvc.perform(post("/planets").
                        content(objectMapper.writeValueAsString(PLANET)).contentType(MediaType.APPLICATION_JSON)).
                andExpect(status().isConflict());
    }

    @Test
    public void getPlanet_ByExistingId_ReturnPlanet() throws Exception {
        when(planetService.findById(1L)).thenReturn(Optional.of(PLANET));

        mockMvc.perform(get("/planets/1").contentType(MediaType.APPLICATION_JSON)).
                andExpect(status().isOk()).
                andExpect(jsonPath("$").value(PLANET));
    }

    @Test
    public void getPlanet_ByUnexistingId_ReturnPNotFound() throws Exception {
        when(planetService.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get(String.format("/planets/%d", 1)).contentType(MediaType.APPLICATION_JSON)).
                andExpect(status().isNotFound());
    }

    @Test
    public void getPlanet_ByExistingName_ReturnPlanet() throws Exception {
        when(planetService.getByName(PLANET.getName())).thenReturn(Optional.of(PLANET));

        mockMvc.perform(get(String.format("/planets/name/%s", PLANET.getName())).contentType(MediaType.APPLICATION_JSON)).
                andExpect(status().isOk()).
                andExpect(jsonPath("$").value(PLANET));
        ;
    }

    @Test
    public void getPlanet_ByUnexistingName_ReturnPNotFound() throws Exception {
        when(planetService.getByName(PLANET.getName())).thenReturn(Optional.empty());
        mockMvc.perform(get(String.format("/planets/name/%s", PLANET.getName())).contentType(MediaType.APPLICATION_JSON)).
                andExpect(status().isNotFound());
    }

    @Test
    public void listPlanets_ReturnsFilterPlanets() throws Exception {
        when(planetService.list(null, null)).thenReturn(PLANETS);
        when(planetService.list(TATOOINE.getTerrain(), TATOOINE.getClimate())).thenReturn(List.of(TATOOINE));

        mockMvc.perform(get("/planets").contentType(MediaType.APPLICATION_JSON)).
                andExpect(status().isOk()).
                andExpect(jsonPath("$", hasSize(3)));

        mockMvc.perform(get(String.format("/planets?terrain=%s&climate=%s", TATOOINE.getTerrain(), TATOOINE.getClimate())).contentType(MediaType.APPLICATION_JSON)).
                andExpect(status().isOk()).

                andExpect(jsonPath("$", hasSize(1))).
                andExpect(jsonPath("$[0]").value(TATOOINE));
    }

    @Test
    public void listPlanets_ReturnsNoPlanets() throws Exception {
        when(planetService.list(null, null)).thenReturn(Collections.emptyList());
        mockMvc.perform(get("/planets").contentType(MediaType.APPLICATION_JSON)).
                andExpect(status().isOk()).
                andExpect(jsonPath("$", hasSize(0)));
    }
}
