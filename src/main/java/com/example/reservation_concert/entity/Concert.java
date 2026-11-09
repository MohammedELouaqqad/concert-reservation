package com.example.reservation_concert.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString(exclude = "reservationList")
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Concert {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;
    private LocalDateTime date;
    private int placesDisponibles;


    //OneToMany c LAZY par defaut
    @JsonIgnore
    @OneToMany(mappedBy = "concert")
    private List<Reservation> reservationList;
//    Au début, sans écrire `currentConcert.getReservationList()`,
//            `reservationList` c’est toujours un proxy (boîte fermée —
//            Hibernate préfère ne pas charger le contenu tant qu’on n’en a pas besoin)
//    parceque on est dans LAZY(par defaut dans OneToMany) pas EAGER.
//    Quand on écrit `currentConcert.getReservationList()` et qu’on accède à son contenu
//            (`.isEmpty()`, boucle...), si la session est ouverte, alors Hibernate fait
//    le fetch LAZY : il exécute le `SELECT` et la collection contient ce que la base renvoie —
//    les réservations s’il y en a, ou une collection vide s’il n’y en a aucune
//            (dans les deux cas le `SELECT` est exécuté).

    @Version
    private Long version;

    public Concert(String name, LocalDateTime date, int placesDisponibles) {
        this.name=name;
        this.date=date;
        this.placesDisponibles=placesDisponibles;
    }
}
