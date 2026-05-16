package com.group2.web_tmdt.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "gio_hang_item")
public class GioHangItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_item")
    private long maItem;

    @Column(name = "so_luong")
    private int soLuong;

    @ManyToOne(cascade = {
            CascadeType.DETACH,
            CascadeType.REFRESH,
            CascadeType.MERGE,
            CascadeType.PERSIST,
    })
    @JoinColumn(name = "ma_san_pham", nullable = false)
    private Product product;

    @ManyToOne(cascade = {
            CascadeType.DETACH,
            CascadeType.REFRESH,
            CascadeType.MERGE,
            CascadeType.PERSIST,
    })
    @JoinColumn(name = "ma_gio_hang", nullable = false)
    private GioHang gioHang;
}
