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

import com.futbolapirest.app.entidades.asociacion;
import com.futbolapirest.app.entidades.club;
import com.futbolapirest.app.repositorio.asociacion_repositorio;
import com.futbolapirest.app.repositorio.club_repositorio;

import jakarta.validation.Valid;

@Controller
public class asociacion_web {

    @Autowired
    private asociacion_repositorio asociacionRepo;

    @Autowired
    private club_repositorio clubRepo;

    // Formulario para registrar
    @GetMapping("/asociacion")
    public String formulario(Model model) {
        model.addAttribute("asociacion", new asociacion());
        return "asociacion/index";
    }

    // Formulario para editar
    @GetMapping("/asociacion/editar/{id}")
    public String editar(@PathVariable String id, Model model) {
        asociacion existente = asociacionRepo.findById(id).orElse(null);
        if (existente == null) {
            return "redirect:/asociacion/listar";
        }
        model.addAttribute("asociacion", existente);
        return "asociacion/index";
    }

    // Guardar (crea o actualiza según tenga id)
    @PostMapping("/asociacion/guardar")
    public String guardar(@Valid @ModelAttribute("asociacion") asociacion a, BindingResult resultado) {

        if (a.getId() != null && a.getId().isBlank()) {
            a.setId(null);
        }

        if (resultado.hasErrors()) {
            return "asociacion/index";
        }

        asociacionRepo.save(a);
        return "redirect:/asociacion/listar";
    }

    // Listado con búsqueda por nombre o país
    @GetMapping("/asociacion/listar")
    public String listar(@RequestParam(required = false) String buscar, Model model) {

        String filtro = buscar == null ? "" : buscar.trim().toLowerCase();

        List<asociacion> lista = asociacionRepo.findAll().stream()
                .filter(x -> (x.getNombre() + " " + x.getPais()).toLowerCase().contains(filtro))
                .toList();

        // id de la asociación -> nombres de los clubes afiliados
        Map<String, List<String>> clubesDe = new HashMap<>();
        for (club c : clubRepo.findAll()) {
            if (c.getAsociacion() != null) {
                clubesDe.computeIfAbsent(c.getAsociacion().getId(), k -> new ArrayList<>()).add(c.getNombre());
            }
        }

        model.addAttribute("asociaciones", lista);
        model.addAttribute("clubesDe", clubesDe);
        model.addAttribute("buscar", buscar);
        return "asociacion/listar";
    }

    // Eliminar (primero la quita de los clubes afiliados)
    @GetMapping("/asociacion/eliminar/{id}")
    public String eliminar(@PathVariable String id) {

        for (club c : clubRepo.findAll()) {
            if (c.getAsociacion() != null && id.equals(c.getAsociacion().getId())) {
                c.setAsociacion(null);
                clubRepo.save(c);
            }
        }

        asociacionRepo.deleteById(id);
        return "redirect:/asociacion/listar";
    }
}