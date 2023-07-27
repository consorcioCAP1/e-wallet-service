package com.nttdata.bootcamp.ewalletservice.dto;

import lombok.Data;


@Data
public class EWalletDto {

	private String numberDocument;
	private String typeDocument;
	private String phone;
	private String imei;
	private String email;
	private Double accountBalance;
}
