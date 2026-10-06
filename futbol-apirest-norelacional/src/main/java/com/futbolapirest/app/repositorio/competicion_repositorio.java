package com.futbolapirest.app.repositorio;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.futbolapirest.app.entidades.competicion;

public interface competicion_repositorio extends MongoRepository<competicion, String> {
}