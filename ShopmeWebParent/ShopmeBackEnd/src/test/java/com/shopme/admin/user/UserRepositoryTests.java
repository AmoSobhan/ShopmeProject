package com.shopme.admin.user;

import com.shopme.commen.entity.Role;
import com.shopme.commen.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Rollback;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = true)
public class UserRepositoryTests {

	@Autowired
	private UserRepository repo;

	@Autowired
	private TestEntityManager testEntityManager;

	// create tests.
	@Test
	public void testCreateFirstUser() {
		Role adminRole = testEntityManager.find(Role.class, 1);
		User sheikh = new User("sheikh@gmail.com", "1234", "sheikh", "aveSina");
		sheikh.addRole(adminRole);

		User savedUser = repo.save(sheikh);

		assertThat(savedUser.getId()).isGreaterThan(0);
	}

	@Test
	public void testCreatUserWithTwoRoles() {
		User plato = new User("plato@gmail.com", "123", "plato", "mosol");
		Role roleEditor = new Role(3);
		Role roleAssistant = new Role(5);

		plato.addRole(roleAssistant);
		plato.addRole(roleEditor);

		User savedUser = repo.save(plato);

		assertThat(savedUser.getId()).isGreaterThan(0);
	}

	// need some(2) tests for read operations. but not now

	// update tests.
	@Test
	public void testUpdateUserDetails() {
		User plato = repo.findById(2).get();
		plato.setEnabled(true);
		plato.setEmail("plato@yahoo.com");

		repo.save(plato);
	}

	@Test
	public void testUpdateUserRole() {
		User plato = repo.findById(2).get();
		Role roleEditor = new Role(3);
		plato.getRoles().remove(roleEditor);
		plato.addRole(new Role(2));

		repo.save(plato);
	}

	// delete tests
	@Test
	public void testDeleteUser() {
		Integer userId = 2;
		repo.deleteById(userId);
	}

	@Test
	public void testGetUserByEmail() {
		String email = "sob0@gmail.com";
		User user = repo.getUserByEmail(email);

		assertThat(user).isNotNull();
	}

	@Test
	public void testEncryptedPassword() {
		String email = "123@gm.com";
		User user = repo.getUserByEmail(email);
		// saved password of the user in database is '12345678'
		String encodedSavedPassword = user.getPassword();
		PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		boolean result = passwordEncoder.matches("12345678", encodedSavedPassword);
		assertThat(result).isTrue();
	}

	@Test
	public void testCountById() {
		Integer id = 1;
		Long countById = repo.countById(id);
		assertThat(countById).isNotNull().isGreaterThan(0);
	}

	@Test
	public void testUpdateEnableStatus() {
		Integer id = 40;
		repo.updateEnabledStatus(id, true);
	}

	@Test
	public void testFirstPage() {
		int pageNumber = 0;
		int pageSize = 4;

		Pageable pageable = PageRequest.of(pageNumber, pageSize);
		Page<User> page = repo.findAll(pageable);

		List<User> listUsers = page.getContent();

		listUsers.forEach(user -> System.out.println(user));

		assertThat(listUsers.size()).isEqualTo(pageSize);

	}

}
