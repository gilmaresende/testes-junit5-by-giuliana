package com.testes.startwas.startwas.common;

import com.testes.startwas.startwas.domain.Planet;

import java.util.ArrayList;
import java.util.List;

public class PlanetConstants {

    public static final Planet PLANET = new Planet("Terra", "atmosferico", "terra");

    public static final Planet INVALID_PLANET = new Planet("", "", "");

    public static final Planet TATOOINE = new Planet(1L, "Tatooine", "arid", "desert");

    public static final Planet ALDERAAN = new Planet(2L, "Alderaan", "temperate", "grass");

    public static final Planet YAVINIV = new Planet(2L, "Yavin IV", "temperate, tropical", "grass");

    public static final List<Planet> PLANETS = new ArrayList<>() {
        {
            add(TATOOINE);
            add(ALDERAAN);
            add(YAVINIV);
        }
    };




}
