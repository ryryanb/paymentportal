package repository;

import entity.Biller;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BillerRepository extends JpaRepository<Biller, Long> {
	
	Biller findByBillerId(Long id);

	Biller findBillerByBillerId(Long id);

	List<Biller> findAll();

}
