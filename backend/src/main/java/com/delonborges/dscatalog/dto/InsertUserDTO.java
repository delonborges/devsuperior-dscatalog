package com.delonborges.dscatalog.dto;

public class InsertUserDTO extends UserDTO {

    private static final long serialVersionUID = 1L;

    private String password;

    InsertUserDTO() {
        super();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
