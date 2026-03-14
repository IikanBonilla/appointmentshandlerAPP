/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package Development.Repository;

import Development.DTOs.UserInfoDTO;
import Development.Model.Role;
import Development.Model.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 *
 * @author iikan
 */
public interface IUserRepository extends JpaRepository<User, String>{
    
    public User findByUserName(String userName);
    public boolean existsByUserName(String userName);
    public boolean existsByRole(Role role);
    
    @Query("""
           SELECT new Development.DTOs.UserInfoDTO(
            u.id,
            u.userName,
            u.fullName,
            u.role,
            u.status
           )
           FROM User u
           WHERE u.id = ?1
           """)
    public UserInfoDTO findByUserId(String id);
    
    @Query("""
            SELECT new Development.DTOs.UserInfoDTO(
             u.id,
             u.userName,
             u.fullName,
             u.role,
             u.status
            )
            FROM User u
           """)
    public List<UserInfoDTO> findAllUsers();
}
