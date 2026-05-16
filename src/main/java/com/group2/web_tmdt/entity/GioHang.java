package com.group2.web_tmdt.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "gio_hang")
public class GioHang {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_gio_hang")
    private long maGioHang;

    @OneToOne(cascade = {
            CascadeType.DETACH,
            CascadeType.REFRESH,
            CascadeType.MERGE,
            CascadeType.PERSIST,
    })
    @JoinColumn(name = "ma_nguoi_dung", nullable = false, unique = true)
    private User user;

    @OneToMany(mappedBy = "gioHang", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GioHangItem> items = new ArrayList<>();
}
