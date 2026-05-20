package com.group2.web_tmdt.security;

public class Endpoints {
        public static final String front_end_host = "http://localhost:5173";

        public static final String[] PUBLIC_GET_ENDPOINTS = new String[] {
                        "/api/auth/kich-hoat",
                        "/api/home/**",
                        "/api/oauth2/**",
                        "/api/users/profile",
                        "/api/files/**",
                        "/api/products/search",
                        "/api/categories/**",
                        "/api/statuses/**"
        };

        public static final String[] PUBLIC_POST_ENDPOINTS = new String[] {
                        "/api/auth/dang-ky",
                        "/api/auth/dang-nhap",
                        "/api/auth/refresh-token",
                        "/api/auth/quen-mat-khau",
                        "/api/auth/xac-nhan-otp",
                        "/api/auth/dat-lai-mat-khau",


        };



        public static final String[] PRIVATE_GET_ENDPOINT = new String[] {
                        "/api/cart",
                        "/api/products/seller",
        };

        public static final String[] PRIVATE_POST_ENDPOINT = new String[] {
                        "/api/auth/dang-xuat",
                        "/api/cart/add",
                        "/api/products/post",
        };

        public static final String[] PRIVATE_PUT_ENDPOINT = new String[] {
                        "/api/user/profile",
                        "/api/cart/item/**",
                        "/api/products/*",
                        "/api/products/*/active",
                        "/api/products/*/deactive",
        };

        public static final String[] ADMIN_PUT_ENDPOINTS = new String[] {
                "/api/products/*/approve",
                "/api/products/*/reject",
        };

        public static final String[] ADMIN_GET_ENDPOINTS = new String[] {
                "/api/products/pending",
        };
}
