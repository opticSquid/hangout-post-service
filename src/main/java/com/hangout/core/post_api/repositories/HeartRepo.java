package com.hangout.core.post_api.repositories;

import java.math.BigInteger;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hangout.core.post_api.entities.Heart;

public interface HeartRepo extends JpaRepository<Heart, BigInteger> {

}
