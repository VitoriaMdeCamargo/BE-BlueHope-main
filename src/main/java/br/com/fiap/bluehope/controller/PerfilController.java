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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import br.com.fiap.bluehope.model.Perfil;
import br.com.fiap.bluehope.repository.PerfilRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("perfil")
@Slf4j
@CacheConfig(cacheNames = "perfis")
@Tag(name = "Perfis")
public class PerfilController {

    @Autowired
    PerfilRepository repository;

    @GetMapping
    @Cacheable
    @Operation(summary = "Listar todos os perfis", description = "Retorna um array com todos os perfis no formato objeto")
    public List<EntityModel<Perfil>> index() {
        var perfis = repository.findAll();
        return perfis.stream()
                .map(Perfil::toModel)
                .collect(Collectors.toList());
    }

    @GetMapping("{id}")
    @Operation(summary = "Obter detalhes de um perfil", description = "Retorna os detalhes de um perfil específico pelo seu ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Perfil encontrado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Perfil não encontrado")
    })
    public EntityModel<Perfil> show(@PathVariable Long id) {
        log.info("Buscando Perfil com id {}", id);

        var perfil = repository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("Perfil não encontrado"));

        return perfil.toEntityModel();
    }

    @PostMapping
    @ResponseStatus(CREATED)
    @CacheEvict(allEntries = true)
    @Operation(summary = "Cadastrar perfil", description = "Cria um novo perfil com os dados enviados no corpo da requisição.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Perfil cadastrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados enviados são inválidos. Verifique o corpo da requisição", useReturnTypeSchema = false)
    })
    public Perfil create(@RequestBody Perfil perfil) {
        log.info("Cadastrando perfil {}", perfil);
        return repository.save(perfil);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(NO_CONTENT)
    @CacheEvict(allEntries = true)
    @Operation(summary = "Deletar um perfil", description = "Remove um perfil específico pelo seu ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Perfil deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Perfil não encontrado")
    })
    public ResponseEntity<Object> destroy(@PathVariable Long id) {
        repository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("Perfil não encontrado"));

        repository.deleteById(id);

        return ResponseEntity.noContent().build();
    }

    @PutMapping("{id}")
    @CacheEvict(allEntries = true)
    @Operation(summary = "Atualizar um perfil", description = "Atualiza os dados de um perfil específico pelo seu ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Perfil atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Perfil não encontrado")
    })
    public Perfil update(@PathVariable Long id, @RequestBody Perfil perfil) {
        log.info("Atualizar perfil {} para {}", id, perfil);

        verificarSePerfilExiste(id);
        perfil.setId(id);
        return repository.save(perfil);
    }

    private void verificarSePerfilExiste(Long id) {
        repository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        NOT_FOUND,
                        "Não existe perfil com o id informado"));
    }
}
