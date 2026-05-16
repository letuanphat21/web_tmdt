package com.group2.web_tmdt.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "user")
public class User {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "ma_nguoi_dung")
        private long maNguoiDung;

        @Column(name = "email")
        private String email;

        @Column(name = "mat_khau", length = 512)
        private String matKhau;

        @Column(name = "ho_dem")
        private String hoDem;

        @Column(name = "ten")
        private String ten;

        @Column(name = "so_dien_thoai")
        private String soDienThoai;

        @Column(name = "dia_chi")
        private String diaChi;

        @Column(name = "da_kich_hoat")
        private boolean daKichHoat;

        @Column(name = "ma_kich_hoat", unique = true)
        private String maKichHoat;

        @Column(name = "thoi_gian_het_han_ma_kich_hoat")
        private LocalDateTime thoiGianHetHanMaKichHoat;

        @Column(name = "gioi_tinh")
        private Character gioiTinh;

        @Column(name = "avatar", columnDefinition = "LONGTEXT")
        @Lob
        private String avatar;

        @Column(name = "active")
        private boolean active;

        @Column(name = "google_id", unique = true)
        private String googleId;

        @Column(name = "hobby")
        private String hobby;

        @CreationTimestamp
        @Column(name = "ngay_dang_ky", updatable = false)
        private LocalDateTime ngayDangKy;

        @UpdateTimestamp
        @Column(name = "thoi_gian_chinh_sua")
        private LocalDateTime thoiGianChinhSua;

        @Column(name = "birth_date")
        private LocalDate birthDay;

        @ManyToMany(fetch = FetchType.EAGER, cascade = {
                        CascadeType.PERSIST,
                        CascadeType.MERGE,
                        CascadeType.REFRESH,
                        CascadeType.DETACH
        })
        @JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "ma_nguoi_dung"), inverseJoinColumns = @JoinColumn(name = "ma_quyen"))
        private List<Role> roles;

        @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = {
                        CascadeType.REFRESH,
                        CascadeType.MERGE,
                        CascadeType.DETACH,
                        CascadeType.REFRESH,
        })
        private List<Product> products;

        @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = {
                        CascadeType.REFRESH,
                        CascadeType.MERGE,
                        CascadeType.DETACH,
                        CascadeType.REFRESH,
        })
        private List<Review> reviews;

        @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = {
                        CascadeType.REFRESH,
                        CascadeType.MERGE,
                        CascadeType.DETACH,
                        CascadeType.REFRESH,
        })
        private List<DonHang> donHangs;

}
