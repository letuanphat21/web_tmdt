package com.group2.web_tmdt.entity;


import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name="product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="ma_san_pham")
    private long maSanPham;

    @Column(name = "ten_san_pham",length = 256)
    private String tenSanPham;

    @Column(name="so_luong")
    private int soLuong;

    @Column(name="gia_san_pham")
    private double giaSanPham;

    @ManyToOne(cascade = {
            CascadeType.DETACH,
            CascadeType.REFRESH,
            CascadeType.MERGE,
            CascadeType.PERSIST,
    })
    @JoinColumn(name = "ma_nguoi_dung",nullable = false)
    private User user;

    @ManyToOne(cascade = {
            CascadeType.DETACH,
            CascadeType.REFRESH,
            CascadeType.MERGE,
            CascadeType.PERSIST,
    })
    @JoinColumn(name = "ma_the_loai",nullable = false)
    private Category category;

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY, cascade = {
            CascadeType.REFRESH,
            CascadeType.MERGE,
            CascadeType.DETACH,
            CascadeType.REFRESH,
    })
    private List<Review> reviews;


    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY, cascade = {
            CascadeType.REFRESH,
            CascadeType.MERGE,
            CascadeType.DETACH,
            CascadeType.REFRESH,
    })
    private List<HinhAnh> hinhAnhs;

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY, cascade = {
            CascadeType.REFRESH,
            CascadeType.MERGE,
            CascadeType.DETACH,
            CascadeType.REFRESH,
    })
    private List<ChiTietDonHang> chiTietDonHangs;


}
