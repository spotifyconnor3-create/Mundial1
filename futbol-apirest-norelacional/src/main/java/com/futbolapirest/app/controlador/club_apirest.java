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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.futbolapirest.app.entidades.club;
import com.futbolapirest.app.repositorio.club_repositorio;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/club")
public class club_apirest {

    @Autowired
    private club_repositorio clubRepo;

    // Listar todos
    @GetMapping
    public List<club> listar() {
        return clubRepo.findAll();
    }

    // Buscar por nombre: /api/club/buscar?nombre=mill
    @GetMapping("/buscar")
    public List<club> buscar(@RequestParam String nombre) {
        return clubRepo.findByNombreContainingIgnoreCase(nombre);
    }

    // Obtener uno por id
    @GetMapping("/{id}")
    public ResponseEntity<club> obtener(@PathVariable String id) {
        return clubRepo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Crear (en el JSON basta con el id de los objetos relacionados)
    @PostMapping
    public ResponseEntity<club> crear(@Valid @RequestBody club nuevo) {
        nuevo.setId(null);
        return ResponseEntity.status(HttpStatus.CREATED).body(clubRepo.save(nuevo));
    }

    // Actualizar
    @PutMapping("/{id}")
    public ResponseEntity<club> actualizar(@PathVariable String id, @Valid @RequestBody club datos) {
        if (!clubRepo.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        datos.setId(id);
        return ResponseEntity.ok(clubRepo.save(datos));
    }

    // Eliminar
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable String id) {
        if (!clubRepo.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        clubRepo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}