package com.nttdata.bootcamp.ewalletservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import com.nttdata.bootcamp.ewalletservice.documents.EWallet;
import com.nttdata.bootcamp.ewalletservice.repository.EWalletRepository;
import com.nttdata.bootcamp.ewalletservice.service.impl.EWalletServiceImpl;
import com.nttdata.bootcamp.ewalletservice.service.impl.KakfaService;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import static org.mockito.Mockito.*;

public class EWalletServiceImplTest {

	@InjectMocks
    private EWalletServiceImpl eWalletService;

    @Mock
    private EWalletRepository repository;

    @Mock
    private KakfaService kafkaService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /*
     * guardado exitoso de billetera
     * */
    @Test
    public void testSaveEWallet() {
        // Datos de Objeto
        EWallet eWallet = EWallet.builder()
        		.id("31312312312")
        		.accountBalance(100.0)
        		.build();

        // Configuración del mock del repositorio
        when(repository.save(any(EWallet.class))).thenReturn(Mono.just(eWallet));

        // Llamada al metodo y verificación del resultado
        Mono<EWallet> result = eWalletService.saveEWallet(eWallet);
        StepVerifier.create(result)
                .expectNext(eWallet)
                .verifyComplete();

        // Verificar que el método del repositorio fue llamado con el objeto correcto
        verify(repository, times(1)).save(eq(eWallet));
    }

    /*
     * recibir pago,billetera con cuenta Bancaria flujo exitoso
     * */
    @Test
    public void testReceivePaymentInAccount() {
        // Datos de objeto
        String phoneNumber = "98270241";
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

        // Configuración del mock del repositorio
        when(repository.findByPhone(phoneNumber)).thenReturn(Mono.just(eWallet));
        when(repository.save(any(EWallet.class))).thenReturn(Mono.just(eWallet));

        // Llamada al método y verificación del resultado
        Mono<EWallet> result = eWalletService.receivePayment(phoneNumber, amount);
        StepVerifier.create(result)
                .expectNext(eWallet)
                .verifyComplete();

        // Verificar que el método del repositorio fue llamado con el número de teléfono correcto
        verify(repository, times(1)).findByPhone(eq(phoneNumber));

        // Verificar que el método del repositorio fue llamado para guardar el nuevo saldo en cuenta
        verify(repository, times(1)).save(eq(eWallet));

        // Verificar que el método de KafkaService fue llamado con los parámetros correctos
        verify(kafkaService, times(1)).updateAccountBalanceCustomer(eq("123456879"), eq(150.0));
    }

    /*
     * recibir pago,billetera sin cuenta Bancaria flujo exitoso
     * */
    @Test
    public void testReceivePaymentWithOutAccount() {
        // Datos de objeto
        String phoneNumber = "98270241";
        Double amount = 50.0;
        EWallet eWallet = EWallet.builder()
        		.id("31312312312")
        		.accountBalance(100.0)
        		.email("elejemplo@example.com")
        		.imei("ABC123XYZ")
        		.typeDocument("DNI")
        		.numberDocument("47810085")
        		.build();

        // Configuración del mock del repositorio
        when(repository.findByPhone(phoneNumber)).thenReturn(Mono.just(eWallet));
        when(repository.save(any(EWallet.class))).thenReturn(Mono.just(eWallet));

        // Llamada al método y verificación del resultado
        Mono<EWallet> result = eWalletService.receivePayment(phoneNumber, amount);
        StepVerifier.create(result)
                .expectNext(eWallet)
                .verifyComplete();

        // Verificar que el método del repositorio fue llamado con el número de teléfono correcto
        verify(repository, times(1)).findByPhone(eq(phoneNumber));

        // Verificar que el método del repositorio fue llamado para guardar el nuevo saldo en cuenta
        verify(repository, times(1)).save(eq(eWallet));
        
        //verficia que kafkaService no fue utilizado
        verifyNoInteractions(kafkaService);
    }

