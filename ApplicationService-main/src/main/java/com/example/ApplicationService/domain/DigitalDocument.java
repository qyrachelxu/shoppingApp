package com.example.ApplicationService.domain;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "DigitalDocument")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class DigitalDocument {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "document_id")
    private Integer documentId;

    @Column(name = "document_type")
    private String documentType;

    @Column(name = "document_isrequired")
    private Boolean documentIsRequired;

    @Column(name = "document_path")
    private String documentPath;

    @Column(name = "document_description")
    private String documentDescription;

    @Column(name = "document_title")
    private String documentTitle;
}
