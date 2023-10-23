package com.sencorsta.ids.api.request;

import lombok.Data;

/**
 * @author ICe
 */
@Data
public class LoginRequest {
    String account;
    String password;
}
