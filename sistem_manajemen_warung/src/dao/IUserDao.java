package dao;

import model.User;
import java.util.List;
import java.util.Optional;

public interface IUserDao {
    boolean insert(User user);
    boolean update(User user);
    boolean delete(int id);
    Optional<User> findById(int id);
    Optional<User> findByUsername(String username);
    List<User>findAll();
    List<User>findByRole(String role);
    List<User>findAllActive();
    boolean isUsernameExists(String username);
}
