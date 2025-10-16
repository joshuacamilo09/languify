package org.rest.languifybackend.User.Service;

import lombok.RequiredArgsConstructor;
import org.rest.languifybackend.User.DTO.UserDTO;
import org.rest.languifybackend.User.DTO.UserUpdateDTO;
import org.rest.languifybackend.User.Model.User;
import org.rest.languifybackend.User.UserRepo.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService
{
     private final UserRepository userRepository;

     public User getCurrentUser()
     {
         Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
         String email = authentication.getName();

         return userRepository.findByEmail(email)
                 .orElseThrow(() -> new RuntimeException("User not found"));
     }

     public UserDTO updateUser (UserUpdateDTO dto)
     {
         User CurrentUser = getCurrentUser();

         if (dto.getNome() != null) CurrentUser.setNome(dto.getNome());
         if (dto.getNative_Idiom() != null) CurrentUser.setNative_Idiom(dto.getNative_Idiom());
         if (dto.getSettings() != null) CurrentUser.setSettings(dto.getSettings());

         userRepository.save(CurrentUser);

         return UserDTO.builder()
                 .id(CurrentUser.getUser_id())
                 .nome(CurrentUser.getNome())
                 .email(CurrentUser.getEmail())
                 .native_Idiom(CurrentUser.getNative_Idiom())
                 .RegisterDate(CurrentUser.getRegisterDate())
                 .build();
     }

     public List<User> getAllusers(){
         return userRepository.findAll();
     }

     public Optional<User> getUserById(Long id){
         return userRepository.findById(id);
     }

     public void deleteUserById(Long id){
         userRepository.deleteById(id);
     }
}
