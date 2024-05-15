package com.testes.startwas.startwas.domain;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Example;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static com.testes.startwas.startwas.common.PlanetConstants.PLANET;
import static com.testes.startwas.startwas.common.PlanetConstants.TATOOINE;
import static org.junit.jupiter.api.Assertions.*;

//@SpringBootTest(classes = PlanetRepository.class)
@DataJpaTest
public class PlanetRepositoryTest {

    @Autowired
    private PlanetRepository planetRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    @AfterEach
    public void afterEach() {
        PLANET.setId(null);
    }

    @Test
    public void createPlanet_WithValidData_ReturnsPlanet() {
        Planet planet = planetRepository.save(PLANET);

        Planet sut = testEntityManager.find(Planet.class, planet.getId());

        assertNotNull(sut);
        assertEquals(PLANET.getName(), sut.getName());
        assertEquals(PLANET.getClimate(), sut.getClimate());
        assertEquals(PLANET.getTerrain(), sut.getTerrain());

    }

    @ParameterizedTest
    @MethodSource("providesInvalidPlanet")
    public void createPlanet_WithInvalidData_ThrowsException(Planet planet) {
        assertThrows(RuntimeException.class, () -> planetRepository.save(planet));
    }

    private static Stream<Arguments> providesInvalidPlanet() {
        return Stream.of(
                Arguments.of(new Planet(null, null, null)),
                Arguments.of(new Planet("", null, null)),
                Arguments.of(new Planet(null, "", null)),
                Arguments.of(new Planet(null, null, "")),
                Arguments.of(new Planet("", "", null)),
                Arguments.of(new Planet("", null, "")),
                Arguments.of(new Planet(null, "", "")),
                Arguments.of(new Planet("", "", "")),
                Arguments.of(new Planet("nome", null, null)),
                Arguments.of(new Planet(null, "climate", null)),
                Arguments.of(new Planet(null, null, "terrain")),
                Arguments.of(new Planet("nome", "", null)),
                Arguments.of(new Planet("nome", null, "")),
                Arguments.of(new Planet("", "climate", null)),
                Arguments.of(new Planet(null, "climate", "")),
                Arguments.of(new Planet("", "", "terrain")),
                Arguments.of(new Planet("nome", "climate", null)),
                Arguments.of(new Planet("nome", null, "terrain")),
                Arguments.of(new Planet(null, "climate", "terrain")),
                Arguments.of(new Planet("nome", "climate", ""))
        );
    }

    @Test
    public void createPlanet_WithExistingName_ThrowsException() {

        Planet planetSave = testEntityManager.persistFlushFind(PLANET);
        testEntityManager.clear();
        planetSave.setId(null);
        assertThrows(RuntimeException.class, () -> planetRepository.save(planetSave));
    }

    @Test
    void getPlanet_byExistingId_ReturnPlanet() {
        Planet planet = testEntityManager.persistFlushFind(PLANET);

        Optional<Planet> planetOpt = planetRepository.findById(planet.getId());

        assertTrue(planetOpt.isPresent());
        assertEquals(PLANET, planetOpt.get());
    }

    @Test
    void getPlanet_byExistingId_ReturnEmpty() {
        Optional<Planet> planetOpt = planetRepository.findById(0L);
        assertTrue(planetOpt.isEmpty());
    }

    @Test
    @Sql(scripts = "/sql/testes/import_planets.sql")
    public void listPlanets_RetuenFilteredPlanets() {
        Example<Planet> queryWithoutFilters = QueryBuilder.makeQuery(new Planet());

        Example<Planet> queryWithFilters = QueryBuilder.makeQuery(new Planet(TATOOINE.getClimate(), TATOOINE.getTerrain()));

        List<Planet> responseWithoutFilters = planetRepository.findAll(queryWithoutFilters);

        List<Planet> responseWithFilters = planetRepository.findAll(queryWithFilters);

        assertFalse(responseWithoutFilters.isEmpty());
        assertEquals(3, responseWithoutFilters.size());
        assertFalse(responseWithFilters.isEmpty());
        assertEquals(1, responseWithFilters.size());
        assertEquals(TATOOINE, responseWithFilters.get(0));
    }

    @Test
    void listPlanets_ReturnsNoPlanets() {
        Example<Planet> query = QueryBuilder.makeQuery(new Planet());

        List<Planet> response = planetRepository.findAll(query);

        assertTrue(response.isEmpty());
    }

    @Test
    public void removePlanet_WithExistingId_RemovePlanetFromDataBase() {
        Planet planet = testEntityManager.persistFlushFind(PLANET);

        planetRepository.deleteById(planet.getId());

        Planet removedPlanet = testEntityManager.find(Planet.class, planet.getId());

        assertNull(removedPlanet);

    }

    /**
     * Nessa versão do spring, quando o repositorio tenta deletar um registro que não existe, não lança mais exceção, age como se o registro tivesse sido deletadow'
     */
    // @Test
    public void removePlanet_WithUnexistingId_ThrowsException() {
        assertThrows(EmptyResultDataAccessException.class, () -> planetRepository.deleteById(1L));
    }


}

















