package diginamic;

import diginamic.entites.Compte;
import diginamic.exception.BanqueException;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class BanqueServiceTest {

    @InjectMocks
    private BanqueService service = new BanqueService();
    @Test
    void creerCompte() {
        var transProcessor = Mockito.mock(TransactionProcessor.class);
        var transacDao = Mockito.mock(TransactionDao.class);
        var compteDao = Mockito.mock(CompteDao.class);
        var numeroCompte = "1234";
        var numeroCompte2 = "12345";
        var email = "abelcoli@oultook.fr";
        Mockito.when(compteDao.findByNumero(numeroCompte)).thenReturn(new Compte());
        Mockito.when(compteDao.findByNumero(numeroCompte2)).thenReturn(null);
        service.setCompteDao(compteDao);
        service.setTransactionProcessor(transProcessor);
        service.setTransactionDao(transacDao);
        assertThrows(BanqueException.class, () -> service.creerCompte(numeroCompte, 1900.12, email));
        try {
            var newCompte = service.creerCompte(numeroCompte2, 1900.23, email);
            assertEquals(newCompte.getSolde(), 1900.23);
            assertEquals(newCompte.getEmail(), email);
            assertEquals(newCompte.getNumero(), numeroCompte2);
        } catch (BanqueException e) {
            System.out.println("shouldn't be here");
        }
    }

    @Test
    void deposer() {
        var compte = new Compte("1312", "abelcoli@outlook.fr", 200d);
        var processor = Mockito.spy(TransactionProcessor.class);
        var compteDao = Mockito.mock(CompteDao.class);
        service.setTransactionProcessor(processor);
        service.setCompteDao(compteDao);
        Mockito.doNothing().when(processor).envoyerMailConfirmation(Mockito.any(Compte.class), Mockito.anyString());
        service.deposer(compte, 200d);
        assertEquals(compte.getSolde(), 400d);


    }

    @Test
    void retirer() {
        var compte = new Compte("1312", "abelcoli@outlook.fr", 200d);
        var processor = Mockito.spy(TransactionProcessor.class);
        var compteDao = Mockito.mock(CompteDao.class);
        var transacDao = Mockito.mock(TransactionDao.class);
        Mockito.doNothing().when(processor).envoyerMailConfirmation(Mockito.any(Compte.class), Mockito.anyString());
        service.setTransactionProcessor(processor);
        service.setCompteDao(compteDao);
        service.setTransactionDao(transacDao);
        service.retirer(compte, 100d);
        Mockito.doNothing().when(processor).envoyerMailConfirmation(Mockito.any(Compte.class), Mockito.anyString());
        assertEquals(compte.getSolde(), 100d);
        service.retirer(compte, 300d);
        assertNotEquals(processor.getErrors().size(), 0);
        assertEquals(compte.getSolde(), 100d);
    }

    @Test
    void virer() {
        var compte1 = new Compte("1312", "abelcoli@outlook.fr", 200d);
        var compte2 = new Compte("1315", "abelcoli@gmail.fr", 200d);
        var processor = Mockito.spy(TransactionProcessor.class);
        var compteDao = Mockito.mock(CompteDao.class);
        var transacDao = Mockito.mock(TransactionDao.class);
        Mockito.doNothing().when(processor).envoyerMailConfirmation(Mockito.any(Compte.class), Mockito.anyString());
        service.setTransactionProcessor(processor);
        service.setCompteDao(compteDao);
        service.setTransactionDao(transacDao);
        service.virer(compte1, compte2, 50d);
        assertEquals(compte1.getSolde(), 149.5d);
        assertEquals(compte2.getSolde(), 250d);
    }
}