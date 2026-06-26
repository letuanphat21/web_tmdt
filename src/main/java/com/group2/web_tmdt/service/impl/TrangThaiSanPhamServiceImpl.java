package com.group2.web_tmdt.service.impl;

import com.group2.web_tmdt.dao.TrangThaiSanPhamRepository;
import com.group2.web_tmdt.dto.TrangThaiSanPhamDTO;
import com.group2.web_tmdt.entity.TrangThaiSanPham;
import com.group2.web_tmdt.mapper.TrangThaiSanPhamMapper;
import com.group2.web_tmdt.service.TrangThaiSanPhamService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TrangThaiSanPhamServiceImpl implements TrangThaiSanPhamService {

    private final TrangThaiSanPhamRepository trangThaiSanPhamRepository;
    private final TrangThaiSanPhamMapper trangThaiSanPhamMapper;


    @Override
    public List<TrangThaiSanPhamDTO> getAll() {
        List<TrangThaiSanPham> result= trangThaiSanPhamRepository.findAll();
        return result.stream()
                .map(trangThaiSanPhamMapper::toDT0)
                .toList();
    }
}
