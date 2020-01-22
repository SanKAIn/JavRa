package com.space.service;

import com.space.model.ShipClass;

import javax.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface ShipService {

    ShipClass save(ShipClass entity, HttpServletResponse response);

    ShipClass update(ShipClass entity, HttpServletResponse response);

    void delete(Long id, HttpServletResponse response);

    ShipClass shipById(Long id, HttpServletResponse response);

    List<ShipClass> findAllByNativeQuery(String query, Pageable pageable);

    List<ShipClass> findAll(Integer str, Integer kol, String sort);

    Long count(String query);
}