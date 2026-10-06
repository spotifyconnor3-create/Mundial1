package com.futbolapirest.app.entidades;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import jakarta.validation.constraints.NotBlank;

@Document(collection = "club")
public class club {

    @Id
    private String id;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    // 1 a 1: un club tiene un entrenador
    @DocumentReference
    private entrenador entrenador;

    // 1 a N: un club tiene muchos jugadores
    @DocumentReference
    private List<jugador> jugadores = new ArrayList<>();

    // N a 1: muchos clubes pertenecen a una asociación
    @DocumentReference
    private asociacion asociacion;

    // N a N: un club juega muchas competiciones
    @DocumentReference
    private List<competicion> competiciones = new ArrayList<>();

    public club() {
    }

    public club(String nombre, entrenador entrenador, List<jugador> jugadores,
                asociacion asociacion, List<competicion> competiciones) {
        this.nombre = nombre;
        this.entrenador = entrenador;
        this.jugadores = jugadores;
        this.asociacion = asociacion;
        this.competiciones = competiciones;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public entrenador getEntrenador() { return entrenador; }
    public void setEntrenador(entrenador entrenador) { this.entrenador = entrenador; }

    public List<jugador> getJugadores() { return jugadores; }
    public void setJugadores(List<jugador> jugadores) { this.jugadores = jugadores; }

    public asociacion getAsociacion() { return asociacion; }
    public void setAsociacion(asociacion asociacion) { this.asociacion = asociacion; }

    public List<competicion> getCompeticiones() { return competiciones; }
    public void setCompeticiones(List<competicion> competiciones) { this.competiciones = competiciones; }
}