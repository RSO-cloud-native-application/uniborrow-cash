package si.fri.rso.uniborrow.cash.services.beans;

import org.eclipse.microprofile.metrics.annotation.Counted;
import si.fri.rso.uniborrow.cash.models.entities.CashEntity;
import si.fri.rso.uniborrow.cash.models.entities.TransactionEntity;
import si.fri.rso.uniborrow.cash.services.users.UsersService;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.ws.rs.NotAllowedException;
import javax.ws.rs.NotFoundException;
import java.time.Instant;
import java.util.List;
import java.util.logging.Logger;


@RequestScoped
public class CashDataProviderBean {

    private Logger log = Logger.getLogger(CashDataProviderBean.class.getName());

    @Inject
    private UsersService usersService;


    @Inject
    private EntityManager em;


    @Counted
    public CashEntity addCash(Integer userId, Integer amount) {
        if (!usersService.checkUserExists(userId)) {
            throw new NotAllowedException("User doesn't exist.");
        }
        CashEntity cashData = getEntityByUserId(userId);
        if (cashData == null) {
            createNewWithAmount(userId, amount);
            createTransaction(userId, userId, amount);
        } else {
            try {
                beginTx();
                cashData.setCurrentCash(cashData.getCurrentCash() + amount);
                commitTx();
                createTransaction(userId, userId, amount);
            } catch (Exception e) {
                rollbackTx();
            }
        }
        return cashData;
    }

    public List<TransactionEntity> getCashTransactionsByUserId(Integer userId) {
        return getTransactionsByUserId(userId);
    }

    public CashEntity getCashByUserId(Integer userId) {
        CashEntity cashData = getEntityByUserId(userId);
        if (cashData == null) {
            throw new NotFoundException();
        }
        return cashData;
    }


    public TransactionEntity sendCash(Integer fromUserId, Integer toUserId, Integer amount) {
        if (!usersService.checkUserExists(fromUserId) || !usersService.checkUserExists(toUserId)) {
            throw new NotAllowedException("User doesn't exist.");
        }
        CashEntity cashDataFrom = getEntityByUserId(fromUserId);
        if (cashDataFrom == null || cashDataFrom.getCurrentCash() < amount) {
            throw new RuntimeException("Not enough money!");
        }
        CashEntity cashDataTo = getEntityByUserId(toUserId);
        if (cashDataTo == null) {
            createNewWithAmount(toUserId, amount);
            try {
                beginTx();
                cashDataFrom.setCurrentCash(cashDataFrom.getCurrentCash() - amount);
                commitTx();
            } catch (Exception e) {
                rollbackTx();
            }
        } else {
            try {
                beginTx();
                cashDataTo.setCurrentCash(cashDataTo.getCurrentCash() + amount);
                cashDataFrom.setCurrentCash(cashDataFrom.getCurrentCash() - amount);
                commitTx();
            } catch (Exception e) {
                rollbackTx();
            }
        }
        return createTransaction(fromUserId, toUserId, amount);
    }

    private CashEntity getEntityByUserId(Integer userId) {
        return em.createQuery("SELECT t FROM CashEntity t where t.userId = :value1", CashEntity.class)
                .setParameter("value1", userId).getSingleResult();
    }

    private List<TransactionEntity> getTransactionsByUserId(Integer userId) {
        return em.createQuery("SELECT t FROM TransactionEntity t where t.fromId = :value1 or t.toId = :value1", TransactionEntity.class)
                .setParameter("value1", userId)
                .getResultList();
    }

    private CashEntity createNewWithAmount(Integer userId, Integer amount) {
        if (!usersService.checkUserExists(userId)) {
            throw new IllegalArgumentException("User doesn't exist.");
        }
        CashEntity cashData = new CashEntity();
        cashData.setUserId(userId);
        cashData.setCurrentCash(amount);
        try {
            beginTx();
            em.persist(cashData);
            commitTx();
        } catch (Exception e) {
            rollbackTx();
        }
        if (cashData.getId() == null) {
            throw new RuntimeException("Entity was not persisted");
        }
        return cashData;
    }

    private TransactionEntity createTransaction(Integer userFrom, Integer userTo, Integer amount) {
        if (usersService.checkUserExists(userFrom) && usersService.checkUserExists(userTo)) {
            TransactionEntity transactionEntity = new TransactionEntity();
            transactionEntity.setCash(amount);
            transactionEntity.setToId(userTo);
            transactionEntity.setFromId(userFrom);
            transactionEntity.setTimestamp(Instant.now());
            try {
                beginTx();
                em.persist(transactionEntity);
                commitTx();
            } catch (Exception e) {
                rollbackTx();
            }
            return transactionEntity;
        }
        throw new NotAllowedException("User doesn't exist.");
    }


    private void beginTx() {
        if (!em.getTransaction().isActive()) {
            em.getTransaction().begin();
        }
    }

    private void commitTx() {
        if (em.getTransaction().isActive()) {
            em.getTransaction().commit();
        }
    }

    private void rollbackTx() {
        if (em.getTransaction().isActive()) {
            em.getTransaction().rollback();
        }
    }
}