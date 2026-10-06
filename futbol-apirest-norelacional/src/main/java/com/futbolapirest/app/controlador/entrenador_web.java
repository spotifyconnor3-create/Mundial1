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
import com.futbolapirest.app.entidades.entrenador;
import com.futbolapirest.app.repositorio.club_repositorio;
import com.futbolapirest.app.repositorio.entrenador_repositorio;

import jakarta.validation.Valid;

@Controller
public class entrenador_web {

    @Autowired
    private entrenador_repositorio entrenadorRepo;

    @Autowired
    private club_repositorio clubRepo;

    // Formulario para registrar
    @GetMapping("/entrenador")
    public String formulario(Model model) {
        model.addAttribute("entrenador", new entrenador());
        return "entrenador/index";
    }

    // Formulario para editar
    @GetMapping("/entrenador/editar/{id}")
    public String editar(@PathVariable String id, Model model) {
        entrenador existente = entrenadorRepo.findById(id).orElse(null);
        if (existente == null) {
            return "redirect:/entrenador/listar";
        }
        model.addAttribute("entrenador", existente);
        return "entrenador/index";
    }

    // Guardar (crea o actualiza según tenga id)
    @PostMapping("/entrenador/guardar")
    public String guardar(@Valid @ModelAttribute("entrenador") entrenador e, BindingResult resultado) {

        if (e.getId() != null && e.getId().isBlank()) {
            e.setId(null);
        }

        if (resultado.hasErrors()) {
            return "entrenador/index";
        }

        entrenadorRepo.save(e);
        return "redirect:/entrenador/listar";
    }

    // Listado con búsqueda por nombre o apellido
    @GetMapping("/entrenador/listar")
    public String listar(@RequestParam(required = false) String buscar, Model model) {

        String filtro = buscar == null ? "" : buscar.trim().toLowerCase();

        List<entrenador> lista = entrenadorRepo.findAll().stream()
                .filter(x -> (x.getNombre() + " " + x.getApellido()).toLowerCase().contains(filtro))
                .toList();

        // id del entrenador -> nombre del club que dirige
        Map<String, String> clubDe = new HashMap<>();
        for (club c : clubRepo.findAll()) {
            if (c.getEntrenador() != null) {
                clubDe.put(c.getEntrenador().getId(), c.getNombre());
            }
        }

        model.addAttribute("entrenadores", lista);
        model.addAttribute("clubDe", clubDe);
        model.addAttribute("buscar", buscar);
        return "entrenador/listar";
    }

    // Eliminar (primero lo saca del club que dirige)
    @GetMapping("/entrenador/eliminar/{id}")
    public String eliminar(@PathVariable String id) {

        for (club c : clubRepo.findAll()) {
            if (c.getEntrenador() != null && id.equals(c.getEntrenador().getId())) {
                c.setEntrenador(null);
                clubRepo.save(c);
            }
        }

        entrenadorRepo.deleteById(id);
        return "redirect:/entrenador/listar";
    }
}