package com.group2.web_tmdt.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "giao_dich")
public class GiaoDich {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_nguoi_dung")
    @JsonIgnore
    @JsonIgnoreProperties({"roles", "products", "reviews", "donHangs", "hibernateLazyInitializer", "handler"})
    private User user;

    private Double soTien; // Ví dụ: 50000
    private String loaiGiaoDich; // "inflow" (tiền vào) hoặc "outflow" (tiền ra)
    private String moTa; // Chi tiết: "Rút tiền về ngân hàng", "Bán được áo"...
    private String trangThai; // "Thành công", "Đang xử lý"...

    @CreationTimestamp
    @Column(name = "ngay_tao", updatable = false)
    private LocalDateTime ngayTao;
}