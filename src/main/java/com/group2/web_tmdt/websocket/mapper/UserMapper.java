package com.group2.web_tmdt.websocket.mapper;


import com.group2.web_tmdt.entity.HinhAnh;
import com.group2.web_tmdt.entity.User;
import com.group2.web_tmdt.websocket.dto.UserResponse;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserResponse toDTO(User user){

        if(user == null){
            return null;
        }
        return new UserResponse(user.getMaNguoiDung(), user.getEmail());

    }
}
