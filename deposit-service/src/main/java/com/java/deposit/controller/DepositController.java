package com.java.deposit.controller;

import com.java.deposit.dto.DepositReqDto;
import com.java.deposit.dto.DepositRespDto;
import com.java.deposit.service.DepositService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DepositController {

    private final DepositService depositService;

    @Autowired
    public DepositController(DepositService depositService) {
        this.depositService = depositService;
    }

    @PostMapping("/deposits")
    public DepositRespDto deposit(@RequestBody DepositReqDto depositReqDto) {
        return depositService.deposit(depositReqDto.getAccountId(),
                depositReqDto.getBillId(), depositReqDto.getAmount());

    }

}
