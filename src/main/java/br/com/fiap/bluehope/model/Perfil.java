package br.com.fiap.bluehope.model;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.hateoas.EntityModel;

import br.com.fiap.bluehope.controller.PerfilController;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
@Entity
public class Perfil {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank
    private String nome;

    @PositiveOrZero
    private int somaPontos;

    private String medalha;

    @OneToOne
    private Usuario usuario;

    public EntityModel<Perfil> toEntityModel() {
        return EntityModel.of(
                this,
                linkTo(methodOn(PerfilController.class).show(id)).withSelfRel(),
                linkTo(methodOn(PerfilController.class).destroy(id)).withRel("delete"),
                linkTo(methodOn(PerfilController.class).index()).withRel("contents"));
    }

    public EntityModel<Perfil> toModel() {
        return EntityModel.of(this,
                linkTo(methodOn(PerfilController.class).index()).withRel("contents"));
    }
}
