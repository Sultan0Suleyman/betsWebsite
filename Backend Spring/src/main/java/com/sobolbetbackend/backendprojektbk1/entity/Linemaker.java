package com.sobolbetbackend.backendprojektbk1.entity;

import com.sobolbetbackend.backendprojektbk1.entity.common.Worker;
import jakarta.persistence.*;

@Entity
@DiscriminatorValue("LINEMAKER")
public class Linemaker extends Worker {
    // Дополнительные атрибуты и методы могут быть добавлены здесь

    public Linemaker() {
        super();
    }
}