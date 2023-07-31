package com.nttdata.bootcamp.ewalletservice.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;

import static org.mockito.Mockito.*;

import com.nttdata.bootcamp.ewalletservice.documents.EWallet;
import com.nttdata.bootcamp.ewalletservice.service.EWalletService;
import com.nttdata.bootcamp.ewalletservice.service.impl.KakfaService;

@RestController
public class EWalletControllerTest {

    @InjectMocks
    private EWalletController eWalletController;

    @Mock
    private EWalletService eWalletService;

    @Mock
    private KakfaService kafkaService;

    private WebTestClient webTestClient;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        webTestClient = WebTestClient.bindToController(eWalletController).build();
    }

    /*
     * guardado exitoso de billetera
     * */
    @Test
    public void testSaveEWallet() {
        // Datos de objeto
    	EWallet eWallet = EWallet.builder()
        		.id("31312312312")
        		.accountBalance(100.0)
        		.primaryAccount("123456879")
        		.email("elejemplo@example.com")
        		.imei("ABC123XYZ")
        		.typeDocument("DNI")
        		.numberDocument("47810085")
        		.build();


        // Configuración del mock del servicio
        when(eWalletService.saveEWallet(any())).thenReturn(Mono.just(eWallet));

        // Llamada al método y verificación del resultado utilizando WebTestClient
        webTestClient.post().uri("/save")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(eWallet)
                .exchange()
                .expectStatus().isOk()
                .expectBody(EWallet.class)
                .isEqualTo(eWallet);

        // Verificar que el método del servicio fue llamado con el objeto correcto
        verify(eWalletService, times(1)).saveEWallet(eq(eWallet));
    }
 
    /*
     * recibir pago exitoso en billetera
     * */
    @Test
    public void testReceivePayment() {
        // Datos de objeto
        String phoneNumber = "123456789";
        Double amount = 50.0;
        EWallet eWallet = EWallet.builder()
        		.id("31312312312")
        		.accountBalance(100.0)
        		.primaryAccount("123456879")
        		.email("elejemplo@example.com")
        		.imei("ABC123XYZ")
        		.typeDocument("DNI")
        		.numberDocument("47810085")
        		.build();


        // Configuración del mock del servicio
        when(eWalletService.receivePayment(eq(phoneNumber), eq(amount))).thenReturn(Mono.just(eWallet));

        // Llamada al método y verificación del resultado utilizando WebTestClient
        webTestClient.post().uri("/receivePayment?phoneNumber={phoneNumber}&amount={amount}", phoneNumber, amount)
                .exchange()
                .expectStatus().isOk()
                .expectBody(EWallet.class)
                .isEqualTo(eWallet);

        // Verificar que el método del servicio fue llamado con los parámetros correctos
        verify(eWalletService, times(1)).receivePayment(eq(phoneNumber), eq(amount));
    }

    @Test
    public void testSendPayment() {
        // Datos de prueba
        String sendPhoneNumber = "123456789";
        String receivePhoneNumber = "987654321";
        Double amount = 50.0;
        
        EWallet receiveWallet = EWallet.builder()
        		.id("14454665")
        		.accountBalance(50.0)
        		.primaryAccount("123456810")
        		.email("elejemplo@example.com")
        		.imei("ABC123XYX")
        		.typeDocument("DNI")
        		.numberDocument("47810084")
        		.build();

        // Configuración del mock del servicio
        when(eWalletService.sendPayment(eq(sendPhoneNumber), eq(receivePhoneNumber), eq(amount)))
                .thenReturn(Mono.just(receiveWallet));

        // Llamada al método y verificación del resultado utilizando WebTestClient
        webTestClient.post().uri("/sendPayment?sendPhoneNumber={sendPhoneNumber}&receivePhoneNumber={receivePhoneNumber}&amount={amount}",
                        sendPhoneNumber, receivePhoneNumber, amount)
                .exchange()
                .expectStatus().isOk()
                .expectBody(EWallet.class)
                .isEqualTo(receiveWallet);

        // Verificar que el método del servicio fue llamado con los parámetros correctos
        verify(eWalletService, times(1)).sendPayment(eq(sendPhoneNumber), eq(receivePhoneNumber), eq(amount));
    }

    @Test
    public void testValidateNumberCard() {
        // Datos de prueba
        EWallet eWallet = EWallet.builder()
        		.id("31312312312")
        		.accountBalance(100.0)
        		.primaryAccount("123456879")
        		.email("elejemplo@example.com")
        		.imei("ABC123XYZ")
        		.typeDocument("DNI")
        		.numberDocument("47810085")
        		.build();
    
        // Llamada al método y verificación del resultado utilizando WebTestClient
        webTestClient.post().uri("/validateNumberCard")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(eWallet)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .isEqualTo("Mensaje enviado para validar el número de tarjeta.");

        // Verificar que el método de KakfaService fue llamado con el objeto correcto
        verify(kafkaService, times(1)).validateNumberCard(eq(eWallet));
    }
}
