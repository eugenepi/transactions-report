package com.natwest.transactions.report.repositories;

import com.natwest.transactions.report.entities.PagesView;
import com.natwest.transactions.report.entities.TransactionEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends CrudRepository<TransactionEntity, Long> {
  @Query(
      value =
          "SELECT DATE_FORMAT(`date`, '%Y-%m') as `date`,COUNT(*) AS count FROM `transactions` WHERE iban = ? GROUP BY MONTH(`date`) ORDER BY `count` DESC;",
      nativeQuery = true)
  List<PagesView> getPagesView(String iban);

  @Query(value = "SELECT * FROM transactions WHERE iban =? AND YEAR(date) = ? AND MONTH(DATE) = ?", nativeQuery = true)
  List<TransactionEntity> getTransactionsByDateAndIban(String iban, String year, String month);
}
