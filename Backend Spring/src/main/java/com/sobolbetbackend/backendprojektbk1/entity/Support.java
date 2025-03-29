package com.sobolbetbackend.backendprojektbk1.entity;

import com.sobolbetbackend.backendprojektbk1.entity.common.Worker;
import jakarta.persistence.*;

@Entity
@DiscriminatorValue("SUPPORT")
public class Support extends Worker {
    // Дополнительные атрибуты и методы могут быть добавлены здесь

    public Support() {
        super();
    }


}