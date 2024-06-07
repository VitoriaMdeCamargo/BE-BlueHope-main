package br.com.fiap.bluehope.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.fiap.bluehope.model.Foto;

public interface FotoRepository extends JpaRepository<Foto, Long> {
}
