package com.futbolapirest.app.controlador;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.futbolapirest.app.entidades.jugador;
import com.futbolapirest.app.repositorio.club_repositorio;
import com.futbolapirest.app.repositorio.jugador_repositorio;

import jakarta.validation.Valid;

@Controller
public class jugador_web {

    @Autowired
    private jugador_repositorio jugadorRepo;

    @Autowired
    private club_repositorio clubRepo;

    // Formulario para registrar
    @GetMapping("/jugador")
    public String formulario(Model model) {
        model.addAttribute("jugador", new jugador());
        return "jugador/index";
    }

    // Formulario para editar
    @GetMapping("/jugador/editar/{id}")
    public String editar(@PathVariable String id, Model model) {
        jugador existente = jugadorRepo.findById(id).orElse(null);
        if (existente == null) {
            return "redirect:/jugador/listar";
        }
        model.addAttribute("jugador", existente);
        return "jugador/index";
    }

    // Guardar (crea o actualiza según tenga id)
    @PostMapping("/jugador/guardar")
    public String guardar(@Valid @ModelAttribute("jugador") jugador j, BindingResult resultado) {

        if (j.getId() != null && j.getId().isBlank()) {
            j.setId(null);
        }

        if (resultado.hasErrors()) {
            return "jugador/index";
        }

        jugadorRepo.save(j);
        return "redirect:/jugador/listar";
    }

    // Listado con búsqueda por nombre o apellido
    @GetMapping("/jugador/listar")
    public String listar(@RequestParam(required = false) String buscar, Model model) {

        String filtro = buscar == null ? "" : buscar.trim().toLowerCase();

        List<jugador> lista = jugadorRepo.findAll().stream()
                .filter(x -> (x.getNombre() + " " + x.getApellido()).toLowerCase().contains(filtro))
                .toList();

        // id del jugador -> nombre del club al que pertenece
        Map<String, String> clubDe = new HashMap<>();
        for (club c : clubRepo.findAll()) {
            if (c.getJugadores() != null) {
                c.getJugadores().forEach(x -> clubDe.put(x.getId(), c.getNombre()));
            }
        }

        model.addAttribute("jugadores", lista);
        model.addAttribute("clubDe", clubDe);
        model.addAttribute("buscar", buscar);
        return "jugador/listar";
    }

    // Eliminar (primero lo saca del club donde esté)
    @GetMapping("/jugador/eliminar/{id}")
    public String eliminar(@PathVariable String id) {

        for (club c : clubRepo.findAll()) {
            if (c.getJugadores() != null && c.getJugadores().removeIf(x -> id.equals(x.getId()))) {
                clubRepo.save(c);
            }
        }

        jugadorRepo.deleteById(id);
        return "redirect:/jugador/listar";
    }
}