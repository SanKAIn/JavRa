package com.space.model;

import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

@Table(name = "ship")
@Entity(name = "ship")
@DynamicUpdate()
public class ShipClass implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name")
    private String name; // Название корабля (до 50 знаков включительно)
    @Column(name = "planet")
    private String planet; // Планета пребывания (до 50 знаков включительно)
    @Column(name = "shipType")
    @Enumerated(EnumType.STRING)
    private ShipType shipType; // Тип корабля
    @Column(name = "prodDate")
    private Date prodDate; // Дата выпуска. Диапазон значений года 2800..3019 включительно
    @Column(name = "isUsed")
    private Boolean isUsed; // Использованный / новый
    @Column(name = "speed")
    private Double speed; // Максимальная скорость корабля. Диапазон значений 0,01..0,99 включительно. Используй математическое округление до сотых.
    @Column(name = "crewSize")
    private Integer crewSize; // Количество членов экипажа. Диапазон значений 1..9999 включительно.
    @Column(name = "rating")
    private Double rating; // Рейтинг корабля. Используй математическое округление до сотых.


    public ShipClass() {
    }

    public ShipClass(Long id, String name, String planet, ShipType shipType, Date prodDate, Boolean isUsed, Double speed, Integer crewSize) {
        this.id = id;
        this.name = name;
        this.planet = planet;
        this.shipType = shipType;
        this.prodDate = prodDate;
        this.isUsed = isUsed;
        this.speed = speed;
        this.crewSize = crewSize;
        setRating();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlanet() {
        return planet;
    }

    public void setPlanet(String planet) {
        this.planet = planet;
    }

    public ShipType getShipType() {
        return shipType;
    }

    public void setShipType(ShipType shipType) {
        this.shipType = shipType;
    }

    public Date getProdDate() {
        return prodDate;
    }

    public void setProdDate(Date prodDate) {
        this.prodDate = prodDate;
    }

    public Boolean getUsed() {
        return isUsed;
    }

    public void setUsed(Boolean used) {
        isUsed = used;
    }

    public Double getSpeed() {
        return speed;
    }

    public void setSpeed(Double speed) {
        this.speed = speed;
    }

    public Integer getCrewSize() {
        return crewSize;
    }

    public void setCrewSize(Integer crewSize) {
        this.crewSize = crewSize;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(){
        LocalDate parseDate = LocalDate.now();
        LocalDate addedDate = parseDate.plusYears(1000);
        SimpleDateFormat sd = new SimpleDateFormat("yyyy");
        Date today = Date.from(addedDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        double chisl = 80 * speed * (isUsed ? 0.5 : 1);
        //Double v = today.getYear()-1 - prodDate.getYear() + 1.0;
        double v = Integer.parseInt(sd.format(today)) - 1 - Integer.parseInt(sd.format(prodDate)) + 1.0;

        this.rating = new BigDecimal((chisl)/(v)).setScale(2, RoundingMode.HALF_EVEN).doubleValue();
    }

}
