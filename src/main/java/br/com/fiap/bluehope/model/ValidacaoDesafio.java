package br.com.fiap.bluehope.model;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.hateoas.EntityModel;

import br.com.fiap.bluehope.controller.ValidacaoDesafioController;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Entity
public class ValidacaoDesafio {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    private Boolean valido;

    @OneToOne
    private Desafio desafio;

    public EntityModel<ValidacaoDesafio> toEntityModel() {
        return EntityModel.of(
                this,
                linkTo(methodOn(ValidacaoDesafioController.class).show(id)).withSelfRel(),
                linkTo(methodOn(ValidacaoDesafioController.class).destroy(id)).withRel("delete"),
                linkTo(methodOn(ValidacaoDesafioController.class).index()).withRel("contents"));
    }

    public EntityModel<ValidacaoDesafio> toModel() {
        return EntityModel.of(this,
                linkTo(methodOn(ValidacaoDesafioController.class).index()).withRel("contents"));
    }
}
