package com.group2.web_tmdt.entity;


import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "chi_tiet_don_hang")
public class ChiTietDonHang {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_chi_tiet_don_hang")
    private long maChiTietDonHang;

    @Column(name = "so_luong")
    private int soLuong;

    @Column(name = "gia_ban")
    private double giaBan;

    @ManyToOne(cascade = {
            CascadeType.DETACH,
            CascadeType.MERGE,
            CascadeType.REFRESH,
            CascadeType.PERSIST,
    })
    @JoinColumn(name = "ma_sach",nullable = false)
    private Product product;

    @ManyToOne(cascade = {
            CascadeType.DETACH,
            CascadeType.MERGE,
            CascadeType.REFRESH,
            CascadeType.PERSIST,
    })
    @JoinColumn(name = "ma_don_hang",nullable = false)
    private DonHang donHang;

}
