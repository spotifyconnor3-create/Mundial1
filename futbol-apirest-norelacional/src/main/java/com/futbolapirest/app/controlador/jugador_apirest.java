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
import com.futbolapirest.app.entidades.jugador;
import com.futbolapirest.app.repositorio.club_repositorio;
import com.futbolapirest.app.repositorio.jugador_repositorio;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/jugador")
public class jugador_apirest {

    @Autowired
    private jugador_repositorio jugadorRepo;

    @Autowired
    private club_repositorio clubRepo;

    // Listar todos
    @GetMapping
    public List<jugador> listar() {
        return jugadorRepo.findAll();
    }

    // Obtener uno por id
    @GetMapping("/{id}")
    public ResponseEntity<jugador> obtener(@PathVariable String id) {
        return jugadorRepo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Crear
    @PostMapping
    public ResponseEntity<jugador> crear(@Valid @RequestBody jugador nuevo) {
        nuevo.setId(null);
        return ResponseEntity.status(HttpStatus.CREATED).body(jugadorRepo.save(nuevo));
    }

    // Actualizar
    @PutMapping("/{id}")
    public ResponseEntity<jugador> actualizar(@PathVariable String id, @Valid @RequestBody jugador datos) {
        if (!jugadorRepo.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        datos.setId(id);
        return ResponseEntity.ok(jugadorRepo.save(datos));
    }

    // Eliminar (primero lo saca del club donde esté)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable String id) {
        if (!jugadorRepo.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        for (club c : clubRepo.findAll()) {
            if (c.getJugadores() != null && c.getJugadores().removeIf(x -> id.equals(x.getId()))) {
                clubRepo.save(c);
            }
        }

        jugadorRepo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}