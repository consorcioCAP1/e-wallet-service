package com.nttdata.bootcamp.ewalletservice.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.nttdata.bootcamp.ewalletservice.documents.EWallet;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Repository
public interface EWalletRepository extends ReactiveMongoRepository<EWallet, String>{

	Mono<EWallet> findByPhone(String phoneNumber);
	Flux<EWallet> findByPrimaryAccount(String primaryAccount);
}
