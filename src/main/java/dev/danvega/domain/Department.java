package dev.danvega.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Getter
@Setter
@Entity
public class Department {

    @Id
    @GeneratedValue( strategy = GenerationType.AUTO )
    private Long id;
    private String name;
    private String number;
    private String errorMessage;
}
