package com.nttdata.bootcamp.ewalletservice.service.impl;


import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nttdata.bootcamp.ewalletservice.repository.EWalletRepository;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderRecord;


@Slf4j
@Service
public class KakfaService {

	@Autowired
    private EWalletRepository repository;
	
	private final ObjectMapper objectMapper;
    private final KafkaReceiver<String, String> kafkaReceiver;
    private final KafkaSender<String, String> kafkaSender;
    private final String topicUpdateBalanceWallet = "updateBalanceAccount";

    public KakfaService( KafkaReceiver<String, String> kafkaReceiver,
            ObjectMapper objectMapper,KafkaSender<String, String> kafkaSender) {
    	this.kafkaReceiver = kafkaReceiver;
    	this.objectMapper = objectMapper;
    	this.kafkaSender = kafkaSender;
    }
    
    @PostConstruct
    public void startConsumeTopic() {
    	 consumeTopics();
    }
    private void consumeTopics() {
        kafkaReceiver.receive()
            .doOnNext(record -> {
                String topic = record.topic();
                String value = record.value();

                if ("topicUpdateWallet2".equals(topic)) {
                	consumeTopicSaveWallet(value);
                } else if ("updateBalanceWallet".equals(topic)) {
                	updateBalanceWallet(value);
                } else {
                    log.warn("others topic: ", topic);
                }
            })
            .subscribe();
    }
	public void consumeTopicSaveWallet(String value){
	    log.info("ingreso al primer topic de topicUpdateWallet2");
	    System.out.println("ingreso al primer  topic de topicUpdateWallet2");
	    
        try {
        	JsonNode jsonNode = objectMapper.readTree(value);
            String numberCardDebit = jsonNode.get("numberCardDebit").asText();
            String phone = jsonNode.get("phone").asText();
            String primaryAccount = jsonNode.get("primaryAccount").asText();
            String accountBalance = jsonNode.get("accountBalance").asText();
            repository.findByPhone(phone)
                .flatMap(ewallet -> {
                	ewallet.setAccountBalance(Double.parseDouble(accountBalance));
                	ewallet.setNumberCardDebit(numberCardDebit);
                	ewallet.setPrimaryAccount(primaryAccount);
                   	return repository.save(ewallet).then();
                })
                .doOnError(error -> {
                    // Lógica para manejar errores "aun no se sabe como realizar esto"
                })
                .subscribe();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	
	public void updateBalanceWallet(String value){
	    log.info("ingreso al Segundo topic de updateBalanceWallet");
	    System.out.println("ingreso al Segundo topic de updateBalanceWallet");
		try {
			JsonNode jsonNode = objectMapper.readTree(value);
			 String bankAccountNumber = jsonNode.get("bankAccountNumber").asText();
		        String accountBalance = jsonNode.get("accountBalance").asText();
		        repository.findByPrimaryAccount(bankAccountNumber)
		            .flatMap(ewallet -> {
		            	ewallet.setAccountBalance(Double.parseDouble(accountBalance));
		               	return repository.save(ewallet).then();
		            })
		            .doOnError(error -> {
		                // Lógica para manejar errores "aun no se sabe como realizar esto"
		            }).subscribe();	
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}           
	}

	public void updateAccountBalanceCustomer(String bankAccountNumber,Double accountBalance) {
		String message = "{\"bankAccountNumber\": \"" + bankAccountNumber 
            	+ "\", \"accountBalance\": \"" + accountBalance + "\"}";
            
        kafkaSender.send(Mono.just(SenderRecord.create(
        	new ProducerRecord<>(topicUpdateBalanceWallet, message), null))).subscribe();
	}
}
