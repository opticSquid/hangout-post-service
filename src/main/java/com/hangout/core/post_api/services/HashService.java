package com.hangout.core.post_api.services;

import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class HashService {
    private static final String ALGORITHM = "SHA3-512";

    public String checksum(MultipartFile file) throws IOException {
        byte[] data = file.getBytes();
        try {
            byte[] hash = MessageDigest.getInstance(ALGORITHM).digest(data);
            return new BigInteger(1, hash).toString(16);
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalArgumentException(ex);
        }
    }
}
