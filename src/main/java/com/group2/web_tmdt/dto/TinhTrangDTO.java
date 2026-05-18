package com.group2.web_tmdt.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TinhTrangDTO {

    private int maTinhTrang;

    private String tenTinhTrang;

    private String moTa;
}
