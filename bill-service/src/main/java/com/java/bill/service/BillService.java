package com.java.bill.service;

import com.java.bill.entity.Bill;
import com.java.bill.exceptions.BillNotFoundException;
import com.java.bill.repository.BillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BillService {

    private final BillRepository billRepository;

    public Bill getBillById(Long billId) {
        return billRepository.findById(billId).orElseThrow(() -> new BillNotFoundException("Not boll with id " + billId));
    }

    public Long createBill(Long accountId, BigDecimal amount, Boolean isDefault, Boolean overdraftEnabled) {

        Bill bill = new Bill(accountId, amount, isDefault, OffsetDateTime.now(), overdraftEnabled);
        return billRepository.save(bill).getBillId();
    }

    public Bill updateBill(Long billId, Long accountId, BigDecimal amount, Boolean isDefault, Boolean overdraftEnabled) {
        Bill bill = new Bill();
        bill.setBillId(billId);
        bill.setAccountId(accountId);
        bill.setAmount(amount);
        bill.setIsDefault(isDefault);
        bill.setOverdraftEnabled(overdraftEnabled);
        return billRepository.save(bill);
    }

    public Bill deleteBill(Long billId) {
        Bill deleteBill = getBillById(billId);
        billRepository.deleteById(billId);
        return deleteBill;
    }

    public List<Bill> getBillsByAccountId(Long accountId) {
        return billRepository.getBillsByAccountId(accountId);
    }
}
