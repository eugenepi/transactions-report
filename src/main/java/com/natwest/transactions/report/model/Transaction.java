package com.natwest.transactions.report.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Transaction {
  private String id;
  private String currency;
  private BigDecimal amount;
  private String iban;
  private LocalDateTime date;
  private String description;

  public Transaction(
      String id, String currency, BigDecimal amount, String iban, LocalDateTime date, String description) {
    this.id = id;
    this.currency = currency;
    this.amount = amount;
    this.iban = iban;
    this.date = date;
    this.description = description;
  }

  public Transaction() {}

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getCurrency() {
    return currency;
  }

  public void setCurrency(String currency) {
    this.currency = currency;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }

  public String getIban() {
    return iban;
  }

  public void setIban(String iban) {
    this.iban = iban;
  }

  public LocalDateTime getDate() {
    return date;
  }

  public void setDate(LocalDateTime date) {
    this.date = date;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }
}
