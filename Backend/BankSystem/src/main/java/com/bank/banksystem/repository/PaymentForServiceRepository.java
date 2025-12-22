package com.bank.banksystem.repository;

import com.bank.banksystem.entity.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentForServiceRepository extends JpaRepository<Service, Long> {

    // Find service by type
    @Query(
            value = "SELECT * FROM service WHERE service_type = :type",
            nativeQuery = true
    )
    List<Service> findByServiceType(@Param("type") String type);

    // Find service by name
    @Query(
            value = "SELECT * FROM service WHERE service_name = :name",
            nativeQuery = true
    )
    Optional<Service> findByServiceName(@Param("name") String name);

    // Find all active services
    @Query(
            value = "SELECT * FROM service ORDER BY service_type, service_name",
            nativeQuery = true
    )
    List<Service> findAllServices();

    // Search services by name
    @Query(
            value = "SELECT * FROM service WHERE service_name ILIKE %:searchTerm%",
            nativeQuery = true
    )
    List<Service> searchServices(@Param("searchTerm") String searchTerm);
}