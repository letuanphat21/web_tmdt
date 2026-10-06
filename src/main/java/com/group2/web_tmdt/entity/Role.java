package com.group2.web_tmdt.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
@Table(name= "role")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_quyen")
    private long maQuyen;

    @Column(name = "ten_quyen")
    private String tenQuyen;

    @ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY)
    private List<User> danhSachNguoiDung;

}
