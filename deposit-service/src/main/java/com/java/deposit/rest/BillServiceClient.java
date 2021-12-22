package com.java.deposit.rest;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@FeignClient(name = "bill-service")
public interface BillServiceClient {
    @RequestMapping(value = "bills/{billId}", method = RequestMethod.GET)
    BillResponseDto getBillById(@PathVariable("billId") Long billId);

    @RequestMapping(value = "bills/{billId}", method = RequestMethod.PUT)
    void update(@PathVariable("billId") Long billId, BillRequestDto billRequestDto);

    @RequestMapping(value = "bills/account/{accountId}",method = RequestMethod.GET)
    List<BillResponseDto> getBillsByAccountId(@PathVariable("accountId") Long accountId);
}
