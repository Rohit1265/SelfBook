package dev.danvega.domain;

import lombok.Data;

import javax.persistence.*;
import java.util.Calendar;

@Data
@Entity
public class DataMapping {

    @Id
    @GeneratedValue( strategy = GenerationType.AUTO )
    private Long id;
    private String sourceTableName;
    private String sourceColumnName;
    private String targetTableName;
    private String targetColumnName;
    private String createUserId;

    @Temporal(TemporalType.TIMESTAMP)
    private Calendar createTs;

    @Temporal(TemporalType.TIMESTAMP)
    private Calendar updateTs;
}
