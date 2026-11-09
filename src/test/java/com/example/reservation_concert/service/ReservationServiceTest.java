package com.example.reservation_concert.service;

import com.example.reservation_concert.dto.ConcertResponse;
import com.example.reservation_concert.dto.CreateConcertDTO;
import com.example.reservation_concert.dto.ReservationResponse;
import com.example.reservation_concert.entity.Concert;
import com.example.reservation_concert.entity.ReservationStatut;
import com.example.reservation_concert.entity.User;
import com.example.reservation_concert.entity.UserRole;
import com.example.reservation_concert.exception.ConcertNotEnoughPlacesException;
import com.example.reservation_concert.exception.ConcertNotFoundException;
import com.example.reservation_concert.exception.InvalidReservationStateException;
import com.example.reservation_concert.repository.ConcertRepository;
import com.example.reservation_concert.repository.ReservationRepository;
import com.example.reservation_concert.repository.UserRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ReservationServiceTest {

    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
            "postgres:16"
    );

    @LocalServerPort
    private Integer port;

    @Autowired
    private ConcertRepository concertRepository;
    @Autowired
    private ConcertService concertService;
    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private ReservationService reservationService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserDetailsService userDetailsService;


    @BeforeAll
    static void beforeAll(){
        postgres.start();
    }

    @AfterAll
    static void afterAll(){
        postgres.stop();
    }

    ConcertResponse concertResponse;
    ReservationResponse reservationResponse;

    @BeforeEach
    void beforeEach(){
        reservationRepository.deleteAll();
        concertRepository.deleteAll();
        userRepository.deleteAll();

        CreateConcertDTO concertDTO = new CreateConcertDTO(
                "Concert Rock",
                LocalDateTime.parse("2026-12-20T20:00:00"),
                2
        );
        concertResponse = concertService.createConcert(concertDTO);

        User user = new User(
                "tom@gmail.com",
                "Tom",
                passwordEncoder.encode("1234"),
                UserRole.USER,
                30
        );
        userRepository.save(user);
        UserDetails userDetails = this.userDetailsService.loadUserByUsername(user.getEmail());

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(authToken);

        reservationResponse = reservationService.createReservation(concertResponse.getId());
    }

    @DynamicPropertySource
    static void configureProprties(DynamicPropertyRegistry registry){
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Test
    void shouldHaveLessOnePlace(){
        Concert concert1 = concertRepository.findById(concertResponse.getId())
                        .orElseThrow(()-> new ConcertNotFoundException(concertResponse.getId()));
        assertEquals(1,concert1.getPlacesDisponibles());
    }



    @Test
    void shouldHaveLessTwoPlaces(){

        reservationService.createReservation(
                concertResponse.getId()
        );

        Concert concert2 = concertRepository.findById(concertResponse.getId())
                .orElseThrow(()-> new ConcertNotFoundException(concertResponse.getId()));

        assertEquals(0,concert2.getPlacesDisponibles());
    }

    @Test
    void shouldHaveLessThreePlaces(){

        reservationService.createReservation(
                concertResponse.getId()
        );

        assertThrows(ConcertNotEnoughPlacesException.class, () -> {
                    reservationService.createReservation(
                            concertResponse.getId()
                    );
                }
        );
    }

    @Test
    void shouldConfimedPenddingReservation(){
        ReservationResponse currentReservation = reservationService.confirmReservation(reservationResponse.getId());
        assertEquals(ReservationStatut.CONFIRMED,currentReservation.getStatut());
    }

    @Test
    void shouldThrowExceptionAlreadyConfirmed(){
        ReservationResponse currentReservation = reservationService.confirmReservation(reservationResponse.getId());
        assertThrows(InvalidReservationStateException.class, () -> {
            reservationService.confirmReservation(currentReservation.getId());
        });
    }



    @Test
    void shouldCancelPenddingReservation(){
        ReservationResponse reservation2 = reservationService.createReservation(
                concertResponse.getId()
        );

        Concert concert2 = concertRepository.findById(concertResponse.getId())
                .orElseThrow(()-> new ConcertNotFoundException(concertResponse.getId()));

        ReservationResponse currentReservation = reservationService.cancelReservation(reservation2.getId());

        Concert currentConcert = concertRepository.findById(concertResponse.getId())
                .orElseThrow(()-> new ConcertNotFoundException(concertResponse.getId()));

        assertEquals(ReservationStatut.CANCELLED,currentReservation.getStatut());
        assertEquals(concert2.getPlacesDisponibles()+1,currentConcert.getPlacesDisponibles());
    }

    @Test
    void shouldThrowExceptionAlreadyCancled(){

        ReservationResponse reservation2 = reservationService.createReservation(
                concertResponse.getId()
        );

        ReservationResponse currentReservation = reservationService.cancelReservation(reservation2.getId());
        assertThrows(InvalidReservationStateException.class, () -> {
            reservationService.cancelReservation(currentReservation.getId());
        });
    }
}