    /*
     * enviar pago en billetera con cuentas bancarias, flujo exitoso
     * */
    @Test
    public void testSendPaymentWithAccounts() {
        // Datos de prueba
        String sendPhoneNumber = "123456789";
        String receivePhoneNumber = "987654321";
        Double amount = 50.0;
        EWallet sendWallet = EWallet.builder()
        		.id("31312312312")
        		.accountBalance(100.0)
        		.primaryAccount("123456879")
        		.email("elejemplo@example.com")
        		.imei("ABC123XYZ")
        		.typeDocument("DNI")
        		.numberDocument("47810085")
        		.build();
        EWallet receiveWallet = EWallet.builder()
        		.id("14454665")
        		.accountBalance(50.0)
        		.primaryAccount("123456810")
        		.email("elejemplo@example.com")
        		.imei("ABC123XYX")
        		.typeDocument("DNI")
        		.numberDocument("47810084")
        		.build();

        // Configuración del mock del repositorio
        when(repository.findByPhone(sendPhoneNumber)).thenReturn(Mono.just(sendWallet));
        when(repository.findByPhone(receivePhoneNumber)).thenReturn(Mono.just(receiveWallet));
        when(repository.save(any(EWallet.class))).thenReturn(Mono.just(sendWallet)).thenReturn(Mono.just(receiveWallet));

        // Llamada al método y verificación del resultado
        Mono<EWallet> result = eWalletService.sendPayment(sendPhoneNumber, receivePhoneNumber, amount);
        StepVerifier.create(result)
                .expectNext(receiveWallet)
                .verifyComplete();

        // Verificar que el método del repositorio findByPhone fue llamado 2 veces
        verify(repository, times(2)).findByPhone(any());

        // Verificar que el método del repositorio fue llamado para guardar los cambios en ambos monederos
        verify(repository, times(2)).save(any(EWallet.class));

        // Verificar que el método de KafkaService fue llamado con los parámetros correctos
        verify(kafkaService, times(1)).updateAccountBalanceCustomer(eq("123456879"), eq(50.0));
        verify(kafkaService, times(1)).updateAccountBalanceCustomer(eq(receiveWallet.getPrimaryAccount()), eq(100.0));
    }

    /*
     * enviar pago en billetera sin cuentas bancarias, flujo exitoso
     * */
    @Test
    public void testSendPaymentWithOutAccounts() {
        // Datos de prueba
        String sendPhoneNumber = "123456789";
        String receivePhoneNumber = "987654321";
        Double amount = 50.0;
        EWallet sendWallet = EWallet.builder()
        		.id("31312312312")
        		.accountBalance(100.0)
        		.email("elejemplo@example.com")
        		.imei("ABC123XYZ")
        		.typeDocument("DNI")
        		.numberDocument("47810085")
        		.build();
        EWallet receiveWallet = EWallet.builder()
        		.id("14454665")
        		.accountBalance(50.0)
        		.email("elejemplo@example.com")
        		.imei("ABC123XYX")
        		.typeDocument("DNI")
        		.numberDocument("47810084")
        		.build();

        // Configuración del mock del repositorio
        when(repository.findByPhone(sendPhoneNumber)).thenReturn(Mono.just(sendWallet));
        when(repository.findByPhone(receivePhoneNumber)).thenReturn(Mono.just(receiveWallet));
        when(repository.save(any(EWallet.class))).thenReturn(Mono.just(sendWallet)).thenReturn(Mono.just(receiveWallet));

        // Llamada al método y verificación del resultado
        Mono<EWallet> result = eWalletService.sendPayment(sendPhoneNumber, receivePhoneNumber, amount);
        StepVerifier.create(result)
                .expectNext(receiveWallet)
                .verifyComplete();

        // Verificar que el método del repositorio findByPhone fue llamado 2 veces
        verify(repository, times(2)).findByPhone(any());

        // Verificar que el método del repositorio fue llamado para guardar los cambios en ambos monederos
        verify(repository, times(2)).save(any(EWallet.class));

        //verifica que kafkaService no fue utilizado
        verifyNoInteractions(kafkaService);
    }

    /*
     * enviar pago en billetera con cuentas bancarias, saldo insuficiente en cuenta bancaria
     * */
    @Test
    public void testSendPaymentInsufficientBalance() {
        // Datos de prueba
        String sendPhoneNumber = "123456789";
        String receivePhoneNumber = "987654321";
        Double amount = 150.0;

        EWallet sendWallet = EWallet.builder()
        		.id("31312312312")
        		.accountBalance(100.0)
        		.primaryAccount("123456879")
        		.email("elejemplo@example.com")
        		.imei("ABC123XYZ")
        		.typeDocument("DNI")
        		.numberDocument("47810085")
        		.build();
        // Configuración del mock del repositorio
        when(repository.findByPhone(sendPhoneNumber)).thenReturn(Mono.just(sendWallet));

        // Llamada al método y verificación del resultado
        Mono<EWallet> result = eWalletService.sendPayment(sendPhoneNumber, receivePhoneNumber, amount);
        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();

        // Verificar que el método del repositorio fue llamado con el número de teléfono correcto
        verify(repository, times(1)).findByPhone(eq(sendPhoneNumber));

        // Verificar que el método del repositorio no fue llamado para guardar los cambios en ningún monedero
        verify(repository, never()).save(any(EWallet.class));

        // Verificar que el método de KafkaService no fue llamado
        verifyNoInteractions(kafkaService);
    }
}


