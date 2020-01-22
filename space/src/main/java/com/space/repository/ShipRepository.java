package com.space.repository;

import com.space.model.ShipClass;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ShipRepository extends JpaRepository<ShipClass, Long>
{
    Page<ShipClass> findByNameLike(String name, Pageable page);

    @Query(value = "SELECT * FROM ship",  nativeQuery = true)
    List<ShipClass> findAll1(@Param("quest") String req);
}