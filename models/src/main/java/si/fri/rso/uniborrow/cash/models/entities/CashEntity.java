package si.fri.rso.uniborrow.cash.models.entities;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "cash")
@Data
public class CashEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "current_cash")
    private Float currentCash;

}

