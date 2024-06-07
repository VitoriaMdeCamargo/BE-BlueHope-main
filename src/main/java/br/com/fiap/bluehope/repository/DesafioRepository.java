package br.com.fiap.bluehope.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.fiap.bluehope.model.Desafio;

public interface DesafioRepository extends JpaRepository<Desafio, Long> {
}
