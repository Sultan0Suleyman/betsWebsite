package com.sobolbetbackend.backendprojektbk1.repository.workerRegistrationRepos;

import com.sobolbetbackend.backendprojektbk1.entity.common.Worker;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface WorkerRepo extends CrudRepository<Worker,Long> {
    Optional<Worker> findByUserId(Long userId);
}
