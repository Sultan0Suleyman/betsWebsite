package com.sobolbetbackend.backendprojektbk1.repository.payment;

import com.sobolbetbackend.backendprojektbk1.entity.other.PaymentMethod;
import org.springframework.data.repository.CrudRepository;

public interface PaymentMethodRepo extends CrudRepository<PaymentMethod,String> {
}
