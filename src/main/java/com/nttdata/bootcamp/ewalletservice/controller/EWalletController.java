package com.nttdata.bootcamp.ewalletservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.nttdata.bootcamp.ewalletservice.documents.EWallet;
import com.nttdata.bootcamp.ewalletservice.service.EWalletService;
import com.nttdata.bootcamp.ewalletservice.service.impl.KakfaService;
import reactor.core.publisher.Mono;


@RestController
@RequestMapping("/")
public class EWalletController {

	@Autowired
    private EWalletService eWalletService;

	@Autowired
    private KakfaService kakfaService;

	@PostMapping("/save")
    public Mono<EWallet> saveEWallet(@RequestBody EWallet eWallet) {
        return eWalletService.saveEWallet(eWallet);
    }

	// para recibir pagos
    @PostMapping("/receivePayment")
    public Mono<EWallet> receivePayment(@RequestParam String phoneNumber, @RequestParam Double amount) {
        return eWalletService.receivePayment(phoneNumber, amount);
    }

    // para enviar pagos
    @PostMapping("/sendPayment")
    public Mono<EWallet> sendPayment(@RequestParam String sendPhoneNumber, 
    				@RequestParam String receivePhoneNumber, @RequestParam Double amount) {
        return eWalletService.sendPayment(sendPhoneNumber, receivePhoneNumber, amount);
    }

    @PostMapping("/validateNumberCard")
    public Mono<ResponseEntity<String>> validateNumberCard(@RequestBody EWallet eWallet) {
    	kakfaService.validateNumberCard(eWallet);
        return Mono.just(ResponseEntity.ok("Mensaje enviado para validar el número de tarjeta."));
    }
}
