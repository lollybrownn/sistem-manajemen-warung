package Model.User;

import Model.Connector;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class DAOUser implements IDAOUser {

    @Override
    public void insert(User user) {

        String sql =
        "INSERT INTO user "
        + "(username, password, nama_lengkap, role, status_online) "
        + "VALUES (?, ?, ?, ?, ?)";

        try (

            Connection conn =
                    Connector.connect();

            PreparedStatement ps =
                    conn.prepareStatement(sql)

        ){

            ps.setString(1,
                    user.getUsername());

            ps.setString(2,
                    user.getPassword());

            ps.setString(3,
                    user.getNamaLengkap());

            ps.setString(4,
                    user.getRole());

            ps.setString(5,
                    user.getStatus());

            ps.executeUpdate();

        } catch (Exception e){

            e.printStackTrace();
        }
    }

    @Override
    public void update(User user) {

    }

    @Override
    public void delete(int id) {

    }

    @Override
    public User getById(int id) {
        return null;
    }

    @Override
    public List<User> getAll() {

        List<User> listUser =
                new ArrayList<>();

        String sql =
                "SELECT * FROM user";

        try (

            Connection conn =
                    Connector.connect();

            PreparedStatement ps =
                    conn.prepareStatement(sql);

            ResultSet rs =
                    ps.executeQuery()

        ){

            while(rs.next()){

                User user = new User(
                        rs.getInt("id_user"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("nama_lengkap"),
                        rs.getString("role"),
                        rs.getString("status_online")
                );

                listUser.add(user);
            }

        } catch (Exception e){

            e.printStackTrace();
        }

        return listUser;
    }

    @Override
    public User getUserByUsername(
            String username
    ) {
        return null;
    }

    @Override
    public void updateStatusOnline(
            String username,
            String status
    ) {

        String sql =
        "UPDATE user "
        + "SET status_online=? "
        + "WHERE username=?";

        try (

            Connection conn =
                    Connector.connect();

            PreparedStatement ps =
                    conn.prepareStatement(sql)

        ){

            ps.setString(1, status);
            ps.setString(2, username);

            ps.executeUpdate();

        } catch (Exception e){

            e.printStackTrace();
        }
    }

    @Override
    public User login(
            String username,
            String password,
            String role
    ){

        String sql =
        "SELECT * FROM user "
        + "WHERE username=? "
        + "AND password=? "
        + "AND role=?";

        try (

            Connection conn =
                    Connector.connect();

            PreparedStatement ps =
                    conn.prepareStatement(sql)

        ){

            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, role);

            ResultSet rs =
                    ps.executeQuery();

            if(rs.next()){

                User user = new User(
                        rs.getInt("id_user"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("nama_lengkap"),
                        rs.getString("role"),
                        rs.getString("status_online")
                );

                return user;
            }

        } catch (Exception e){

            e.printStackTrace();
        }

        return null;
    }
}