package repository;

import org.springframework.data.jpa.repository.JpaRepository;

import entity.Audit;

public interface AuditRepository extends JpaRepository<Audit, Long> {
	
}
