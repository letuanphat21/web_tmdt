package com.group2.web_tmdt.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Entity
@Table(name = "user")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
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

    @Column(name = "so_du", columnDefinition = "DOUBLE DEFAULT 0.0")
    private Double soDu = 0.0;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "ma_nguoi_dung"), inverseJoinColumns = @JoinColumn(name = "ma_quyen"))
    private List<Role> roles;

    @JsonIgnore // Chặn quét tuần hoàn danh sách sản phẩm
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = {
            CascadeType.REFRESH,
            CascadeType.MERGE,
            CascadeType.DETACH,
    })
    private List<Product> products;

    @JsonIgnore // Chặn quét tuần hoàn danh sách đánh giá
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = {
            CascadeType.REFRESH,
            CascadeType.MERGE,
            CascadeType.DETACH,
    })
    private List<Review> reviews;

    @JsonIgnoreProperties({"user"}) // Ngăn chặn đứt gãy khi DonHang gọi ngược lại User
    @JsonIgnore // Không cho Jackson cố serialize list đơn hàng khi đang gọi thông tin Ví
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = {
            CascadeType.REFRESH,
            CascadeType.MERGE,
            CascadeType.DETACH,
    })
    private List<DonHang> donHangs;




//    ------------------------ Phần app chat
    @ManyToMany
    @JoinTable(
            name= "user_friends",
            joinColumns =@JoinColumn(name="user_id"),
            inverseJoinColumns = @JoinColumn(name ="friend_id")
    )
    @ToString.Exclude
    private Set<User> friends = new HashSet<>();


    // Các conversation tham gia
    @ManyToMany(mappedBy = "members")
    @ToString.Exclude
    private Set<Conversation> conversations = new HashSet<>();

    @OneToMany(mappedBy = "sender")
    private List<Message> sentMessages;








}