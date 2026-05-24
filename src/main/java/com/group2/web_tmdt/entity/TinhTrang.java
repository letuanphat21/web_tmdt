package com.group2.web_tmdt.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Data
@Table(name = "tinh_trang")
public class TinhTrang {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_tinh_trang")
    private Integer maTinhTrang;

    @Column(name = "ten_tinh_trang", nullable = false, unique = true, length = 100)
    private String tenTinhTrang;

    @Column(name = "mo_ta", columnDefinition = "TEXT")
    private String moTa;

    @OneToMany(mappedBy = "tinhTrang", fetch = FetchType.LAZY, cascade = {
            CascadeType.REFRESH,
            CascadeType.MERGE,
            CascadeType.DETACH,
    })
    private List<Product> products;
}
