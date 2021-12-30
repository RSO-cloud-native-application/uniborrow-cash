package si.fri.rso.uniborrow.cash.models.entities;

import lombok.Data;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "transactions")
@Data
public class TransactionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private Float cash;

    @Column
    private Instant timestamp;

    @Column(name = "from_id")
    private Integer fromId;

    @Column(name = "to_id")
    private Integer toId;
}

