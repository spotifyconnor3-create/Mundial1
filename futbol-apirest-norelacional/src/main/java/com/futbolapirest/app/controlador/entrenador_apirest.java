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
import com.futbolapirest.app.entidades.entrenador;
import com.futbolapirest.app.repositorio.club_repositorio;
import com.futbolapirest.app.repositorio.entrenador_repositorio;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/entrenador")
public class entrenador_apirest {

    @Autowired
    private entrenador_repositorio entrenadorRepo;

    @Autowired
    private club_repositorio clubRepo;

    // Listar todos
    @GetMapping
    public List<entrenador> listar() {
        return entrenadorRepo.findAll();
    }

    // Obtener uno por id
    @GetMapping("/{id}")
    public ResponseEntity<entrenador> obtener(@PathVariable String id) {
        return entrenadorRepo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Crear
    @PostMapping
    public ResponseEntity<entrenador> crear(@Valid @RequestBody entrenador nuevo) {
        nuevo.setId(null);
        return ResponseEntity.status(HttpStatus.CREATED).body(entrenadorRepo.save(nuevo));
    }

    // Actualizar
    @PutMapping("/{id}")
    public ResponseEntity<entrenador> actualizar(@PathVariable String id, @Valid @RequestBody entrenador datos) {
        if (!entrenadorRepo.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        datos.setId(id);
        return ResponseEntity.ok(entrenadorRepo.save(datos));
    }

    // Eliminar (primero lo saca del club que dirige)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable String id) {
        if (!entrenadorRepo.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        for (club c : clubRepo.findAll()) {
            if (c.getEntrenador() != null && id.equals(c.getEntrenador().getId())) {
                c.setEntrenador(null);
                clubRepo.save(c);
            }
        }

        entrenadorRepo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}