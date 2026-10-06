package com.futbolapirest.app.repositorio;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.futbolapirest.app.entidades.jugador;

public interface jugador_repositorio extends MongoRepository<jugador, String> {
}