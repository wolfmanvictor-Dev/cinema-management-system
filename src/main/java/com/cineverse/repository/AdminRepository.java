package com.cineverse.repository;

import com.cineverse.model.Admin;

import java.util.Optional;

public interface AdminRepository {
    Optional<Admin> buscarPorUsuario(String usuario);
    Admin salvar(Admin admin);
}
