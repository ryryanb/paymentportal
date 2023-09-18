package repository;

import org.springframework.data.jpa.repository.JpaRepository;

import entity.Currency;

public interface CurrencyRepository extends JpaRepository<Currency, Long> {

}
