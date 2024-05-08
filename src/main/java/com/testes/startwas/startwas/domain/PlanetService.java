package com.testes.startwas.startwas.domain;

import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PlanetService {
    private final PlanetRepository planetRepository;

    public PlanetService(PlanetRepository planetRepository) {
        this.planetRepository = planetRepository;
    }

    public Planet create(Planet planet) {
        return planetRepository.save(planet);
    }

    public Optional<Planet> findById(Long id) {
        return planetRepository.findById(id);
    }

    public Optional<Planet> getByName(String name) {
        return planetRepository.findByName(name);
    }

    public List<Planet> list(String terrain, String climate) {
        Planet filter = new Planet();
        filter.setClimate(climate);
        filter.setTerrain(terrain);
        Example<Planet> query = QueryBuilder.makeQuery(filter);
        return planetRepository.findAll(query);
    }

    public void remove(Long id) {
        this.planetRepository.deleteById(id);
    }
}
