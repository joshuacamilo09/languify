package org.rest.languifybackend.User.Controller;

import lombok.RequiredArgsConstructor;
import org.rest.languifybackend.User.DTO.UserDTO;
import org.rest.languifybackend.User.DTO.UserUpdateDTO;
import org.rest.languifybackend.User.Model.User;
import org.rest.languifybackend.User.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/languify/user")
public class UserController
{
    @Autowired
    private UserService userService;

    @GetMapping("/MyProfile/{id}")
    public ResponseEntity<UserDTO> getMyProfile(@PathVariable Long id){
        User user = userService.getCurrentUser();
        UserDTO userDTO = UserDTO.builder()
                .id(user.getUserId())
                .nome(user.getUsername())
                .email(user.getEmail())
                .RegisterDate(user.getRegisterDate())
                .native_idiom(user.getNative_idiom())
                .build();

        return ResponseEntity.ok(userDTO);
    }

    @PutMapping("UpdateProfile/{id}")
    public ResponseEntity<UserDTO>updateProfile(@PathVariable Long id, @RequestBody UserUpdateDTO user)
    {
        return ResponseEntity.ok(userService.updateUser(user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserByID(@PathVariable Long id)
    {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers(){
        return ResponseEntity.ok(userService.getAllusers());
    }

    @DeleteMapping("/DeleteUser/{id}")
    public ResponseEntity<Void>deleteUserByID(@PathVariable Long id)
    {
        userService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }
}
