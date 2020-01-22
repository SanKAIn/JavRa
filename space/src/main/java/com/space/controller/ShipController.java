package com.space.controller;

import com.space.model.ShipClass;
import com.space.model.ShipType;
import com.space.service.ShipServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Stream;

//mvn -DskipTests=true clean install
//mysqld -initialize-insecure -user=root
//mysqld --console
//@Controller
@RestController
public class ShipController {

    @Autowired
    private ShipServiceImpl shipService;

    private int sHipCount = 0;

    @RequestMapping(path = "rest/ships/{id}")
    @ResponseBody
    public ShipClass findById(@PathVariable("id") Long id, HttpServletResponse response){
        return shipService.shipById(id,response);
    }

    @RequestMapping(path = "/rest/ships", method = RequestMethod.GET)
    @ResponseBody
    public List<ShipClass> getShips(@RequestParam(required = false, name = "name") String name,
                                    @RequestParam(required = false, name = "planet") String planet,
                                    @RequestParam(required = false, name = "shipType") ShipType shipType,
                                    @RequestParam(required = false, name = "after") Long after,
                                    @RequestParam(required = false, name = "before") Long before,
                                    @RequestParam(required = false, name = "isUsed") Boolean isUsed,
                                    @RequestParam(required = false, name = "minSpeed") Double minSpeed,
                                    @RequestParam(required = false, name = "maxSpeed") Double maxSpeed,
                                    @RequestParam(required = false, name = "minCrewSize") Integer minCrewSize,
                                    @RequestParam(required = false, name = "maxCrewSize") Integer maxCrewSize,
                                    @RequestParam(required = false, name = "minRating") Double minRating,
                                    @RequestParam(required = false, name = "maxRating") Double maxRating,
                                    @RequestParam(required = false, name = "pageNumber") Integer pageNumber,
                                    @RequestParam(required = false, name = "pageSize") Integer pageSize,
                                    @RequestParam(required = false, name = "order") String orders){
        String sor = orders == null ? "id" : orders.equals("DATE") ? "prodDate" : orders.toLowerCase();
        String zaps = buildReq(name, planet, shipType, after, before, isUsed, minSpeed, maxSpeed, minCrewSize, maxCrewSize, minRating, maxRating, sor);
        List<ShipClass> otv = shipService.findAllByNativeQuery(zaps, PageRequest.of(pageNumber == null ? 0 : pageNumber, pageSize == null ? 3 : pageSize, Sort.by(sor)));
        sHipCount = otv.size();
        return otv;
    }

    @RequestMapping(path = "/rest/ships/count", method = RequestMethod.GET)
    public Long getCount(@RequestParam(required = false, name = "name") String name,
                         @RequestParam(required = false, name = "planet") String planet,
                         @RequestParam(required = false, name = "shipType") ShipType shipType,
                         @RequestParam(required = false, name = "after") Long after,
                         @RequestParam(required = false, name = "before") Long before,
                         @RequestParam(required = false, name = "isUsed") Boolean isUsed,
                         @RequestParam(required = false, name = "minSpeed") Double minSpeed,
                         @RequestParam(required = false, name = "maxSpeed") Double maxSpeed,
                         @RequestParam(required = false, name = "minCrewSize") Integer minCrewSize,
                         @RequestParam(required = false, name = "maxCrewSize") Integer maxCrewSize,
                         @RequestParam(required = false, name = "minRating") Double minRating,
                         @RequestParam(required = false, name = "maxRating") Double maxRating,
                         @RequestParam(required = false, name = "order") String orders){
        String sor = orders == null ? "id" : orders.equals("DATE") ? "prodDate" : orders.toLowerCase();
        String zapros = buildReq(name, planet, shipType, after, before, isUsed, minSpeed,
                maxSpeed, minCrewSize, maxCrewSize, minRating, maxRating, sor);
        return shipService.count(zapros);
    }

    @RequestMapping(path = "/rest/ships/{id}", method = RequestMethod.POST)
    public ShipClass updateShip(@PathVariable("id") Long id,  @RequestBody ShipClass shipClass, HttpServletResponse response){
        shipClass.setId(id);
        return shipService.update(shipClass, response);
    }

    @RequestMapping(path = "/rest/ships/{id}", method = RequestMethod.DELETE)
    public void deleteShip(@PathVariable("id") Long id, HttpServletResponse response){
        shipService.delete(id, response);
    }

    @RequestMapping(path = "/rest/ships", method = RequestMethod.POST)
    public ShipClass createShip(@RequestBody ShipClass shipClass, HttpServletResponse response, HttpServletRequest request) {
        return shipService.save(shipClass, response);
    }

    private String buildReq(String name,String planet,ShipType shipType, Long after, Long before,Boolean isUsed, Double minSpeed,Double maxSpeed,
                            Integer minSize, Integer maxSize,Double minRating, Double maxRating,String order){
        String sql = "SELECT s FROM ship s WHERE ";
        String dateFormat = "yyyy-MM-dd";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
        if (name != null) sql += ("s.name LIKE '%" + name + "%' AND ");
        if (planet != null) sql += ("s.planet LIKE '%" + planet + "%' AND ");
        if (shipType != null) sql += ("s.shipType = '" + shipType + "' AND ");
        if (after != null) sql += ("s.prodDate >= '" + simpleDateFormat.format(new Date(after)) + "' AND ");
        if (before != null) sql += ("s.prodDate < '" + simpleDateFormat.format(new Date(before)) + "' AND ");
        if (isUsed != null) sql += ("s.isUsed = " + isUsed + " AND ");
        if (minSpeed != null) sql += ("s.speed >= " + minSpeed + " AND ");
        if (maxSpeed != null) sql += ("s.speed <= " + maxSpeed + " AND ");
        if (minSize != null) sql += ("s.crewSize >= " + minSize + " AND ");
        if (maxSize != null) sql += ("s.crewSize <= " + maxSize + " AND ");
        if (minRating != null) sql += ("s.rating >= " + minRating + " AND ");
        if (maxRating != null) sql += ("s.rating <= " + maxRating);

        String otv = sql;
        if (otv.endsWith(" WHERE ")) {
            otv = "SELECT s FROM ship s ORDER BY s." + order + " ASC";
        }
        if (otv.endsWith(" AND ")) {
            otv = otv.substring(0, otv.length() - 5) + " ORDER BY s." + order + " ASC";
        }
        return otv;
    }
}