package si.fri.rso.uniborrow.cash.models.entities;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "cash")
@NamedQueries(value =
        {
                @NamedQuery(name = "LoanEntity.getAll",
                        query = "SELECT im FROM CashEntity im")
        })
@Data
public class CashEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "current_cash")
    private Integer currentCash;

}

