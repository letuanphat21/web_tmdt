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
    public List<ProductDTO> getNewestProducts(int limit, String excludeEmail) {
        // Optimize: Limit and filter at DB level instead of loading all products into memory
        return productRepository.findNewestProducts(excludeEmail, org.springframework.data.domain.PageRequest.of(0, limit))
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Page<ProductDTO> getBestSellingProducts(Pageable pageable, String excludeEmail) {
        return productRepository.findBestSellingProducts(excludeEmail, pageable)
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
    public void activeProduct(Long id, String email, boolean isAdmin) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new BusinessException(HttpStatus.NOT_FOUND, "Không tìm thấy người dùng")
                );

        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new BusinessException(HttpStatus.NOT_FOUND, "Không tìm thấy sản phẩm")
                );

        verifyCanManageProduct(product, user, isAdmin);

        product.setActive(true);
        productRepository.save(product);
    }

    @Override
    public void deactiveProduct(Long id, String email, boolean isAdmin) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new BusinessException(HttpStatus.NOT_FOUND, "Không tìm thấy người dùng")
                );

        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new BusinessException(HttpStatus.NOT_FOUND, "Không tìm thấy sản phẩm")
                );

        verifyCanManageProduct(product, user, isAdmin);

        product.setActive(false);
        productRepository.save(product);
    }

    @Override
    @Transactional
    public void updateProduct(Long productId, ProductUpdateRequest request, String email, boolean isAdmin) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new BusinessException(
                                HttpStatus.NOT_FOUND,
                                "Không tìm thấy người dùng"
                        )
                );

        Product product = productRepository.findById(productId)
                .orElseThrow(() ->
                        new BusinessException(
                                HttpStatus.NOT_FOUND,
                                "Không tìm thấy sản phẩm"
                        )
                );

        verifyCanManageProduct(product, user, isAdmin);

        if (request.getTenSanPham() != null) {
            if (request.getTenSanPham().isBlank()) {
                throw new BusinessException("Tên sản phẩm không được để trống");
            }
            product.setTenSanPham(request.getTenSanPham().trim());
        }

        if (request.getSoLuong() != null) {
            if (request.getSoLuong() < 1) {
                throw new BusinessException("Số lượng phải lớn hơn hoặc bằng 1");
            }
            if (request.getSoLuong() < product.getSoLuongDaBan()) {
                throw new BusinessException(
                        "Số lượng không được nhỏ hơn số lượng đã bán (" + product.getSoLuongDaBan() + ")"
                );
            }
            product.setSoLuong(request.getSoLuong());
        }

        if (request.getGiaBan() != null) {
            if (request.getGiaBan() <= 0) {
                throw new BusinessException("Giá bán phải lớn hơn 0");
            }
            product.setGiaSanPham(request.getGiaBan());
        }

        if (request.getMoTa() != null) {
            if (request.getMoTa().isBlank()) {
                throw new BusinessException("Mô tả không được để trống");
            }
            product.setMoTa(request.getMoTa());
        }

        if (request.getMauSac() != null) {
            if (request.getMauSac().isBlank()) {
                throw new BusinessException("Màu sắc không được để trống");
            }
            product.setMauSac(request.getMauSac());
        }

        if (request.getKichThuoc() != null) {
            if (request.getKichThuoc().isBlank()) {
                throw new BusinessException("Kích thước không được để trống");
            }
            product.setKichCo(request.getKichThuoc());
        }

        if (request.getThuongHieu() != null) {
            if (request.getThuongHieu().isBlank()) {
                throw new BusinessException("Thương hiệu không được để trống");
            }
            product.setThuongHieu(request.getThuongHieu());
        }

        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() ->
                            new BusinessException(
                                    HttpStatus.NOT_FOUND,
                                    "Danh mục không tồn tại"
                            )
                    );
            product.setCategory(category);
        }

        if (request.getTinhTrangId() != null) {
            TinhTrang tinhTrang = tinhTrangRepository.findById(request.getTinhTrangId())
                    .orElseThrow(() ->
                            new BusinessException(
                                    HttpStatus.NOT_FOUND,
                                    "Tình trạng sản phẩm không tồn tại"
                            )
                    );
            product.setTinhTrang(tinhTrang);
        }

        if (request.getDeleteImageIds() != null && !request.getDeleteImageIds().isEmpty()) {
            for (Integer imageId : request.getDeleteImageIds()) {
                HinhAnh image = hinhAnhRepository
                        .findByMaHinhAnhAndProduct_MaSanPham(imageId, productId)
                        .orElseThrow(() ->
                                new BusinessException(
                                        HttpStatus.NOT_FOUND,
                                        "Không tìm thấy ảnh với mã: " + imageId
                                )
                        );
                hinhAnhRepository.delete(image);
            }
        }

        if (request.getImages() != null && !request.getImages().isEmpty()) {
            for (ProductImageUpdateRequest imgRequest : request.getImages()) {
                if (imgRequest.getMaHinhAnh() != null && imgRequest.getMaHinhAnh() > 0) {
                    HinhAnh existing = hinhAnhRepository
                            .findByMaHinhAnhAndProduct_MaSanPham(imgRequest.getMaHinhAnh(), productId)
                            .orElseThrow(() ->
                                    new BusinessException(
                                            HttpStatus.NOT_FOUND,
                                            "Không tìm thấy ảnh với mã: " + imgRequest.getMaHinhAnh()
                                    )
                            );

                    if (imgRequest.getTenAnh() != null) {
                        if (imgRequest.getTenAnh().isBlank()) {
                            throw new BusinessException("Tên ảnh không được để trống");
                        }
                        existing.setTenHinhAnh(imgRequest.getTenAnh());
                    }
                    if (imgRequest.getDuongDan() != null) {
                        if (imgRequest.getDuongDan().isBlank()) {
                            throw new BusinessException("Đường dẫn ảnh không được để trống");
                        }
                        existing.setDuongDan(imgRequest.getDuongDan());
                    }
                    hinhAnhRepository.save(existing);
                } else {
                    if (imgRequest.getTenAnh() == null || imgRequest.getTenAnh().isBlank()
                            || imgRequest.getDuongDan() == null || imgRequest.getDuongDan().isBlank()) {
                        throw new BusinessException("Ảnh mới phải có tên và đường dẫn");
                    }

                    HinhAnh newImage = new HinhAnh();
                    newImage.setTenHinhAnh(imgRequest.getTenAnh());
                    newImage.setDuongDan(imgRequest.getDuongDan());
                    newImage.setProduct(product);
                    hinhAnhRepository.save(newImage);
                }
            }
        }

        long imageCount = hinhAnhRepository.countByProduct_MaSanPham(productId);
        if (imageCount == 0) {
            throw new BusinessException("Sản phẩm phải có ít nhất 1 hình ảnh");
        }
        if (imageCount > 3) {
            throw new BusinessException("Chỉ được tối đa 3 hình ảnh cho mỗi sản phẩm");
        }

        productRepository.save(product);
    }

    private void verifyCanManageProduct(Product product, User user, boolean isAdmin) {
        boolean isOwner = product.getUser().getMaNguoiDung()== user.getMaNguoiDung();
        if (!isAdmin && !isOwner) {
            throw new BusinessException(
                    HttpStatus.FORBIDDEN,
                    "Bạn không có quyền thực hiện thao tác này với sản phẩm"
            );
        }
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
