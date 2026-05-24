package com.group2.web_tmdt.controller;

import com.group2.web_tmdt.dao.TrangThaiDonHangRepository;
import com.group2.web_tmdt.dto.ApiResponse;
import com.group2.web_tmdt.dto.TrangThaiDonHangDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/order-statuses")
@RequiredArgsConstructor
public class TrangThaiDonHangController {

    private final TrangThaiDonHangRepository trangThaiDonHangRepository;

    @GetMapping
    public ResponseEntity<ApiResponse<List<TrangThaiDonHangDTO>>> getAllStatuses() {
        List<TrangThaiDonHangDTO> statuses = trangThaiDonHangRepository.findAll()
                .stream()
                .map(status -> new TrangThaiDonHangDTO(status.getId(), status.getTenTrangThai()))
                .toList();
        return ApiResponse.ok("Lấy danh sách trạng thái đơn hàng thành công!", statuses);
    }
}
