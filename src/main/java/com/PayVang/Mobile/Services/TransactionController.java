package com.PayVang.Mobile.Services;

import com.PayVang.Mobile.Constants.ErrorConstants;
import com.PayVang.Mobile.CustomExceptions.InvalidRequestException;
import com.PayVang.Mobile.Models.TransactionRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    @PostMapping("/initiatetransaction")
    public String initiateTransaction(@RequestBody TransactionRequest transactionRequest) {
        // TODO: TBD
        if (transactionRequest.amount == 0)
        {
            throw new InvalidRequestException(ErrorConstants.invalidRequestBody);
        }
        return "https://dashboard.payvang.com/v1/jsp/paymentInIt";
    }
}
