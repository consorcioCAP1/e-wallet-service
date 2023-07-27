package com.nttdata.bootcamp.ewalletservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@EnableReactiveMongoRepositories
@SpringBootApplication
public class EWalletServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(EWalletServiceApplication.class, args);
	}

}
