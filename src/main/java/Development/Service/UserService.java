/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Development.Service;

import Development.DTOs.UpUserRoleDTO;
import Development.DTOs.UpUserStatusDTO;
import Development.DTOs.UserInfoDTO;
import Development.Model.User;
import Development.Repository.IUserRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 *
 * @author iikan
 */

@Service
public class UserService implements IUserService{
    
    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private PasswordValidatorService passwordValidator;
    
    @Override
    public void changePassword(String userId, String password){
        
        User user = userRepository.findById(userId).orElseThrow(
                () -> new IllegalArgumentException("No existe un usuario con ID: " + userId)
        );
        
        
        if(!passwordValidator.isValidPassword(password)) 
            throw new IllegalArgumentException(passwordValidator.getPasswordRequirements());
        
        user.setPassword(passwordEncoder.encode(password));
        
        userRepository.save(user);
    
    }

    @Override
    public void changeStatus(String userId, UpUserStatusDTO dto) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new IllegalArgumentException("No existe usuario con ID: " + userId)
        );
        
        try{
            user.setStatus(dto.getStatus());
            userRepository.save(user);
        }catch(Exception ex){
            throw new RuntimeException("Error inesperado al ACTUALIZAR ESTADO: " + ex.getMessage());
        }
    }

    @Override
    public void changeRole(String userId, UpUserRoleDTO dto) {
        User user = userRepository.findById(userId).orElseThrow(
            () -> new IllegalArgumentException("No existe usuario con ID: " + userId)
        );

        try{
            user.setRole(dto.getRole());
            userRepository.save(user);
        }catch(Exception ex){
            throw new RuntimeException("Error inesperado al ACTUALIZAR ROL: " + ex.getMessage());
        }
        
    }

    @Override
    public List<UserInfoDTO> ListUsers() {
        try{
            return userRepository.findAllUsers();
        }catch(Exception ex){
            throw new RuntimeException("Error inesperado al LISTAR USUARIOS: " + ex.getMessage());
        }
    }

    @Override
    public UserInfoDTO findUser(String userId) {
        
        UserInfoDTO user = userRepository.findByUserId(userId);
        
        if(user == null)
            throw new IllegalArgumentException("No existe un usuario con ID: " + userId);
        
        return user;
    }
    
}
