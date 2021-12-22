package com.java.deposit.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.java.deposit.dto.DepositRespDto;
import com.java.deposit.entity.Deposit;
import com.java.deposit.exception.DepositServiceException;
import com.java.deposit.repository.DepositRepo;
import com.java.deposit.rest.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Service
public class DepositService {
    public static final String TOPIC_EXCHANGE_DEPOSIT = "js.deposit.notify.exchange";
    public static final String ROUTING_KEY_DEPOSIT = "js.key.deposit";

    private final DepositRepo depositRepo;
    private final AccountServiceClient accountServiceClient;
    private final BillServiceClient billServiceClient;
    private final RabbitTemplate rabbitTemplate;


    public DepositService(DepositRepo depositRepo, AccountServiceClient accountServiceClient,
                          BillServiceClient billServiceClient, RabbitTemplate rabbitTemplate) {
        this.depositRepo = depositRepo;
        this.accountServiceClient = accountServiceClient;
        this.billServiceClient = billServiceClient;
        this.rabbitTemplate = rabbitTemplate;
    }

    public DepositRespDto deposit(Long accountId, Long billId, BigDecimal amount) {

        if (accountId == null && billId == null) {
            throw new DepositServiceException("Нет такого счета или аккаунта");
        }
        if (billId != null) {
            BillResponseDto billResponseDto = billServiceClient.getBillById(billId);
            BillRequestDto billRequestDto = createBillReq(amount, billResponseDto);
            billServiceClient.update(billId, billRequestDto);
            AccountResponseDto accountResponseDto = accountServiceClient.getAccountById(billResponseDto.getAccountId());
            depositRepo.save(new Deposit(amount, billId, accountResponseDto.getEmail(), OffsetDateTime.now()));
            return createResponse(amount, accountResponseDto);
        }
        BillResponseDto defaultBill = getDefaultBill(accountId);
        BillRequestDto billRequestDto = createBillReq(amount, defaultBill);
        billServiceClient.update(defaultBill.getBillId(), billRequestDto);
        AccountResponseDto account = accountServiceClient.getAccountById(accountId);
        depositRepo.save(new Deposit(amount, defaultBill.getBillId(), account.getEmail(), OffsetDateTime.now()));
        return createResponse(amount, account);
    }

    private DepositRespDto createResponse(BigDecimal amount, AccountResponseDto accountResponseDto) {
        DepositRespDto depositRespDto = new DepositRespDto(amount, accountResponseDto.getEmail());

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            rabbitTemplate
                    .convertAndSend(TOPIC_EXCHANGE_DEPOSIT, ROUTING_KEY_DEPOSIT
                            , objectMapper.writeValueAsString(depositRespDto));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new DepositServiceException("Сообщение не отправлено на сервер");
        }
        return depositRespDto;
    }

    private BillRequestDto createBillReq(BigDecimal amount, BillResponseDto billResponseDto) {
        BillRequestDto billRequestDto = new BillRequestDto();
        billRequestDto.setAccountId(billResponseDto.getAccountId());
        billRequestDto.setCreateDate(billResponseDto.getCreateDate());
        billRequestDto.setIsDefault(billResponseDto.getIsDefault());
        billRequestDto.setOverdraftEnabled(billResponseDto.getOverdraftEnabled());
        billRequestDto.setAmount(billResponseDto.getAmount().add(amount));
        return billRequestDto;
    }

    private BillResponseDto getDefaultBill(Long accountId) {
        return billServiceClient.getBillsByAccountId(accountId).stream()
                .filter(BillResponseDto::getIsDefault)
                .findAny()
                .orElseThrow(() -> new DepositServiceException("Unable to find bill " + accountId));
    }
}
