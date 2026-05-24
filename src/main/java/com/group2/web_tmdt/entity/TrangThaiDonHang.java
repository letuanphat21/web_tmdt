package com.group2.web_tmdt.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "trang_thai_don_hang")
@Data
public class TrangThaiDonHang {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ten_trang_thai", nullable = false)
    private String tenTrangThai;
}