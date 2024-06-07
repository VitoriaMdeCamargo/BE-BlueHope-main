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

import br.com.fiap.bluehope.model.Usuario;
import br.com.fiap.bluehope.repository.UsuarioRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("usuario")
@Slf4j
@CacheConfig(cacheNames = "usuarios")
@Tag(name = "Usuarios")
public class UsuarioController {

    @Autowired
    UsuarioRepository repository;

    @GetMapping
    @Cacheable
    @Operation(summary = "Listar todos os usuarios", description = "Retorna um array com todas os usuarios no formato objeto")
    public List<EntityModel<Usuario>> index() {
        var usuarios = repository.findAll();
        return usuarios.stream()
                .map(Usuario::toModel)
                .collect(Collectors.toList());
    }

    @GetMapping("{id}")
    @Operation(summary = "Obter detalhes de um usuário", description = "Retorna os detalhes de um usuário específico pelo seu ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuario não encontrada")
    })
    public EntityModel<Usuario> show(@PathVariable Long id) {
        log.info("Buscando Usuario com id {}", id);

        var usuario = repository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("Usuario não encontrada"));

        return usuario.toEntityModel();
    }

    @PostMapping
    @ResponseStatus(CREATED)
    @CacheEvict(allEntries = true)
    @Operation(summary = "Cadastrar usuario", description = "Cria um novo usuario com os dados enviados no corpo da requisição.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuario cadastrada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados enviados são inválidos. Verifique o corpo da requisição", useReturnTypeSchema = false)
    })
    public Usuario create(@RequestBody Usuario usuario) {
        log.info("Cadastrando usuario {}", usuario);
        return repository.save(usuario);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(NO_CONTENT)
    @CacheEvict(allEntries = true)
    @Operation(summary = "Deletar um usuario", description = "Remove um usuario específico pelo seu ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Usuario deletada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuario não encontrada")
    })
    public ResponseEntity<Object> destroy(@PathVariable Long id) {
        repository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("Usuario não encontrado"));

        repository.deleteById(id);

        return ResponseEntity.noContent().build();
    }

    @PutMapping("{id}")
    @CacheEvict(allEntries = true)
    @Operation(summary = "Atualizar um usuario", description = "Atualiza os dados de um usuario específica pelo seu ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuario não encontrada")
    })
    public Usuario update(@PathVariable Long id, @RequestBody Usuario usuario) {
        log.info("Atualizar usuario {} para {}", id, usuario);

        verificarSeUsuarioExiste(id);
        usuario.setId(id);
        return repository.save(usuario);
    }

    private void verificarSeUsuarioExiste(Long id) {
        repository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        NOT_FOUND,
                        "Não existe usuario com o id informado"));
    }
}
