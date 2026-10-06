package com.futbolapirest.app.controlador;

import java.util.ArrayList;
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
import com.futbolapirest.app.entidades.competicion;
import com.futbolapirest.app.repositorio.club_repositorio;
import com.futbolapirest.app.repositorio.competicion_repositorio;

import jakarta.validation.Valid;

@Controller
public class competicion_web {

    @Autowired
    private competicion_repositorio competicionRepo;

    @Autowired
    private club_repositorio clubRepo;

    // Formulario para registrar
    @GetMapping("/competicion")
    public String formulario(Model model) {
        model.addAttribute("competicion", new competicion());
        return "competicion/index";
    }

    // Formulario para editar
    @GetMapping("/competicion/editar/{id}")
    public String editar(@PathVariable String id, Model model) {
        competicion existente = competicionRepo.findById(id).orElse(null);
        if (existente == null) {
            return "redirect:/competicion/listar";
        }
        model.addAttribute("competicion", existente);
        return "competicion/index";
    }

    // Guardar (crea o actualiza según tenga id)
    @PostMapping("/competicion/guardar")
    public String guardar(@Valid @ModelAttribute("competicion") competicion c, BindingResult resultado) {

        if (c.getId() != null && c.getId().isBlank()) {
            c.setId(null);
        }

        // La fecha de fin no puede ser anterior a la de inicio
        if (c.getFechaInicio() != null && c.getFechaFin() != null
                && c.getFechaFin().isBefore(c.getFechaInicio())) {
            resultado.rejectValue("fechaFin", "fecha.invalida",
                    "La fecha de fin no puede ser anterior a la de inicio");
        }

        if (resultado.hasErrors()) {
            return "competicion/index";
        }

        competicionRepo.save(c);
        return "redirect:/competicion/listar";
    }

    // Listado con búsqueda por nombre
    @GetMapping("/competicion/listar")
    public String listar(@RequestParam(required = false) String buscar, Model model) {

        String filtro = buscar == null ? "" : buscar.trim().toLowerCase();

        List<competicion> lista = competicionRepo.findAll().stream()
                .filter(x -> x.getNombre().toLowerCase().contains(filtro))
                .toList();

        // id de la competición -> nombres de los clubes que participan
        Map<String, List<String>> clubesDe = new HashMap<>();
        for (club c : clubRepo.findAll()) {
            if (c.getCompeticiones() != null) {
                c.getCompeticiones().forEach(x ->
                        clubesDe.computeIfAbsent(x.getId(), k -> new ArrayList<>()).add(c.getNombre()));
            }
        }

        model.addAttribute("competiciones", lista);
        model.addAttribute("clubesDe", clubesDe);
        model.addAttribute("buscar", buscar);
        return "competicion/listar";
    }

    // Eliminar (primero la quita de los clubes que participan)
    @GetMapping("/competicion/eliminar/{id}")
    public String eliminar(@PathVariable String id) {

        for (club c : clubRepo.findAll()) {
            if (c.getCompeticiones() != null && c.getCompeticiones().removeIf(x -> id.equals(x.getId()))) {
                clubRepo.save(c);
            }
        }

        competicionRepo.deleteById(id);
        return "redirect:/competicion/listar";
    }
}