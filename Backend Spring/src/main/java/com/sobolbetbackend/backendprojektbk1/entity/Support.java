package com.sobolbetbackend.backendprojektbk1.entity;

import com.sobolbetbackend.backendprojektbk1.entity.common.UserE;
import com.sobolbetbackend.backendprojektbk1.entity.other.Contract;
import jakarta.persistence.*;

@Entity
public class Support{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne(mappedBy = "support")
    private Contract contract;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserE user;

    public Support() {
        user = new UserE();
    }

    public Contract getContract() {
        return contract;
    }

    public void setContract(Contract contract) {
        this.contract = contract;
    }

}
