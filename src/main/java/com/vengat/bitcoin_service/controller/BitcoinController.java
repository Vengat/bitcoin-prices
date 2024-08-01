package com.vengat.bitcoin_service.controller;

import org.springframework.web.bind.annotation.RestController;

import com.vengat.bitcoin_service.api.BitcoinService;

import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("bitcoin")
public class BitcoinController {

    @Autowired
    private BitcoinService bitcoinService;

    public ResponseEntity<String> getBitcoinPrice() {
        return ResponseEntity.ok("Bitcoin price is $1000");
    }

}
