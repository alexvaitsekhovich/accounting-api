package com.alexvait.accountingapi.accounting.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(PositionController.BASE_URL)
@CrossOrigin(origins = "*")
public class PositionController {

    public static final String BASE_URL = "/position";

    @GetMapping
    public void getFreePositions() {
        return;
    }

    @PostMapping
    public void addPosition() {
        return;
    }

    @PatchMapping("/{positionId}")
    public void updatePosition() {
        return;
    }
}
