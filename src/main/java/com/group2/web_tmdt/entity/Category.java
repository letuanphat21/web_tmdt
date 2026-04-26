package com.group2.web_tmdt.entity;


import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
@Table(name="category")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="ma_the_loai")
    private int maTheLoai;

    @Column(name = "ten_the_loai")
    private String tenTheLoai;

    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY, cascade = {
            CascadeType.REFRESH,
            CascadeType.MERGE,
            CascadeType.DETACH,
            CascadeType.REFRESH,
    })
    private List<Product> products;

}
