package ncadvanced2018.groupeone.parent.service;

import ncadvanced2018.groupeone.parent.model.entity.User;

public interface UserService {

    User create(User user);

    User findByEmail(String email);

    User findById(Long id);

    boolean delete(String email);

    boolean update(User employee);

    boolean delete(User employee);

    boolean delete(Long id);

}
