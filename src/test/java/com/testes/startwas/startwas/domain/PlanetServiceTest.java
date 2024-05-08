package com.testes.startwas.startwas.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Example;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.testes.startwas.startwas.common.PlanetConstants.INVALID_PLANET;
import static com.testes.startwas.startwas.common.PlanetConstants.PLANET;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

/**
 * Não há necessidade de usar o teste do spring, quando á uma auta quantidade de classes, os testes podem ficar pesados
 */

//@SpringBootTest(classes = PlanetService.class)
@ExtendWith(MockitoExtension.class)
class PlanetServiceTest {

    // @Autowired
    @InjectMocks
    private PlanetService planetService;

    // @MockBean
    @Mock
    private PlanetRepository planetRepository;

    @Test
    void createPlanet_WithValidData_ReturnsPlanet() {
        when(planetRepository.save(PLANET)).thenReturn(PLANET);
        Planet sut = planetService.create(PLANET);
        assertEquals(PLANET, sut);
    }

    @Test
    void createPlamet_WithInvalidData_ThrowsException() {
        when(planetRepository.save(INVALID_PLANET)).thenThrow(RuntimeException.class);

        assertThrows(RuntimeException.class, () -> planetService.create(INVALID_PLANET));
    }

    @Test
    void getById_withId_returnPlanet() {
        when(planetRepository.findById(1L)).thenReturn(Optional.of(PLANET));

        Optional<Planet> op = planetService.findById(1L);

        assertEquals(PLANET, op.get());
    }

    @Test
    void getPlanet_ByUnexistingId_ReturnEmpty() {
        when(planetRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Planet> op = planetService.findById(99L);

        assertTrue(op.isEmpty());
    }

    @Test
    void getByName_ByExisteingName_returnPlanet() {
        when(planetRepository.findByName(PLANET.getName())).thenReturn(Optional.of(PLANET));

        Optional<Planet> op = planetService.getByName(PLANET.getName());

        assertEquals(PLANET, op.get());
    }

    @Test
    void getPlanet_ByUnexistingName_ReturnEmpty() {
        when(planetRepository.findByName("name")).thenReturn(Optional.empty());

        Optional<Planet> op = planetService.getByName("name");

        assertTrue(op.isEmpty());
    }

    @Test
    void listPlanet_ReturnsAllPlanets() {
        List<Planet> planets = new ArrayList<>() {{
            add(PLANET);
        }};

        Example<Planet> query = QueryBuilder.makeQuery(new Planet(PLANET.getClimate(), PLANET.getTerrain()));

        when(planetRepository.findAll(query)).thenReturn(planets);

        List<Planet> sut = planetService.list(PLANET.getTerrain(), PLANET.getClimate());

        assertFalse(sut.isEmpty());
        assertEquals(1, sut.size());
        assertEquals(PLANET, sut.get(0));

    }

    @Test
    void listPlanet_ReturnsNoPlanets() {
//ArgumentMatchers class onde o ant é implementado
        when(planetRepository.findAll(any())).thenReturn(Collections.emptyList());

        List<Planet> sut = planetService.list(PLANET.getTerrain(), PLANET.getClimate());

        assertTrue(sut.isEmpty());
    }

    @Test
    void removePlanet_WithExistingId_doesNotThrowAntException() {
        assertDoesNotThrow(() -> planetService.remove(1L));
    }

    @Test
    void removePlanet_WithUnexistingId_ThrowAntException() {
        doThrow(new RuntimeException()).when(planetRepository).deleteById(0L);
        assertThrows(RuntimeException.class, () -> planetService.remove(0L));
    }
}