package com.nttdata.bootcamp.ewalletservice.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.nttdata.bootcamp.ewalletservice.documents.EWallet;
import com.nttdata.bootcamp.ewalletservice.repository.EWalletRepository;
import com.nttdata.bootcamp.ewalletservice.service.EWalletService;
import reactor.core.publisher.Mono;

@Service
public class EWalletServiceImpl implements EWalletService{
	
	@Autowired
    private EWalletRepository repository;

	@Autowired
    private KakfaService kakfaService;
    @Override
    public Mono<EWallet> saveEWallet(EWallet eWallet) {
        return repository.save(eWallet);
    }

	@Override
    public Mono<EWallet> receivePayment(String phoneNumber, Double amount) {
        //validamos que el numero exista y si existe registramos el nuevo saldo en cuenta
		return findByPhoneNumber(phoneNumber)
            .flatMap(wallet -> {
                Double newBalance = wallet.getAccountBalance() + amount;
                wallet.setAccountBalance(newBalance);
                if(!wallet.getPrimaryAccount().isEmpty()) {
                	kakfaService.updateAccountBalanceCustomer(wallet.getPrimaryAccount(), newBalance);
                }
                return repository.save(wallet);
            });
    }

	@Override
    public Mono<EWallet> sendPayment(String sendPhoneNumber, String receivePhoneNumber, Double amount) {
        return findByPhoneNumber(sendPhoneNumber)
            .flatMap(sendWallet -> {
                Double sendBalance = sendWallet.getAccountBalance();
                //validamos que el monto en cuenta sea suficiente para ser enviado
                if (sendBalance >= amount) {
                	sendWallet.setAccountBalance(sendBalance - amount);
                	if(!sendWallet.getPrimaryAccount().isEmpty()) {
                    	kakfaService.updateAccountBalanceCustomer(sendWallet.getPrimaryAccount(),
                    			sendWallet.getAccountBalance());
                    }
                    return repository.save(sendWallet)
                        .then(findByPhoneNumber(receivePhoneNumber))
                        .flatMap(receiveWallet -> {
                        	Double newBalance = receiveWallet.getAccountBalance() + amount;
                            receiveWallet.setAccountBalance(newBalance);
                            if(!receiveWallet.getPrimaryAccount().isEmpty()) {
                            	kakfaService.updateAccountBalanceCustomer(receiveWallet.getPrimaryAccount(),
                            			newBalance);
                            }
                            return repository.save(receiveWallet);
                        });
                } else {
                    return Mono.error(new RuntimeException("Saldo insuficiente para enviar el pago"));
                }
            });
    }
	
	//validacion para busqueda de numero como monedero
	public Mono<EWallet> findByPhoneNumber(String phoneNumber) {
        return repository.findByPhone(phoneNumber)
              .switchIfEmpty(Mono.error(new RuntimeException("Número: "+ phoneNumber +" no existe como monedero")));
    }

}
