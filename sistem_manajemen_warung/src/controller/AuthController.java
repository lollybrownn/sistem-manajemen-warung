package controller;

import dao.DaoFactory;
import model.User;

public class AuthController {
    private static User currentUser;

    public User login(String username, String password) {
        if (username == null || username.isBlank() || password == null || password.isBlank()) return null;
        return DaoFactory.getUserDao().findByUsername(username.trim())
                .filter(u -> u.getPassword().equals(password) && u.isAktif())
                .map(u -> { currentUser = u; return u; })
                .orElse(null);
    }

    public void logout() { currentUser = null; }
    public static User getCurrentUser() { return currentUser; }
}
