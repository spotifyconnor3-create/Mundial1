package com.futbolapirest.app.controlador;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.futbolapirest.app.entidades.club;
import com.futbolapirest.app.repositorio.asociacion_repositorio;
import com.futbolapirest.app.repositorio.club_repositorio;
import com.futbolapirest.app.repositorio.competicion_repositorio;
import com.futbolapirest.app.repositorio.entrenador_repositorio;
import com.futbolapirest.app.repositorio.jugador_repositorio;

import jakarta.validation.Valid;

@Controller
public class club_web {

    @Autowired
    private club_repositorio clubRepo;

    @Autowired
    private asociacion_repositorio asociacionRepo;

    @Autowired
    private competicion_repositorio competicionRepo;

    @Autowired
    private entrenador_repositorio entrenadorRepo;

    @Autowired
    private jugador_repositorio jugadorRepo;

    // Landing page
    @GetMapping("/")
    public String inicio() {
        return "index";
    }

    // Formulario para registrar un club
    @GetMapping("/club")
    public String formulario(Model model) {
        model.addAttribute("club", new club());
        cargarListas(model, null);
        return "club/index";
    }

    // Formulario para editar un club
    @GetMapping("/club/editar/{id}")
    public String editar(@PathVariable String id, Model model) {
        club existente = clubRepo.findById(id).orElse(null);
        if (existente == null) {
            return "redirect:/club/listar";
        }
        model.addAttribute("club", existente);
        cargarListas(model, id);
        return "club/index";
    }

    // Guardar (crea o actualiza según tenga id)
    @PostMapping("/club/guardar")
    public String guardar(@Valid @ModelAttribute("club") club c,
                          BindingResult resultado,
                          @RequestParam(required = false) String entrenadorId,
                          @RequestParam(required = false) String asociacionId,
                          @RequestParam(required = false) List<String> jugadoresIds,
                          @RequestParam(required = false) List<String> competicionesIds,
                          Model model) {

        // El campo oculto llega vacío cuando el club es nuevo
        if (c.getId() != null && c.getId().isBlank()) {
            c.setId(null);
        }

        if (resultado.hasErrors()) {
            cargarListas(model, c.getId());
            return "club/index";
        }

        c.setEntrenador(entrenadorId == null || entrenadorId.isBlank()
                ? null
                : entrenadorRepo.findById(entrenadorId).orElse(null));

        c.setAsociacion(asociacionId == null || asociacionId.isBlank()
                ? null
                : asociacionRepo.findById(asociacionId).orElse(null));

        c.setJugadores(jugadoresIds == null
                ? new ArrayList<>()
                : jugadorRepo.findAllById(jugadoresIds));

        c.setCompeticiones(competicionesIds == null
                ? new ArrayList<>()
                : competicionRepo.findAllById(competicionesIds));

        clubRepo.save(c);
        return "redirect:/club/listar";
    }

    // Listado, con búsqueda opcional por nombre
    @GetMapping("/club/listar")
    public String listar(@RequestParam(required = false) String buscar, Model model) {
        List<club> lista = (buscar == null || buscar.isBlank())
                ? clubRepo.findAll()
                : clubRepo.findByNombreContainingIgnoreCase(buscar);

        model.addAttribute("clubes", lista);
        model.addAttribute("buscar", buscar);
        return "club/listar";
    }

    // Eliminar
    @GetMapping("/club/eliminar/{id}")
    public String eliminar(@PathVariable String id) {
        clubRepo.deleteById(id);
        return "redirect:/club/listar";
    }

    // Listas para el formulario.
    // Jugadores y entrenadores que ya están en OTRO club no se muestran.
    // clubActualId es el club que se está editando (null si es nuevo).
    private void cargarListas(Model model, String clubActualId) {

        Set<String> jugadoresOcupados = new HashSet<>();
        Set<String> entrenadoresOcupados = new HashSet<>();

        for (club otro : clubRepo.findAll()) {

            if (clubActualId != null && clubActualId.equals(otro.getId())) {
                continue;
            }

            if (otro.getJugadores() != null) {
                otro.getJugadores().forEach(j -> jugadoresOcupados.add(j.getId()));
            }

            if (otro.getEntrenador() != null) {
                entrenadoresOcupados.add(otro.getEntrenador().getId());
            }
        }

        model.addAttribute("asociaciones", asociacionRepo.findAll());
        model.addAttribute("competiciones", competicionRepo.findAll());

        model.addAttribute("jugadores", jugadorRepo.findAll().stream()
                .filter(j -> !jugadoresOcupados.contains(j.getId()))
                .toList());

        model.addAttribute("entrenadores", entrenadorRepo.findAll().stream()
                .filter(e -> !entrenadoresOcupados.contains(e.getId()))
                .toList());
    }
}