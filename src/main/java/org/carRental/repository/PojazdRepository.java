package org.carRental.repository;


import org.carRental.model.Pojazdy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PojazdRepository extends JpaRepository<Pojazdy, Long> {


}
