package br.com.fiap.bluehope.model;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.hateoas.EntityModel;

import br.com.fiap.bluehope.controller.FotoController;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Entity
public class Foto {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    private String caminhoFoto;

    @ManyToOne
    private Desafio desafio;

    public EntityModel<Foto> toEntityModel() {
        return EntityModel.of(
                this,
                linkTo(methodOn(FotoController.class).show(id)).withSelfRel(),
                linkTo(methodOn(FotoController.class).destroy(id)).withRel("delete"),
                linkTo(methodOn(FotoController.class).index()).withRel("contents"));
    }

    public EntityModel<Foto> toModel() {
        return EntityModel.of(this,
                linkTo(methodOn(FotoController.class).index()).withRel("contents"));
    }
}
