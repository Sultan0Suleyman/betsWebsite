package com.sobolbetbackend.backendprojektbk1.repository.payment;

import com.sobolbetbackend.backendprojektbk1.entity.events.payment.Withdraw;
import org.springframework.data.repository.CrudRepository;

public interface WithdrawRepo extends CrudRepository<Withdraw, Long> {
}
