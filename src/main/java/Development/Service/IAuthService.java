/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package Development.Service;

import Development.DTOs.LoginRequestDTO;
import Development.DTOs.AuthResponseDTO;
import Development.DTOs.RegisterRequestDTO;

/**
 *
 * @author iikan
 */
public interface IAuthService {
    public AuthResponseDTO login(LoginRequestDTO dto);
    public void register(RegisterRequestDTO dto); 
}
