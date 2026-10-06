package com.futbolapirest.app.repositorio;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.futbolapirest.app.entidades.asociacion;

public interface asociacion_repositorio extends MongoRepository<asociacion, String> {
}