package com.bank.banksystem.repository;

import com.bank.banksystem.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    //Login checker
    @Query(
            value = "SELECT * FROM user_information WHERE user_email = :email and user_password = :password",
            nativeQuery = true
    )
    User login(@Param("email") String email, @Param("password") String password);

    //Registration
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(
            value = """
                INSERT INTO user_information 
                (user_email, user_password, user_name, user_surname, user_birthday, user_salary, user_id_card_no_series, user_id_card_no, user_fin, user_phone_number, user_codeword)
                VALUES (:email, :password, :name, :surname, :birthday, :salary, :id_card_no_series, :id_card_no, :fin, :phone_number, :codeword)
                """,
            nativeQuery = true
    )
    void registerUser(
            @Param("email") String email,
            @Param("password") String password,
            @Param("name") String name,
            @Param("surname") String surname,
            @Param("birthday") LocalDate birthday,
            @Param("salary") Long salary,
            @Param("id_card_no_series")  String id_card_no_series,
            @Param("id_card_no")   String id_card_no,
            @Param("fin") String  fin,
            @Param("phone_number") String phone_number,
            @Param("codeword") String codeword
    );

    // Find user by email
    @Query(
            value = "SELECT * FROM user_information WHERE user_email = :email",
            nativeQuery = true
    )
    Optional<User> findByEmail(@Param("email") String email);

    // Check if email exists
    @Query(
            value = "SELECT COUNT(*) > 0 FROM user_information WHERE user_email = :email",
            nativeQuery = true
    )
    boolean existsByEmail(@Param("email") String email);

    // Update user profile
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(
            value = """
            UPDATE user_information 
            SET user_name = :name, user_surname = :surname, user_salary = :salary, user_birthday = :birthday, user_phone_number = :phone_number
            WHERE user_id = :userId
            """,
            nativeQuery = true
    )
    void updateProfile(
            @Param("userId") Long userId,
            @Param("name") String name,
            @Param("surname") String surname,
            @Param("salary") Long salary,
            @Param("birthday") LocalDate birthday,
            @Param("phone_number") String phone_number
    );

    // Change password
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(
            value = "UPDATE user_information SET user_password = :password WHERE user_id = :userId",
            nativeQuery = true
    )
    void changePassword(@Param("userId") Long userId, @Param("password") String password);

    @Query(
            value = "SELECT user_codeword FROM user_information WHERE user_id = :userId",
            nativeQuery = true
    )
    String getCodewordByUserId(@Param("userId") Long userId);

    // Check ID card number
    @Query(
            value = "SELECT COUNT(*) > 0 FROM user_information WHERE user_id_card_no = :idCardNo",
            nativeQuery = true
    )
    boolean existsByIdCardNo(@Param("idCardNo") String idCardNo);

    // Check FIN
    @Query(
            value = "SELECT COUNT(*) > 0 FROM user_information WHERE user_fin = :fin",
            nativeQuery = true
    )
    boolean existsByFin(@Param("fin") String fin);

    // Check phone number
    @Query(
            value = "SELECT COUNT(*) > 0 FROM user_information WHERE user_phone_number = :phone",
            nativeQuery = true
    )
    boolean existsByPhoneNumber(@Param("phone") String phone);

    @Query(
            value = "SELECT user_email FROM user_information WHERE user_id = :user_id",
            nativeQuery = true
    )
    String findEmailbyId(@Param("user_id") Long user_id);
}