package com.bank.banksystem.entity;

import jakarta.persistence.*;

@Entity
@Table (name = "service")
public class Service {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "service_id")
    private Long service_id;

    @Column(name = "service_name")
    private String service_name;

    @Column(name = "service_type")
    private String service_type;

    @Column(name = "service_description")
    private String service_description;

    public Service(Long service_id, String service_name, String service_type, String service_description) {
        this.service_id = service_id;
        this.service_name = service_name;
        this.service_type = service_type;
        this.service_description = service_description;
    }

    public Service() {

    }

    public Long getService_id() {
        return service_id;
    }

    public void setService_id(Long service_id) {
        this.service_id = service_id;
    }

    public String getService_name() {
        return service_name;
    }

    public void setService_name(String service_name) {
        this.service_name = service_name;
    }

    public String getService_type() {
        return service_type;
    }

    public void setService_type(String service_type) {
        this.service_type = service_type;
    }

    public String getService_description() {
        return service_description;
    }

    public void setService_description(String service_description) {
        this.service_description = service_description;
    }
}
