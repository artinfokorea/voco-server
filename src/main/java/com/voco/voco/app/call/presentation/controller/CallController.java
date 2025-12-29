package com.voco.voco.app.call.presentation.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Call", description = "통화 내역 API")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/v1/calls")
@RequiredArgsConstructor
public class CallController {

}
