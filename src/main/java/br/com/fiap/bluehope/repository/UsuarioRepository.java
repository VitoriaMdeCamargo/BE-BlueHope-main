package br.com.fiap.bluehope.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.fiap.bluehope.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
}
