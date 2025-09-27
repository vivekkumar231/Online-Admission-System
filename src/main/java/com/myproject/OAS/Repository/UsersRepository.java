package com.myproject.OAS.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.myproject.OAS.Model.Users;

public interface UsersRepository  extends JpaRepository<Users, Long>{

	boolean existsByEmail(String userId);

	Users findByEmail(String userId);

}
