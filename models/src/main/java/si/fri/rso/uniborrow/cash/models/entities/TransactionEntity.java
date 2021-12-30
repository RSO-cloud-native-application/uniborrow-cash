package si.fri.rso.uniborrow.cash.models.entities;

import lombok.Data;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "cash")
@NamedQueries(value =
        {
                @NamedQuery(name = "LoanEntity.getAll",
                        query = "SELECT im FROM CashEntity im")
        })
@Data
public class TransactionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private Integer cash;

    @Column
    private Instant timestamp;

    @Column(name = "from_id")
    private Integer fromId;

    @Column(name = "to_id")
    private Integer toId;
}

