package org.carRental.model;

import lombok.Data;
import lombok.Getter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Data
@Entity
//@Table(name = "pojazdy")
public class Pojazdy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Pojazdy musi posiadać markę")
    @Column(name = "marka")
    private String marka;

    @NotBlank(message = "Podaj model pojazdu")
    @Column(name = "model")
    private String model;

    @Column(name = "moc")
    private String moc;

    @NotBlank(message = "Podaj Numer Rejestracyjny")
    @Column(name = "srednie_spalanie")
    private String numerRejestracyjny;

    @NotBlank(message = "Podaj Cenę za dobę")
    @Column(name = "cena_za_dobe")
    private String cenaZaDobe;

    @Column(name = "miniaturka")
    private String miniaturka;



}
