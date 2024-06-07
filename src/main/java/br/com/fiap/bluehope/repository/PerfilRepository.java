package br.com.fiap.bluehope.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.fiap.bluehope.model.Perfil;

public interface PerfilRepository extends JpaRepository<Perfil, Long> {
}
