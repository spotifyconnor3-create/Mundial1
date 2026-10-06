package com.futbolapirest.app.repositorio;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.futbolapirest.app.entidades.entrenador;

public interface entrenador_repositorio extends MongoRepository<entrenador, String> {
}