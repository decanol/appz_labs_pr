package com.coursework.demo.service;

import org.springframework.data.domain.Pageable;

import java.io.Serializable;
import java.util.List;

interface BasicService<T extends Serializable, I extends Serializable> {

    T getById(I id);

    List<T> getAll(Pageable pageable);

    T save(T object);

    T delete(T object);
}
