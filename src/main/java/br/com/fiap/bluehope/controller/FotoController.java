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

import br.com.fiap.bluehope.model.Foto;
import br.com.fiap.bluehope.repository.FotoRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("foto")
@Slf4j
@CacheConfig(cacheNames = "fotos")
@Tag(name = "Fotos")
public class FotoController {

    @Autowired
    FotoRepository repository;

    @GetMapping
    @Cacheable
    @Operation(summary = "Listar todas as fotos", description = "Retorna um array com todas as fotos no formato objeto")
    public List<EntityModel<Foto>> index() {
        var fotos = repository.findAll();
        return fotos.stream()
                .map(Foto::toModel)
                .collect(Collectors.toList());
    }

    @GetMapping("{id}")
    @Operation(summary = "Obter detalhes de uma foto", description = "Retorna os detalhes de uma foto específica pelo seu ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Foto encontrada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Foto não encontrada")
    })
    public EntityModel<Foto> show(@PathVariable Long id) {
        log.info("Buscando Foto com id {}", id);

        var foto = repository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("Foto não encontrada"));

        return foto.toEntityModel();
    }

    @PostMapping
    @ResponseStatus(CREATED)
    @CacheEvict(allEntries = true)
    @Operation(summary = "Cadastrar foto", description = "Cria uma nova foto com os dados enviados no corpo da requisição.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Foto cadastrada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados enviados são inválidos. Verifique o corpo da requisição", useReturnTypeSchema = false)
    })
    public Foto create(@RequestBody Foto foto) {
        log.info("Cadastrando foto {}", foto);
        return repository.save(foto);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(NO_CONTENT)
    @CacheEvict(allEntries = true)
    @Operation(summary = "Deletar uma foto", description = "Remove uma foto específica pelo seu ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Foto deletada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Foto não encontrada")
    })
    public ResponseEntity<Object> destroy(@PathVariable Long id) {
        repository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("Foto não encontrada"));

        repository.deleteById(id);

        return ResponseEntity.noContent().build();
    }

    @PutMapping("{id}")
    @CacheEvict(allEntries = true)
    @Operation(summary = "Atualizar uma foto", description = "Atualiza os dados de uma foto específica pelo seu ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Foto atualizada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Foto não encontrada")
    })
    public Foto update(@PathVariable Long id, @RequestBody Foto foto) {
        log.info("Atualizar foto {} para {}", id, foto);

        verificarSeFotoExiste(id);
        foto.setId(id);
        return repository.save(foto);
    }

    private void verificarSeFotoExiste(Long id) {
        repository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        NOT_FOUND,
                        "Não existe foto com o id informado"));
    }
}
