package org.codi.catan.model.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {

    private String id;
    private String pwd;
}
