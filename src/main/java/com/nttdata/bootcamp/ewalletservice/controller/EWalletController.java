package com.nttdata.bootcamp.ewalletservice.controller;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nttdata.bootcamp.ewalletservice.documents.EWallet;
import com.nttdata.bootcamp.ewalletservice.service.EWalletService;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderRecord;


@RestController
@RequestMapping("/")
public class EWalletController {

	@Autowired
    private EWalletService eWalletService;

	private final KafkaSender<String, String> kafkaSender;
    private final String topic = "topicprueba";
    
    public EWalletController(KafkaSender<String, String> kafkaSender) {
        this.kafkaSender = kafkaSender;
    }

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
        // Envía un mensaje al topic con el número de tarjeta para validar
        String message = "{\"numberCardDebit\": \"" + eWallet.getNumberCardDebit() 
        					+ "\", \"phone\": \"" + eWallet.getPhone() + "\"}";

        kafkaSender.send(Mono.just(SenderRecord.create(new ProducerRecord<>(topic, message), null)))
        		.subscribe();       
        return Mono.just(ResponseEntity.ok("Mensaje enviado para validar el número de tarjeta."));
    }
}
