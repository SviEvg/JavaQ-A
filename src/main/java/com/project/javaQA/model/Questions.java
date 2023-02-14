package com.project.javaQA.model;


import lombok.Data;

;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Data
public class Questions {
    @Column(length = 2550000)
    private String question;
    @Column(length = 2550000)
    private String answer;
    private String category;
    @Id
    private Integer id;
}
