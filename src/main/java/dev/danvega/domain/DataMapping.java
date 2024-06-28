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



    Staging Schema: T_BIBR_BRX (Source Table)------>Dynamic
        Normalization Schema: Introducting_Broker_Dealer_Branch ( Target Table)

        Source Data: Bibr----->Dynamic
        Target Data: introductionBrokerDealerBranch

        DataMapping : List

        1.	Get data from DataMapping By source_table and target_table
        2.	Get sourceCloumnName and targetCloumnName
        3.	If both column names are null then return
        4.	Either set the value