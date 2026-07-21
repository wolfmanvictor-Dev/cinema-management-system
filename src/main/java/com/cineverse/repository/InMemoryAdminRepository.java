package com.cineverse.repository;

import com.cineverse.model.Admin;
import com.cineverse.security.HashUtil;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryAdminRepository implements AdminRepository {

    private final Map<String, Admin> dados = new ConcurrentHashMap<>();
    private final AtomicLong sequencia = new AtomicLong(0);

    /**
     * Cria o administrador padrão assim que a aplicação sobe, para que o
     * login funcione "de fábrica" sem precisar de nenhum cadastro manual.
     * Usuário: admin | Senha: admin123
     */
    @PostConstruct
    public void seed() {
        Admin admin = new Admin(sequencia.incrementAndGet(), "admin", HashUtil.sha256("admin123"), "Administrador");
        dados.put(admin.getUsuario(), admin);
    }

    @Override
    public Optional<Admin> buscarPorUsuario(String usuario) {
        return Optional.ofNullable(dados.get(usuario));
    }

    @Override
    public Admin salvar(Admin admin) {
        if (admin.getId() == null) {
            admin.setId(sequencia.incrementAndGet());
        }
        dados.put(admin.getUsuario(), admin);
        return admin;
    }
}
