/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package Development.Service;

import Development.DTOs.UpUserRoleDTO;
import Development.DTOs.UpUserStatusDTO;
import Development.DTOs.UserInfoDTO;
import Development.Model.User;
import java.util.List;

/**
 *
 * @author iikan
 */

public interface IUserService {
    public void changePassword(String userId, String password);
    public void changeStatus(String userId, UpUserStatusDTO dto);
    public void changeRole(String userId, UpUserRoleDTO dto);
    public List<UserInfoDTO> ListUsers();
    public UserInfoDTO findUser(String userId);
}
