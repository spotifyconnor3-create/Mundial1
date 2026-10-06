package com.futbolapirest.app;

import java.time.LocalDate;
import java.util.Arrays;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.futbolapirest.app.entidades.asociacion;
import com.futbolapirest.app.entidades.club;
import com.futbolapirest.app.entidades.competicion;
import com.futbolapirest.app.entidades.entrenador;
import com.futbolapirest.app.entidades.jugador;
import com.futbolapirest.app.repositorio.asociacion_repositorio;
import com.futbolapirest.app.repositorio.club_repositorio;
import com.futbolapirest.app.repositorio.competicion_repositorio;
import com.futbolapirest.app.repositorio.entrenador_repositorio;
import com.futbolapirest.app.repositorio.jugador_repositorio;

@SpringBootApplication
public class FutbolApirestNorelacionalApplication {

    public static void main(String[] args) {
        SpringApplication.run(FutbolApirestNorelacionalApplication.class, args);
    }

    @Bean
    CommandLineRunner datosIniciales(asociacion_repositorio asociaciones,
                                     competicion_repositorio competiciones,
                                     entrenador_repositorio entrenadores,
                                     jugador_repositorio jugadores,
                                     club_repositorio clubes) {
        return args -> {

            // Si ya hay clubes, no se vuelve a cargar nada
            if (clubes.count() > 0) {
                return;
            }

            // 1. ASOCIACIONES
            asociacion fcf = asociaciones.save(
                    new asociacion("Federación Colombiana de Fútbol", "Colombia", "Ramón Castillo"));
            asociacion rfef = asociaciones.save(
                    new asociacion("Real Federación Española de Fútbol", "España", "Luis Barrios"));
            asociaciones.save(
                    new asociacion("Asociación del Fútbol Argentino", "Argentina", "Claudio Méndez"));

            // 2. COMPETICIONES
            competicion libertadores = competiciones.save(
                    new competicion("Copa Libertadores", 20000000L,
                            LocalDate.of(2026, 2, 1), LocalDate.of(2026, 11, 28)));
            competicion sudamericana = competiciones.save(
                    new competicion("Copa Sudamericana", 8000000L,
                            LocalDate.of(2026, 3, 1), LocalDate.of(2026, 11, 21)));
            competicion betplay = competiciones.save(
                    new competicion("Liga BetPlay", 1500000L,
                            LocalDate.of(2026, 1, 20), LocalDate.of(2026, 12, 15)));
            competicion champions = competiciones.save(
                    new competicion("Liga de Campeones", 25000000L,
                            LocalDate.of(2026, 9, 15), LocalDate.of(2027, 5, 30)));

            // 3. ENTRENADORES
            entrenador entMillonarios = entrenadores.save(
                    new entrenador("Andrés", "Vargas", 52, "Colombiana"));
            entrenador entSantaFe = entrenadores.save(
                    new entrenador("Julián", "Moreno", 47, "Colombiana"));
            entrenador entMadrid = entrenadores.save(
                    new entrenador("Carlos", "Herrera", 55, "Española"));
            entrenador entBarcelona = entrenadores.save(
                    new entrenador("Marc", "Soler", 49, "Española"));

            // 4. JUGADORES (3 por club)
            var jugMillonarios = jugadores.saveAll(Arrays.asList(
                    new jugador("Daniel", "Ospina", 1, "Portero"),
                    new jugador("Felipe", "Cardona", 8, "Mediocampista"),
                    new jugador("Sebastián", "Rincón", 9, "Delantero")));

            var jugSantaFe = jugadores.saveAll(Arrays.asList(
                    new jugador("Camilo", "Pérez", 1, "Portero"),
                    new jugador("Mateo", "Salazar", 5, "Defensa"),
                    new jugador("Juan", "Acosta", 10, "Mediocampista")));

            var jugMadrid = jugadores.saveAll(Arrays.asList(
                    new jugador("Álvaro", "Reyes", 1, "Portero"),
                    new jugador("Pablo", "Navarro", 4, "Defensa"),
                    new jugador("Diego", "Fuentes", 9, "Delantero")));

            var jugBarcelona = jugadores.saveAll(Arrays.asList(
                    new jugador("Jordi", "Castells", 1, "Portero"),
                    new jugador("Pau", "Ferrer", 6, "Mediocampista"),
                    new jugador("Adrià", "Molina", 11, "Delantero")));

            // 5. CLUBES (ya enlazados con todo lo anterior)
            clubes.save(new club("Millonarios", entMillonarios, jugMillonarios, fcf,
                    Arrays.asList(betplay, libertadores)));

            clubes.save(new club("Santa Fe", entSantaFe, jugSantaFe, fcf,
                    Arrays.asList(betplay, sudamericana)));

            clubes.save(new club("Real Madrid", entMadrid, jugMadrid, rfef,
                    Arrays.asList(champions)));

            clubes.save(new club("Barcelona", entBarcelona, jugBarcelona, rfef,
                    Arrays.asList(champions)));

            System.out.println(">>> Datos iniciales de fútbol cargados en MongoDB");
        };
    }
}