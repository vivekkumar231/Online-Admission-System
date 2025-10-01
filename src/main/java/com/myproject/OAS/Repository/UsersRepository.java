package com.myproject.OAS.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.myproject.OAS.Model.Users;
import com.myproject.OAS.Model.Users.UserRole;
import com.myproject.OAS.Model.Users.UserStatus;

public interface UsersRepository  extends JpaRepository<Users, Long>{

	boolean existsByEmail(String userId);

	Users findByEmail(String userId);

	

	List<Users> findAllByRoleAndStatus(UserRole student, UserStatus pending);

	boolean existsByRollNo(String userId);

	Users findByRollNo(String userId);

	List<Users> findAllByRoleAndStatusOrStatus(UserRole student, UserStatus approved, UserStatus disabled);

	

}
