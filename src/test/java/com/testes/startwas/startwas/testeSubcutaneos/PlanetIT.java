package com.testes.startwas.startwas.testeSubcutaneos;

import com.testes.startwas.startwas.domain.Planet;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import static com.testes.startwas.startwas.common.PlanetConstants.PLANET;
import static com.testes.startwas.startwas.common.PlanetConstants.TATOOINE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ActiveProfiles("it")
@Sql(scripts = {"/sql/testes/remove_planets.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@Sql(scripts = {"/sql/testes/import_planets.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PlanetIT {

    @Autowired
    TestRestTemplate restTemplate;

    @Test
    void createPlanet_ReturnCreated() {
        ResponseEntity<Planet> set = restTemplate.postForEntity("/planets", PLANET, Planet.class);

        assertEquals(HttpStatus.CREATED, set.getStatusCode());
        assertNotNull(set.getBody());
        assertNotNull(set.getBody().getId());
        assertEquals(PLANET.getName(), set.getBody().getName());
        assertEquals(PLANET.getClimate(), set.getBody().getClimate());
        assertEquals(PLANET.getTerrain(), set.getBody().getTerrain());

    }

    @Test
    public void getPlanet_ReturnCreate() {
        ResponseEntity<Planet> set = restTemplate.getForEntity("/planets/1", Planet.class);

        assertEquals(HttpStatus.OK, set.getStatusCode());
        assertEquals(TATOOINE, set.getBody());
    }

    @Test
    public void getPlanetByName_ReturnPlanet() {
        ResponseEntity<Planet> set = restTemplate.getForEntity("/planets/name/" + TATOOINE.getName(), Planet.class);

        assertEquals(HttpStatus.OK, set.getStatusCode());
        assertEquals(TATOOINE, set.getBody());
    }

    @Test
    public void listPlanets_ReturnAllPlanet() {
        ResponseEntity<Planet[]> set = restTemplate.getForEntity("/planets", Planet[].class);

        assertEquals(HttpStatus.OK, set.getStatusCode());
        assertNotNull(set.getBody());
        assertEquals(3, set.getBody().length);
    }

    @Test
    public void listPlanets_ByTerrain_ReturnPlanet() {
        ResponseEntity<Planet[]> set = restTemplate.getForEntity(String.format("/planets?terrain=%s", TATOOINE.getTerrain()), Planet[].class);

        assertEquals(HttpStatus.OK, set.getStatusCode());
        assertNotNull(set.getBody());
        assertEquals(1, set.getBody().length);
    }

    @Test
    public void listPlanets_ByClimate_ReturnPlanet() {
        ResponseEntity<Planet[]> set = restTemplate.getForEntity(String.format("/planets?climate=%s", TATOOINE.getClimate()), Planet[].class);
        assertEquals(HttpStatus.OK, set.getStatusCode());
        assertNotNull(set.getBody());
        assertEquals(1, set.getBody().length);
    }

    @Test
    public void removePlanet_ByClimate_ReturnNoContent() {
        ResponseEntity<Void> set = restTemplate.exchange("/planets/" + TATOOINE.getId(), HttpMethod.DELETE, null, Void.class);

        assertEquals(HttpStatus.NO_CONTENT, set.getStatusCode());
    }

}
