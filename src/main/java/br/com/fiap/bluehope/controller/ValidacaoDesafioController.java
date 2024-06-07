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

import br.com.fiap.bluehope.model.ValidacaoDesafio;
import br.com.fiap.bluehope.repository.ValidacaoDesafioRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("validacaodesafio")
@Slf4j
@CacheConfig(cacheNames = "validacaodesafios")
@Tag(name = "ValidacaoDesafios")
public class ValidacaoDesafioController {

    @Autowired
    ValidacaoDesafioRepository repository;

    @GetMapping
    @Cacheable
    @Operation(summary = "Listar todas as validações de desafios", description = "Retorna um array com todas as validações de desafios no formato objeto")
    public List<EntityModel<ValidacaoDesafio>> index() {
        var validacaodesafios = repository.findAll();
        return validacaodesafios.stream()
                .map(ValidacaoDesafio::toModel)
                .collect(Collectors.toList());
    }

    @GetMapping("{id}")
    @Operation(summary = "Obter detalhes de uma validação de desafio", description = "Retorna os detalhes de uma validação de desafio específica pelo seu ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Validação de desafio encontrada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Validação de desafio não encontrada")
    })
    public EntityModel<ValidacaoDesafio> show(@PathVariable Long id) {
        log.info("Buscando Validação de Desafio com id {}", id);

        var validacaodesafio = repository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("Validação de desafio não encontrada"));

        return validacaodesafio.toEntityModel();
    }

    @PostMapping
    @ResponseStatus(CREATED)
    @CacheEvict(allEntries = true)
    @Operation(summary = "Cadastrar validação de desafio", description = "Cria uma nova validação de desafio com os dados enviados no corpo da requisição.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Validação de desafio cadastrada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados enviados são inválidos. Verifique o corpo da requisição", useReturnTypeSchema = false)
    })
    public ValidacaoDesafio create(@RequestBody ValidacaoDesafio validacaodesafio) {
        log.info("Cadastrando validação de desafio {}", validacaodesafio);
        return repository.save(validacaodesafio);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(NO_CONTENT)
    @CacheEvict(allEntries = true)
    @Operation(summary = "Deletar uma validação de desafio", description = "Remove uma validação de desafio específica pelo seu ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Validação de desafio deletada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Validação de desafio não encontrada")
    })
    public ResponseEntity<Object> destroy(@PathVariable Long id) {
        repository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("Validação de desafio não encontrada"));

        repository.deleteById(id);

        return ResponseEntity.noContent().build();
    }

    @PutMapping("{id}")
    @CacheEvict(allEntries = true)
    @Operation(summary = "Atualizar uma validação de desafio", description = "Atualiza os dados de uma validação de desafio específica pelo seu ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Validação de desafio atualizada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Validação de desafio não encontrada")
    })
    public ValidacaoDesafio update(@PathVariable Long id, @RequestBody ValidacaoDesafio validacaodesafio) {
        log.info("Atualizar validação de desafio {} para {}", id, validacaodesafio);

        verificarSeValidacaoDesafioExiste(id);
        validacaodesafio.setId(id);
        return repository.save(validacaodesafio);
    }

    private void verificarSeValidacaoDesafioExiste(Long id) {
        repository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        NOT_FOUND,
                        "Não existe validação de desafio com o id informado"));
    }
}
