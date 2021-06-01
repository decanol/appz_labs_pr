package com.coursework.demo.entity;

import com.coursework.demo.entity.enums.TimeType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Table(name = "employees")
public class Employee implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 35, nullable = false)
    private String name;

    @Column(length = 35, nullable = false)
    private String surname;

    @Column(length = 35, nullable = false)
    private String patronymic;

    @Column(length = 35, nullable = false)
    private String positionName;

    @Enumerated(EnumType.STRING)
    private TimeType timeType;

    @Column(unique = true, length = 40)
    private String email;

    private Long salary;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "building_id")
    private Building building;
}
