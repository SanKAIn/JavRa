package com.space.service;

import com.space.model.ShipClass;
import com.space.model.ShipType;
import com.space.repository.ShipRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

@Service
public class ShipServiceImpl implements ShipService {

    @Autowired
    ShipRepository shipRepository;

    @PersistenceContext
    private EntityManager em;

    @Override
    public ShipClass save(ShipClass entity, HttpServletResponse response) {
        String name = entity.getName();
        String planet = entity.getPlanet();
        ShipType shipType = entity.getShipType();
        Date prodDate = entity.getProdDate();
        if (entity.getUsed() == null) entity.setUsed(false);
        Double speed = entity.getSpeed();
        Integer crewSize =entity.getCrewSize();

        LocalDate parseDate = LocalDate.now();
        LocalDate addedDate = parseDate.plusYears(1000);
        LocalDate miusYar = parseDate.plusYears(781);
        Date today = Date.from(addedDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        if (name == null && planet == null && shipType == null && prodDate == null  && speed == null &&
                crewSize == null || name == "" || name.length() >= 50 || planet == "" || planet.length() >= 50 ||
                crewSize == null || prodDate == null || speed == null || prodDate.getTime() < 0 ||
                prodDate.getTime() > today.getTime() || speed < 0.01 || speed > 0.99 || crewSize < 1 || crewSize > 9999) {
            response.setStatus(400);
            return null;
        }
        entity.setRating();
        return shipRepository.saveAndFlush(entity);
    }

    @Override
    public ShipClass update(@RequestBody ShipClass entity, HttpServletResponse response) {
        if (entity.getId() < 1 || entity.getName() == ""){
            response.setStatus(400);
            return entity;
        }
        ShipClass newEnt = shipById(entity.getId(), response);
        if (newEnt == null){
            response.setStatus(404);
            return entity;
        }
        BeanUtils.copyProperties(entity,newEnt,getNullPropertyNames(entity));

        if (entity.getName() == null && entity.getPlanet() == null && entity.getShipType() == null &&
                entity.getProdDate() == null  && entity.getSpeed() == null && entity.getCrewSize() == null){
            response.setStatus(200);
            return newEnt;
        }
        LocalDate parseDate = LocalDate.now();
        LocalDate addedDate = parseDate.plusYears(1000);
        Date today = Date.from(addedDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        if (newEnt.getCrewSize() < 1 || newEnt.getCrewSize() > 9999 || newEnt.getProdDate().getTime() < 0 ||
                newEnt.getProdDate().getTime() > today.getTime()){
            response.setStatus(400);
            return entity;
        }
        newEnt.setRating();
        return shipRepository.save(newEnt);
    }

    @Override
    public void delete(Long id, HttpServletResponse response) {
        Optional<ShipClass> result = shipRepository.findById(id);
        if (result.isPresent()) {
            shipRepository.deleteById(id);
        }else {
            if (id < 1)  response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            else response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    public ShipClass shipById(Long id, HttpServletResponse response) {
        Optional<ShipClass> result = shipRepository.findById(id);
        if (result.isPresent()) {
            return result.get();
        }else {
            if (id < 1)  response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            else response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
        return null;
    }

    @Override
    public List<ShipClass> findAllByNativeQuery(String query ,Pageable pageable) {
        List<ShipClass> list = em.createQuery(query, ShipClass.class).getResultList();
        long start = pageable.getOffset();
        long end =(start + pageable.getPageSize()) > list .size() ? list .size() : (start + pageable.getPageSize());
        Page<ShipClass> pagedResult = new PageImpl<>(list.subList((int)start,(int)end), pageable, list.size());
        if(pagedResult.hasContent()) {
            return pagedResult.getContent();
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public List<ShipClass> findAll(Integer str, Integer kol, String sort) {
        String sor = "";
        sor = sort == null ? "id" : sort.equals("DATE") ? "prodDate" : sort.toLowerCase();
        Page<ShipClass> pagedResult = shipRepository.findAll(PageRequest.of(str == null ? 0 : str, kol == null ? 3 : kol, Sort.by(sor)));
        if(pagedResult.hasContent()) {
            return pagedResult.getContent();
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public Long count(String query) {
        List<ShipClass> list = em.createQuery(query, ShipClass.class).getResultList();
        return (long)list.size();
    }

    public List<ShipClass> findByName(String name, Integer str, Integer kol, String sort){
        String sor = "";
        sor = sort == null ? "id" : sort.equals("DATE") ? "prodDate" : sort.toLowerCase();
        Page<ShipClass> pagedResult = shipRepository.findByNameLike("%" + name + "%", PageRequest.of(str == null ? 0 :
                str, kol == null ? 3 : kol, Sort.by(sor)));
        if(pagedResult.hasContent()) {
            return pagedResult.getContent();
        } else {
            return new ArrayList<ShipClass>();
        }
    }

    public static String[] getNullPropertyNames (Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet<>();
        for(java.beans.PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null) emptyNames.add(pd.getName());
        }
        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }

}
