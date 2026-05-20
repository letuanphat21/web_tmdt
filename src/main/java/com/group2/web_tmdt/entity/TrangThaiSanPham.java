package com.group2.web_tmdt.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "trang_thai_san_pham")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrangThaiSanPham {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_trang_thai")
    private Integer id;

    @Column(name = "ten_trang_thai",nullable = false, unique = true)
    private String tenTrangThai;


}
