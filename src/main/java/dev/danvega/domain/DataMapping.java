package dev.danvega.domain;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Calendar;


@Setter
@Getter
@Entity
public class DataMapping {

    @Id
    @GeneratedValue( strategy = GenerationType.AUTO )
    private Long id;
    private String sourceTableName;
    private String sourceColumnName;
    private String targetTableName;
    private String targetColumnName;
}
