package com.futbolapirest.app.entidades;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotBlank;

@Document(collection = "asociacion")
public class asociacion {

    @Id
    private String id;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El país es obligatorio")
    private String pais;

    @NotBlank(message = "El presidente es obligatorio")
    private String presidente;

    public asociacion() {
    }

    public asociacion(String nombre, String pais, String presidente) {
        this.nombre = nombre;
        this.pais = pais;
        this.presidente = presidente;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getPais() { return pais; }
    public void setPais(String pais) { this.pais = pais; }

    public String getPresidente() { return presidente; }
    public void setPresidente(String presidente) { this.presidente = presidente; }
}