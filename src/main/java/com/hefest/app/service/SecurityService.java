package com.hefest.app.service;

import org.springframework.stereotype.Service;

import com.hefest.app.security.SecurityUtils;

@Service
public class SecurityService {

	 public String getCurrentLogin() {
	        return SecurityUtils.getCurrentLogin();
	    }

	 public static boolean isAuthenticated() {
        return SecurityUtils.isAuthenticated();
    }
}
