package com.group2.web_tmdt.security;

public class Endpoints {
        public static final String front_end_host = "http://localhost:5173";

        public static final String[] PUBLIC_GET_ENDPOINTS = new String[] {
                        "/api/auth/kich-hoat",
        };

        public static final String[] PUBLIC_POST_ENDPOINTS = new String[] {
                        "/api/auth/dang-ky",
                        "/api/auth/dang-nhap",
                        "/api/auth/refresh-token",
                        "/api/auth/quen-mat-khau",
                        "/api/auth/xac-nhan-otp",
                        "/api/auth/dat-lai-mat-khau"
        };

        public static final String[] PRIVATE_GET_ENDPOINT = new String[] {

        };

        public static final String[] PRIVATE_POST_ENDPOINT = new String[] {
                        "/api/auth/dang-xuat"
        };
}
