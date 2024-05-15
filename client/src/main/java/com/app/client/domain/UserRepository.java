package com.app.client.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<ClientUser, Long> {

    ClientUser findByName(String name);
}
