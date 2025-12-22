package com.bank.banksystem.controller;

import com.bank.banksystem.dto.request.CheckPasswordRequest;
import com.bank.banksystem.dto.request.UpdatePasswordRequest;
import com.bank.banksystem.dto.request.UpdateUserRequest;
import com.bank.banksystem.dto.response.UserResponse;
import com.bank.banksystem.service.ChangeUserInformationService;
import com.bank.banksystem.service.UserPasswordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/dashboard/user-settings")
public class UserController {

    @Autowired
    private ChangeUserInformationService changeUserInformationService;
    @Autowired
    private UserPasswordService userPasswordService;

    @PutMapping("/change")
    public UserResponse changeUserInformation(@RequestBody UpdateUserRequest request) {
        return changeUserInformationService.changeUserInformation(request);
    }

    @PostMapping("/check-password")
    public ResponseEntity<Boolean> checkPassword(
            @RequestBody CheckPasswordRequest request
    ) {
        boolean result = userPasswordService.checkCurrentPassword(
                request.getUserId(),
                request.getCurrentPassword()
        );
        return ResponseEntity.ok(result);
    }

    /**
     * STEP 2: Update password
     */
    @PostMapping("/update-password")
    public ResponseEntity<String> updatePassword(
            @RequestBody UpdatePasswordRequest request
    ) {
        userPasswordService.updatePassword(
                request.getUserId(),
                request.getNewPassword()
        );
        return ResponseEntity.ok("Password updated successfully");
    }
}

