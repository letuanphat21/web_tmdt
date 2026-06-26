package com.group2.web_tmdt.controller;


import com.group2.web_tmdt.dto.ApiResponse;
import com.group2.web_tmdt.dto.TrangThaiSanPhamDTO;
import com.group2.web_tmdt.service.TrangThaiSanPhamService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/conditions")
@RequiredArgsConstructor
public class TrangThaiSanPhamController {

    private final TrangThaiSanPhamService trangThaiSanPhamService;


    @GetMapping()
    public ResponseEntity<ApiResponse<List<TrangThaiSanPhamDTO>>> getAllsTrangThaiSanPham(){
        List<TrangThaiSanPhamDTO> trangThaiSanPhamDTO = trangThaiSanPhamService.getAll();
        return ApiResponse.ok("Lấy danh sách trạng thái sản phầm thành công", trangThaiSanPhamDTO);
    }

}
