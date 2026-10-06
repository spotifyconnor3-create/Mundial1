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

import com.futbolapirest.app.entidades.asociacion;
import com.futbolapirest.app.entidades.club;
import com.futbolapirest.app.repositorio.asociacion_repositorio;
import com.futbolapirest.app.repositorio.club_repositorio;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/asociacion")
public class asociacion_apirest {

    @Autowired
    private asociacion_repositorio asociacionRepo;

    @Autowired
    private club_repositorio clubRepo;

    // Listar todas
    @GetMapping
    public List<asociacion> listar() {
        return asociacionRepo.findAll();
    }

    // Obtener una por id
    @GetMapping("/{id}")
    public ResponseEntity<asociacion> obtener(@PathVariable String id) {
        return asociacionRepo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Crear
    @PostMapping
    public ResponseEntity<asociacion> crear(@Valid @RequestBody asociacion nueva) {
        nueva.setId(null);
        return ResponseEntity.status(HttpStatus.CREATED).body(asociacionRepo.save(nueva));
    }

    // Actualizar
    @PutMapping("/{id}")
    public ResponseEntity<asociacion> actualizar(@PathVariable String id, @Valid @RequestBody asociacion datos) {
        if (!asociacionRepo.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        datos.setId(id);
        return ResponseEntity.ok(asociacionRepo.save(datos));
    }

    // Eliminar (primero la quita de los clubes afiliados)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable String id) {
        if (!asociacionRepo.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        for (club c : clubRepo.findAll()) {
            if (c.getAsociacion() != null && id.equals(c.getAsociacion().getId())) {
                c.setAsociacion(null);
                clubRepo.save(c);
            }
        }

        asociacionRepo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}