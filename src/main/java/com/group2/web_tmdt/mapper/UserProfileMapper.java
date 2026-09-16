package com.group2.web_tmdt.mapper;


import com.group2.web_tmdt.dto.UserProfileResponse;
import com.group2.web_tmdt.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserProfileMapper {

    public UserProfileResponse toDTO(User user){
        if(user==null){
            return null;
        }
        UserProfileResponse res = new UserProfileResponse();
        res.setMaNguoiDung(user.getMaNguoiDung());
        res.setEmail(user.getEmail());
        res.setHoDem(user.getHoDem());
        res.setTen(user.getTen());
        res.setSoDienThoai(user.getSoDienThoai());
        res.setDiaChi(user.getDiaChi());
        res.setGioiTinh(user.getGioiTinh());
        res.setAvatar(user.getAvatar());
        res.setHobby(user.getHobby());
        res.setGoogleId(user.getGoogleId());
        res.setBirthDay(user.getBirthDay());
        res.setNgayDangKy(user.getNgayDangKy());
        res.setThoiGianChinhSua(user.getThoiGianChinhSua());
        return res;


    }
}
