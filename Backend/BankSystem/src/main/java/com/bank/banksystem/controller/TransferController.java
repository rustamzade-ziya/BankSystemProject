package com.bank.banksystem.controller;

import com.bank.banksystem.dto.request.TransferRequest;
import com.bank.banksystem.dto.response.TransferResponse;
import com.bank.banksystem.service.TransferService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController // Spring-ə bu sinfin REST Controller olduğunu bildirir
@RequestMapping("/api/transfer") // Bütün metodlar üçün əsas URL yolu
public class TransferController {

    // 1. Service-in Controller-a daxil edilməsi (Dependency Injection)
    private final TransferService transferService;

    // Constructor Injection vasitəsilə Service-i alırıq
    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }

    // 2. Köçürmə əməliyyatı üçün POST Endpoint-in yaradılması
    @PostMapping
    public ResponseEntity<TransferResponse> transferMoney(@RequestBody TransferRequest request) {

        // Controller, Request-i Service-ə ötürür
        TransferResponse response = transferService.performTransfer(request);

        // 3. Nəticəyə uyğun HTTP Status Kodu qaytarırıq
        if (response.isSuccess()) {
            // Əgər Service uğurlu cavab qaytarıbsa: HTTP 200 OK
            return ResponseEntity.ok(response);
        } else {
            // Əgər Service uğursuz cavab (məsələn, balans çatışmazlığı) qaytarıbsa: HTTP 400 Bad Request
            return ResponseEntity.badRequest().body(response);
        }
    }
}