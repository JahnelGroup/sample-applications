package com.jahnelgroup.jgbay.search.rsql

import lombok.Data

import javax.persistence.*
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
@Table(name="rsql_test_entity")
@Data
class RSQLTestEntity {

    @Id
    public Long id
    
    @Column(name="integer_val")
    public Integer intVal
    
    @Column(name="str_val")
    public String strVal
    
    @Column(name="bool_val")
    public Boolean boolVal

    @Column(name="date_val")
    public LocalDate dateVal
    
    @Column(name="timest_val")
    public LocalDateTime timestVal
    
    @Column(name="double_val")
    public BigDecimal doubleVal

    @Column(name="enum_val")
    @Enumerated(EnumType.STRING)
    public Lorem enumVal
    
}
