/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package Model.User;

import java.util.List;

/**
 *
 * @author ASUS
 */
public interface IDAOUser {
    public void insert(User user);
    public void update(User user);
    public void delete(int id);
    public User getById(int id);
    public List<User> getAll();
    
    public User getUserByUsername(String username);
    public void updateStatusOnline(String username, String status);
    public User login(String username, String password, String role);
}
