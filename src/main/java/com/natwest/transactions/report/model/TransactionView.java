package com.natwest.transactions.report.model;

import java.math.BigDecimal;
import java.util.List;

public class TransactionView {

  private String iban;
  private String currency;
  private String year;
  private String month;
  private Integer page;
  private Integer totalPages;
  private BigDecimal credit;
  private BigDecimal debit;
  private List<Transaction> transactions;

  public TransactionView(
      String iban,
      String currency,
      String year,
      String month,
      Integer page,
      Integer totalPages,
      BigDecimal credit,
      BigDecimal debit,
      List<Transaction> transactions) {
    this.iban = iban;
    this.currency = currency;
    this.year = year;
    this.month = month;
    this.page = page;
    this.totalPages = totalPages;
    this.credit = credit;
    this.debit = debit;
    this.transactions = transactions;
  }

  public TransactionView() {
  }

  public String getIban() {
    return iban;
  }

  public void setIban(String iban) {
    this.iban = iban;
  }

  public String getCurrency() {
    return currency;
  }

  public void setCurrency(String currency) {
    this.currency = currency;
  }

  public String getYear() {
    return year;
  }

  public void setYear(String year) {
    this.year = year;
  }

  public String getMonth() {
    return month;
  }

  public void setMonth(String month) {
    this.month = month;
  }

  public Integer getPage() {
    return page;
  }

  public void setPage(Integer page) {
    this.page = page;
  }

  public Integer getTotalPages() {
    return totalPages;
  }

  public void setTotalPages(Integer totalPages) {
    this.totalPages = totalPages;
  }

  public BigDecimal getCredit() {
    return credit;
  }

  public void setCredit(BigDecimal credit) {
    this.credit = credit;
  }

  public BigDecimal getDebit() {
    return debit;
  }

  public void setDebit(BigDecimal debit) {
    this.debit = debit;
  }

  public List<Transaction> getTransactions() {
    return transactions;
  }

  public void setTransactions(List<Transaction> transactions) {
    this.transactions = transactions;
  }
}
