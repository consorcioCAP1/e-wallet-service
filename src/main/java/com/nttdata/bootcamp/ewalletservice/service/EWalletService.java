package com.nttdata.bootcamp.ewalletservice.service;

import com.nttdata.bootcamp.ewalletservice.documents.EWallet;

import reactor.core.publisher.Mono;

public interface EWalletService {
	public Mono<EWallet> saveEWallet(EWallet eWallet); 
	public Mono<EWallet> receivePayment(String phoneNumber, Double amount);
	public Mono<EWallet> sendPayment(String sendPhoneNumber, String receivePhoneNumber, Double amount);
}

