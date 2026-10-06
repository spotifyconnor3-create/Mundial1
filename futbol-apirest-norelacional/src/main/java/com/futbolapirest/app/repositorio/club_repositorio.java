package com.futbolapirest.app.repositorio;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.futbolapirest.app.entidades.club;

public interface club_repositorio extends MongoRepository<club, String> {

    List<club> findByNombreContainingIgnoreCase(String nombre);
}