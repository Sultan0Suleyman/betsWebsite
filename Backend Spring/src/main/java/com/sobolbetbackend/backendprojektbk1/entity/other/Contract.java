package com.sobolbetbackend.backendprojektbk1.entity.other;

import com.sobolbetbackend.backendprojektbk1.entity.Linemaker;
import com.sobolbetbackend.backendprojektbk1.entity.Support;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
public class Contract {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    @OneToOne
    @JoinColumn(name = "linemaker_id", referencedColumnName = "id", nullable = true)
    private Linemaker linemaker;
    @NotNull
    @OneToOne
    @JoinColumn(name = "support_id", referencedColumnName = "id", nullable = true)
    private Support support;
    private LocalDate createdAt;
    private Double salary;

    public Contract() {
        this.createdAt = LocalDate.now();
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    public Double getSalary() {
        return salary;
    }

    public void setSalary(Double salary) {
        this.salary = salary;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Linemaker getLinemaker() {
        return linemaker;
    }

    public void setLinemaker(Linemaker linemaker) {
        this.linemaker = linemaker;
    }

    public Support getSupport() {
        return support;
    }

    public void setSupport(Support support) {
        this.support = support;
    }
}
