package com.sobolbetbackend.backendprojektbk1.repository.otherRepos;

import com.sobolbetbackend.backendprojektbk1.entity.other.Role;
import org.springframework.data.repository.CrudRepository;
public interface RoleRepo extends CrudRepository<Role,Long>{
    Role findByName(String name);
}
