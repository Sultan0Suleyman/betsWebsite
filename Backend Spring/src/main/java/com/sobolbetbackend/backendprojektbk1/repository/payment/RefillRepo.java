package com.sobolbetbackend.backendprojektbk1.repository.payment;

import com.sobolbetbackend.backendprojektbk1.entity.events.payment.Refill;
import org.springframework.data.repository.CrudRepository;

public interface RefillRepo extends CrudRepository<Refill,Long> {
}
