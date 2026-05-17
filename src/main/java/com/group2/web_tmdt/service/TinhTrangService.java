package com.group2.web_tmdt.service;

import com.group2.web_tmdt.dao.TinhTrangRepository;
import com.group2.web_tmdt.dto.TinhTrangDTO;
import com.group2.web_tmdt.entity.TinhTrang;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TinhTrangService {

    private final TinhTrangRepository tinhTrangRepository;
    private final ModelMapper modelMapper;

    /**
     * Lấy tất cả tình trạng sản phẩm
     */
    public List<TinhTrangDTO> getAllStatuses() {
        return tinhTrangRepository.findAll().stream()
                .map(tinhTrang -> modelMapper.map(tinhTrang, TinhTrangDTO.class))
                .collect(Collectors.toList());
    }

    /**
     * Lấy tình trạng theo ID
     */
    public TinhTrangDTO getStatusById(int maTinhTrang) {
        TinhTrang tinhTrang = tinhTrangRepository.findById(maTinhTrang)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tình trạng với ID: " + maTinhTrang));
        return modelMapper.map(tinhTrang, TinhTrangDTO.class);
    }

    /**
     * Lấy tình trạng theo tên
     */
    public TinhTrangDTO getStatusByName(String tenTinhTrang) {
        TinhTrang tinhTrang = tinhTrangRepository.findByTenTinhTrang(tenTinhTrang)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tình trạng: " + tenTinhTrang));
        return modelMapper.map(tinhTrang, TinhTrangDTO.class);
    }

    /**
     * Tạo mới tình trạng
     */
    public TinhTrangDTO createStatus(TinhTrangDTO tinhTrangDTO) {
        if (tinhTrangRepository.existsByTenTinhTrang(tinhTrangDTO.getTenTinhTrang())) {
            throw new RuntimeException("Tình trạng '" + tinhTrangDTO.getTenTinhTrang() + "' đã tồn tại!");
        }

        TinhTrang tinhTrang = modelMapper.map(tinhTrangDTO, TinhTrang.class);
        TinhTrang savedTinhTrang = tinhTrangRepository.save(tinhTrang);

        return modelMapper.map(savedTinhTrang, TinhTrangDTO.class);
    }

    /**
     * Cập nhật tình trạng
     */
    public TinhTrangDTO updateStatus(int maTinhTrang, TinhTrangDTO tinhTrangDTO) {
        TinhTrang tinhTrang = tinhTrangRepository.findById(maTinhTrang)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tình trạng với ID: " + maTinhTrang));

        if (!tinhTrang.getTenTinhTrang().equals(tinhTrangDTO.getTenTinhTrang()) &&
                tinhTrangRepository.existsByTenTinhTrang(tinhTrangDTO.getTenTinhTrang())) {
            throw new RuntimeException("Tình trạng '" + tinhTrangDTO.getTenTinhTrang() + "' đã tồn tại!");
        }

        tinhTrang.setTenTinhTrang(tinhTrangDTO.getTenTinhTrang());
        tinhTrang.setMoTa(tinhTrangDTO.getMoTa());

        TinhTrang updatedTinhTrang = tinhTrangRepository.save(tinhTrang);

        return modelMapper.map(updatedTinhTrang, TinhTrangDTO.class);
    }

    /**
     * Xóa tình trạng
     */
    public void deleteStatus(int maTinhTrang) {
        if (!tinhTrangRepository.existsById(maTinhTrang)) {
            throw new RuntimeException("Không tìm thấy tình trạng với ID: " + maTinhTrang);
        }
        tinhTrangRepository.deleteById(maTinhTrang);
    }
}
