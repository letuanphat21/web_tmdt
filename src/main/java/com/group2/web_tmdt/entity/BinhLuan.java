package com.group2.web_tmdt.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Table(name = "binh_luan")
public class BinhLuan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_binh_luan")
    private long maBinhLuan;

    @Column(name = "noi_dung", nullable = false, length = 1000)
    private String noiDung;

    @CreationTimestamp
    @Column(name = "thoi_gian_tao", updatable = false)
    private LocalDateTime thoiGianTao;

    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.REFRESH, CascadeType.MERGE, CascadeType.PERSIST})
    @JoinColumn(name = "ma_san_pham", nullable = false)
    private Product product;

    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.REFRESH, CascadeType.MERGE, CascadeType.PERSIST})
    @JoinColumn(name = "ma_nguoi_dung", nullable = false)
    private User user;

    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.REFRESH})
    @JoinColumn(name = "ma_binh_luan_cha", nullable = true)
    private BinhLuan binhLuanCha;

    @OneToMany(mappedBy = "binhLuanCha", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<BinhLuan> traLoi;
}
