package com.nttdata.bootcamp.ewalletservice.documents;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "eWallet")
public class EWallet {

	@Id
	private String id;
	private String numberDocument;
	private String typeDocument;
	private String phone;
	private String imei;
	private String email;
	private Double accountBalance;
	private String numberCardDebit;
	private String primaryAccount;

}
