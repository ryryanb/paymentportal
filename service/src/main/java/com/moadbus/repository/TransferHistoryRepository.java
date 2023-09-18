package repository;

import java.util.Date;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import entity.TransferHistory;

public interface TransferHistoryRepository extends JpaRepository<TransferHistory, Long> {
	public Page<TransferHistory> findByCommentContainsAndBillerId(String comment, String billerId, Pageable page);

	public Page<TransferHistory> findByCommentContainsAndBillerIdAndToAccount(String string, String billerId, String toAccount,
			Pageable pageRequest);
	
	public Page<TransferHistory> findByCommentContainsAndBillerIdAndRefNum(String string, String billerId, String refNum,
			Pageable pageRequest);

	public Page<TransferHistory> findByCommentContainsAndBillerIdAndCreatedGreaterThanEqual(String string, String billerId, Date created,
			Pageable pageRequest);

	public Page<TransferHistory> findByCommentContainsAndBillerIdAndCreatedLessThanEqual(String string, String billerId, Date created,
			Pageable pageRequest);

	public Page<TransferHistory> findByCommentContainsAndBillerIdAndToAccountAndCreatedGreaterThanEqual(String string, String billerId,
			String toAccount, Date created, Pageable pageRequest);
	
	public Page<TransferHistory> findByCommentContainsAndBillerIdAndRefNumAndCreatedGreaterThanEqual(String string, String billerId,
			String toAccount, Date created, Pageable pageRequest);
	
	public Page<TransferHistory> findByCommentContainsAndBillerIdAndToAccountAndCreatedLessThanEqual(String string, String billerId,
			String toAccount, Date created, Pageable pageRequest);
	
	public Page<TransferHistory> findByCommentContainsAndBillerIdAndRefNumAndCreatedLessThanEqual(String string, String billerId,
			String toAccount, Date created, Pageable pageRequest);

	public Page<TransferHistory> findByCommentContainsAndBillerIdAndCreatedBetween(String string, String billerId, Date startDate, Date endDate,
			Pageable pageRequest);

	public Page<TransferHistory> findByCommentContainsAndBillerIdAndToAccountAndCreatedBetween(String string, String billerId, String toAccount,
			Date startDate, Date endDate, Pageable pageRequest);
	
	public Page<TransferHistory> findByCommentContainsAndBillerIdAndRefNumAndCreatedBetween(String string, String billerId, String toAccount,
			Date startDate, Date endDate, Pageable pageRequest);

}
