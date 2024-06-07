package br.com.fiap.bluehope.model;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.hateoas.EntityModel;

import br.com.fiap.bluehope.controller.UsuarioController;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Entity
public class Usuario extends EntityModel<Usuario> {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String telefone;

    @NotBlank
    private String cpf;

    @NotBlank
    private String senha;

    public EntityModel<Usuario> toEntityModel() {
        return EntityModel.of(
                this,
                linkTo(methodOn(UsuarioController.class).show(id)).withSelfRel(),
                linkTo(methodOn(UsuarioController.class).destroy(id)).withRel("delete"),
                linkTo(methodOn(UsuarioController.class).index()).withRel("contents"));
    }

    public EntityModel<Usuario> toModel() {
        return EntityModel.of(this,
                linkTo(methodOn(UsuarioController.class).index()).withRel("contents"));
    }
}
