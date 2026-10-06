package com.futbolapirest.app.controlador;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.futbolapirest.app.entidades.club;
import com.futbolapirest.app.entidades.competicion;
import com.futbolapirest.app.repositorio.club_repositorio;
import com.futbolapirest.app.repositorio.competicion_repositorio;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/competicion")
public class competicion_apirest {

    @Autowired
    private competicion_repositorio competicionRepo;

    @Autowired
    private club_repositorio clubRepo;

    // Listar todas
    @GetMapping
    public List<competicion> listar() {
        return competicionRepo.findAll();
    }

    // Obtener una por id
    @GetMapping("/{id}")
    public ResponseEntity<competicion> obtener(@PathVariable String id) {
        return competicionRepo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Crear
    @PostMapping
    public ResponseEntity<competicion> crear(@Valid @RequestBody competicion nueva) {
        nueva.setId(null);
        return ResponseEntity.status(HttpStatus.CREATED).body(competicionRepo.save(nueva));
    }

    // Actualizar
    @PutMapping("/{id}")
    public ResponseEntity<competicion> actualizar(@PathVariable String id, @Valid @RequestBody competicion datos) {
        if (!competicionRepo.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        datos.setId(id);
        return ResponseEntity.ok(competicionRepo.save(datos));
    }

    // Eliminar (primero la quita de los clubes que participan)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable String id) {
        if (!competicionRepo.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        for (club c : clubRepo.findAll()) {
            if (c.getCompeticiones() != null && c.getCompeticiones().removeIf(x -> id.equals(x.getId()))) {
                clubRepo.save(c);
            }
        }

        competicionRepo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}