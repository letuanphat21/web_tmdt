package com.group2.web_tmdt.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/conditions")
@RequiredArgsConstructor
public class TrangThaiSanPham {

    private TrangThaiSanPhamService trangThaiSanPhamService;
}
