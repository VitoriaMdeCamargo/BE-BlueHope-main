package br.com.fiap.bluehope.model;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.hateoas.EntityModel;

import br.com.fiap.bluehope.controller.DesafioController;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
@Entity
public class Desafio {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank
    private String descricao;

    @Positive
    private int pontos;

    // TODO tipo desafio
    // private TipoDesafio tipo;

    @ManyToOne
    private Perfil perfil;

    public EntityModel<Desafio> toEntityModel() {
        return EntityModel.of(
                this,
                linkTo(methodOn(DesafioController.class).show(id)).withSelfRel(),
                linkTo(methodOn(DesafioController.class).destroy(id)).withRel("delete"),
                linkTo(methodOn(DesafioController.class).index()).withRel("contents"));
    }

    public EntityModel<Desafio> toModel() {
        return EntityModel.of(this,
                linkTo(methodOn(DesafioController.class).index()).withRel("contents"));
    }
}
