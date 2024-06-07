package br.com.fiap.bluehope.controller;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.NO_CONTENT;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import br.com.fiap.bluehope.model.Desafio;
import br.com.fiap.bluehope.repository.DesafioRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("desafio")
@Slf4j
@CacheConfig(cacheNames = "desafios")
@Tag(name = "Desafios")
public class DesafioController {

    @Autowired
    DesafioRepository repository;

    @GetMapping
    @Cacheable
    @Operation(summary = "Listar todos os desafios", description = "Retorna um array com todos os desafios no formato objeto")
    public List<EntityModel<Desafio>> index() {
        var desafios = repository.findAll();
        return desafios.stream()
                .map(Desafio::toModel)
                .collect(Collectors.toList());
    }

    @GetMapping("{id}")
    @Operation(summary = "Obter detalhes de um desafio", description = "Retorna os detalhes de um desafio específico pelo seu ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Desafio encontrado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Desafio não encontrado")
    })
    public EntityModel<Desafio> show(@PathVariable Long id) {
        log.info("Buscando Desafio com id {}", id);

        var desafio = repository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("Desafio não encontrado"));

        return desafio.toEntityModel();
    }

    @PostMapping
    @ResponseStatus(CREATED)
    @CacheEvict(allEntries = true)
    @Operation(summary = "Cadastrar desafio", description = "Cria um novo desafio com os dados enviados no corpo da requisição.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Desafio cadastrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados enviados são inválidos. Verifique o corpo da requisição", useReturnTypeSchema = false)
    })
    public Desafio create(@RequestBody Desafio desafio) {
        log.info("Cadastrando desafio {}", desafio);
        return repository.save(desafio);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(NO_CONTENT)
    @CacheEvict(allEntries = true)
    @Operation(summary = "Deletar um desafio", description = "Remove um desafio específico pelo seu ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Desafio deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Desafio não encontrado")
    })
    public ResponseEntity<Object> destroy(@PathVariable Long id) {
        repository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("Desafio não encontrado"));

        repository.deleteById(id);

        return ResponseEntity.noContent().build();
    }

    @PutMapping("{id}")
    @CacheEvict(allEntries = true)
    @Operation(summary = "Atualizar um desafio", description = "Atualiza os dados de um desafio específico pelo seu ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Desafio atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Desafio não encontrado")
    })
    public Desafio update(@PathVariable Long id, @RequestBody Desafio desafio) {
        log.info("Atualizar desafio {} para {}", id, desafio);

        verificarSeDesafioExiste(id);
        desafio.setId(id);
        return repository.save(desafio);
    }

    private void verificarSeDesafioExiste(Long id) {
        repository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        NOT_FOUND,
                        "Não existe desafio com o id informado"));
    }
}
