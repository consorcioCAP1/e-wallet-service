package com.nttdata.bootcamp.ewalletservice.dto;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class EWalletDto {

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
