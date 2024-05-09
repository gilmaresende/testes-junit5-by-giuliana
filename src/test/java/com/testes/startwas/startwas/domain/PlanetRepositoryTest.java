package com.testes.startwas.startwas.domain;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static com.testes.startwas.startwas.common.PlanetConstants.PLANET;
import static org.junit.jupiter.api.Assertions.*;

//@SpringBootTest(classes = PlanetRepository.class)
@DataJpaTest
public class PlanetRepositoryTest {

    @Autowired
    private PlanetRepository planetRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    public void createPlanet_WithValidData_ReturnsPlanet() {
        Planet planet = planetRepository.save(PLANET);

        Planet sut = testEntityManager.find(Planet.class, planet.getId());

        assertNotNull(sut);
        assertEquals(PLANET.getName(), sut.getName());
        assertEquals(PLANET.getClimate(), sut.getClimate());
        assertEquals(PLANET.getTerrain(), sut.getTerrain());

    }

    @Test
    public void createPlanet_WithInvalidData_ThrowsException() {
        Planet emptyPlanet = new Planet();

        Planet invalidPlanet = new Planet("", "", "");

        assertThrows(RuntimeException.class, () -> planetRepository.save(emptyPlanet));
        assertThrows(RuntimeException.class, () -> planetRepository.save(invalidPlanet));
    }

    public void createPlanet_WithExistingName_ThrowsExcepition() {

        Planet planetSave = testEntityManager.persistFlushFind(PLANET);
        testEntityManager.detach(PLANET);
        planetSave.setId(null);

        assertThrows(RuntimeException.class, () -> planetRepository.save(planetSave));
    }
}

















