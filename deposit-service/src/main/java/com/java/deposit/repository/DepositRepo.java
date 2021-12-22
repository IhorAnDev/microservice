package com.java.deposit.repository;

import com.java.deposit.entity.Deposit;
import org.springframework.data.repository.CrudRepository;

public interface DepositRepo extends CrudRepository<Deposit,Long> {
}
