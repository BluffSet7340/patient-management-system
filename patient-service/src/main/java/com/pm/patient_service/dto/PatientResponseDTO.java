package com.pm.patient_service.dto;

// DTO is simply a Java object, same attributes as patient entity
// but the datatypes for the DTO is all Strings, it's harder to convert
// uuid and LocalDate types to json
// no registered date since that is for auditing purpose for our own purposes
public class PatientResponseDTO {
    private String id;
    private String name;
    private String email;
    // private String phone;
    private String address;
    private String dateofBirth;
    
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    // public String getPhone() {
    //     return phone;
    // }
    // public void setPhone(String phone) {
    //     this.phone = phone;
    // }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public String getDateofBirth() {
        return dateofBirth;
    }
    public void setDateofBirth(String dateofBirth) {
        this.dateofBirth = dateofBirth;
    }

}
