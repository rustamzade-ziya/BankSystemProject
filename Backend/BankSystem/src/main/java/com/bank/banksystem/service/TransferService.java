package com.bank.banksystem.service;

import com.bank.banksystem.dto.request.TransferRequest;
import com.bank.banksystem.dto.response.TransferResponse;

public interface TransferService {

    // Yeganə funksiyamız: köçürmə əməliyyatını həyata keçirmək
    TransferResponse performTransfer(TransferRequest request);
}