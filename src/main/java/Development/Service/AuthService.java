/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/springframework/Service.java to edit this template
 */
package Development.Service;

import Development.DTOs.AuthResponseDTO;
import Development.DTOs.LoginRequestDTO;
import Development.DTOs.RegisterRequestDTO;
import Development.Model.Role;
import Development.Model.Status;
import Development.Model.User;
import Development.Model.UserDetail;
import Development.Repository.IUserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 *
 * @author iikan
 */
@Service
public class AuthService implements IAuthService{

    @Autowired
    private IUserRepository  userRepository;
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
   
    @Autowired
    private PasswordValidatorService passwordValidator;
    
    @Override
    public AuthResponseDTO login(LoginRequestDTO dto) {
        try{
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            dto.getUserName(),
                            dto.getPassword()
                    )
            );
            
            UserDetail userDetail = (UserDetail) authentication.getPrincipal();
            User user = userDetail.getUser();
            
            return new AuthResponseDTO(
                    user.getId(),
                    user.getFullName(),
                    user.getRole(),
                    user.getStatus()
            
            );
            
        }catch(BadCredentialsException ex){
            throw new RuntimeException(ex.getMessage());
        }catch(AuthenticationException ex){
            throw new RuntimeException("Usuario o contraseña incorrectos");
        }
        
    }

    @Override
    public void register(RegisterRequestDTO dto) {
        if(userRepository.count() == 0){
        
            if(!passwordValidator.isValidPassword(dto.getPassword())){
                throw new RuntimeException(passwordValidator.getPasswordRequirements());
            }

            User userAdmin = new User();
            userAdmin.setUserName(dto.getUserName());
            userAdmin.setPassword(passwordEncoder.encode(dto.getPassword()));
            userAdmin.setFullName(dto.getFullName());
            userAdmin.setEmail(dto.getEmail());
            userAdmin.setRole(Role.SUPERADMIN);  
            userAdmin.setStatus(Status.ACTIVE);  // Activo por defecto
            userRepository.save(userAdmin);
            
            return;
        }
        
        if(userRepository.existsByUserName(dto.getUserName()))
            throw new RuntimeException("¡Ya existe un usuario llamado: " + dto.getUserName() + "!");
        
        if(!passwordValidator.isValidPassword(dto.getPassword())){
            throw new RuntimeException(passwordValidator.getPasswordRequirements());
        }

        User user = new User();
        user.setUserName(dto.getUserName());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setFullName(dto.getFullName());
        user.setRole(dto.getRole());  
        user.setStatus(Status.ACTIVE);  // Activo por defecto
        userRepository.save(user);
        
    }
    
}
