package com.shopme.admin.user;

import com.shopme.commen.entity.Role;
import com.shopme.commen.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class UserRepositoryTests {

    @Autowired
    private UserRepository repo;

    @Autowired
    private TestEntityManager testEntityManager;

    //create tests.
    @Test
    public void testCreateFirstUser() {
        Role adminRole = testEntityManager.find(Role.class, 1);
        User sheikh = new User("sheikh@gmail.com", "1234", "sheikh", "aveSina");
        sheikh.addRole(adminRole);

        User savedUser = repo.save(sheikh);

        assertThat(savedUser.getId()).isGreaterThan(0);
    }

    @Test
    public void testCreatUserWithTwoRoles()
    {
        User plato =  new User("plato@gmail.com", "123", "plato", "mosol");
        Role roleEditor = new Role(3);
        Role roleAssistant = new Role(5);

        plato.addRole(roleAssistant);
        plato.addRole(roleEditor);

        User savedUser = repo.save(plato);

        assertThat(savedUser.getId()).isGreaterThan(0);
    }

    // need some(2) tests for read operations. but not now

    //update tests.
    @Test
    public void testUpdateUserDetails()
    {
        User plato = repo.findById(2).get();
        plato.setEnabled(true);
        plato.setEmail("plato@yahoo.com");

        repo.save(plato);
    }

    @Test
    public void testUpdateUserRole()
    {
        User plato = repo.findById(2).get();
        Role roleEditor = new Role(3);
        plato.getRoles().remove(roleEditor);
        plato.addRole(new Role(2));

        repo.save(plato);
    }

    //delete tests
    @Test
    public void testDeleteUser()
    {
        Integer userId = 2;
        repo.deleteById(userId);
    }
}
