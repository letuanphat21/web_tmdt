package com.group2.web_tmdt.service.impl;

import com.group2.web_tmdt.dao.*;
import com.group2.web_tmdt.dto.*;
import com.group2.web_tmdt.entity.*;
import com.group2.web_tmdt.exception.BusinessException;
import com.group2.web_tmdt.mapper.ProductMapper;
import com.group2.web_tmdt.mapper.ProductSellerMapper;
import com.group2.web_tmdt.service.EmailService;
import com.group2.web_tmdt.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final HinhAnhRepository hinhAnhRepository;
    private final CategoryRepository categoryRepository;
    private final TinhTrangRepository tinhTrangRepository;
    private final TrangThaiSanPhamRepository trangThaiSanPhamRepository;
    private final EmailService emailService;
    private final ProductMapper productMapper;
    private final ProductSellerMapper productSellerMapper;

    @Override
    public List<ProductDTO> getNewestProducts(int limit) {
        return productRepository.findAll().stream()
                .sorted((p1, p2) -> Long.compare(p2.getMaSanPham(), p1.getMaSanPham()))
                .limit(limit)
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Page<ProductDTO> getBestSellingProducts(Pageable pageable) {
        return productRepository.findBestSellingProducts(pageable)
                .map(this::convertToDTO);
    }

    @Override
    public ProductDTO getProductById(long id) {
        Optional<Product> product = productRepository.findById(id);
        return product.map(this::convertToDTO).orElse(null);
    }

    @Override
    public List<ProductDTO> getProductsByCategory(int categoryId) {
        return productRepository.findByCategoryMaTheLoai(categoryId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductDTO> getProductsBySeller(long sellerId) {
        return productRepository.findByUserMaNguoiDung(sellerId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Page<ProductDTO> searchProducts(String keyword, Integer categoryId, Integer statusId, 
                                           Double minPrice, Double maxPrice, Pageable pageable) {
        return productRepository.searchProducts(keyword, categoryId, statusId, minPrice, maxPrice, pageable)
                .map(this::convertToDTO);
    }

    @Override
    @Transactional
    public void postProduct(ProductForSaleRequest request, String email) {
        // Tìm user đang đăng nhập
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new BusinessException(
                                HttpStatus.NOT_FOUND,
                                "Không tìm thấy người dùng"
                        )
                );

        // Tìm danh mục
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() ->
                        new BusinessException(
                                HttpStatus.NOT_FOUND,
                                "Danh mục không tồn tại"
                        )
                );

        // Tìm tình trạng
        TinhTrang tinhTrang = tinhTrangRepository.findById(request.getTinhTrangId())
                .orElseThrow(() ->
                        new BusinessException(
                                HttpStatus.NOT_FOUND,
                                "Tình trạng sản phẩm không tồn tại"
                        )
                );

        TrangThaiSanPham trangThai =
                trangThaiSanPhamRepository
                        .findByTenTrangThai("PENDING")
                        .orElseThrow(() ->
                                new BusinessException(
                                        HttpStatus.NOT_FOUND,
                                        "Không tìm thấy trạng thái PENDING"
                                )
                        );


        // Tạo product
        Product product = new Product();

        product.setTenSanPham(request.getTenSanPham());
        product.setSoLuong(request.getSoLuong());
        product.setGiaSanPham(request.getGiaBan());
        product.setMoTa(request.getMoTa());
        product.setMauSac(request.getMauSac());
        product.setKichCo(request.getKichThuoc());
        product.setThuongHieu(request.getThuongHieu());
        product.setActive(true);
        product.setSoLuongDaBan(0);

        // set quan hệ
        product.setUser(user);
        product.setCategory(category);
        product.setTinhTrang(tinhTrang);
        product.setTrangThaiSanPham(trangThai);

        // Lưu product trước
        Product savedProduct = productRepository.save(product);


        // Lưu danh sách ảnh
        List<HinhAnh> images = request.getImages()
                .stream()
                .map(imgRequest -> {

                    HinhAnh image = new HinhAnh();

                    image.setTenHinhAnh(imgRequest.getTenAnh());
                    image.setDuongDan(imgRequest.getDuongDan());

                    // gán product
                    image.setProduct(savedProduct);

                    return image;
                })
                .toList();

        hinhAnhRepository.saveAll(images);
    }

    @Override
    @Transactional
    public void approveProduct(Long id) {
        // Tìm sản phẩm
        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new BusinessException(
                                HttpStatus.NOT_FOUND,
                                "Không tìm thấy sản phẩm"
                        )
                );

        // Tìm trạng thái APPROVED
        TrangThaiSanPham approvedStatus =
                trangThaiSanPhamRepository
                        .findByTenTrangThai("APPROVED")
                        .orElseThrow(() ->
                                new BusinessException(
                                        HttpStatus.NOT_FOUND,
                                        "Không tìm thấy trạng thái APPROVED"
                                )
                        );

        // Cập nhật trạng thái
        product.setTrangThaiSanPham(approvedStatus);

        // Lưu lại
        productRepository.save(product);
    }

    @Override
    @Transactional
    public void rejectProduct(Long id, RejectProductRequest request) {

        // Tìm sản phẩm
        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new BusinessException(
                                HttpStatus.NOT_FOUND,
                                "Không tìm thấy sản phẩm"
                        )
                );

        // Tìm trạng thái REJECTED
        TrangThaiSanPham rejectedStatus =
                trangThaiSanPhamRepository
                        .findByTenTrangThai("REJECTED")
                        .orElseThrow(() ->
                                new BusinessException(
                                        HttpStatus.NOT_FOUND,
                                        "Không tìm thấy trạng thái REJECTED"
                                )
                        );

        // Set trạng thái bị từ chối
        product.setTrangThaiSanPham(rejectedStatus);

        User user  = product.getUser();

        // Save
        productRepository.save(product);

        emailService.guiEmailKichHoat(user.getEmail(),request.getLyDo() );
    }

    @Override
    public Page<ProductPendingDTO> getPendingProducts(Pageable pageable) {

        TrangThaiSanPham trangThaiSanPham = trangThaiSanPhamRepository.findByTenTrangThai("PENDING").orElseThrow(() ->new BusinessException( HttpStatus.NOT_FOUND,
                "Không tìm thấy trạng thái PENDING"));

        Page<Product> products =
                productRepository.findByTrangThaiSanPham(trangThaiSanPham, pageable);


        return products.map(productMapper::toPendingDTO);
    }

    @Override
    public Page<ProductSellerDTO> getProductsByUser(String email, Pageable pageable) {
        User user =  userRepository.findByEmail(email).orElseThrow(() -> new BusinessException("Không tim thấy người dùng"));
        Page<Product> products = productRepository.findByUser(user, pageable);
        return products.map(productSellerMapper::toDTO);
    }

    @Override
    public void activeProduct(Long id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new BusinessException("không tim thấy sản phẩm"));
        // Cập nhật trạng thái
        product.setActive(true);
        // Lưu lại
        productRepository.save(product);
    }

    @Override
    public void deactiveProduct(Long id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new BusinessException("không tim thấy sản phẩm"));
        // Cập nhật trạng thái
        product.setActive(false);
        // Lưu lại
        productRepository.save(product);
    }

    private ProductDTO convertToDTO(Product product) {
        ProductDTO dto = new ProductDTO();
        dto.setMaSanPham(product.getMaSanPham());
        dto.setTenSanPham(product.getTenSanPham());
        dto.setGiaSanPham(product.getGiaSanPham());
        dto.setSoLuong(product.getSoLuong());
        dto.setTenNguoiBan(product.getUser().getEmail());
        dto.setMaNguoiBan(product.getUser().getMaNguoiDung());
        dto.setEmail(product.getUser().getEmail());
        
        // Set category info
        if (product.getCategory() != null) {
            dto.setTenTheLoai(product.getCategory().getTenTheLoai());
            dto.setMaTheLoai(product.getCategory().getMaTheLoai());
        }
        
        // Set status info
        if (product.getTinhTrang() != null) {
            dto.setMaTinhTrang(product.getTinhTrang().getMaTinhTrang());
            dto.setTenTinhTrang(product.getTinhTrang().getTenTinhTrang());
        }

        // Map danh sách hình ảnh sản phẩm
        if (product.getHinhAnhs() != null && !product.getHinhAnhs().isEmpty()) {
            List<String> hinhAnhs = product.getHinhAnhs().stream()
                    .map(hinhAnh -> hinhAnh.getDuongDan())
                    .collect(Collectors.toList());
            dto.setHinhAnhs(hinhAnhs);
            
            // Lấy ảnh đầu tiên của SẢN PHẨM làm ảnh đại diện
            if (!hinhAnhs.isEmpty()) {
                dto.setHinhAnhDaiDien(hinhAnhs.get(0));
            }
        } else {
            dto.setHinhAnhs(Collections.emptyList());
            dto.setHinhAnhDaiDien(null);
        }

        return dto;
    }
}
