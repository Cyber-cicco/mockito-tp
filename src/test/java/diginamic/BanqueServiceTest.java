package diginamic;

import diginamic.entites.Compte;
import diginamic.exception.BanqueException;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
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
}