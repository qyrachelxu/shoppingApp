package com.example.ApplicationService.domain;

import lombok.*;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "WorkFlow")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class WorkFlow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "workflow_id")
    private Integer workflowId;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "workflow_datecreated")
    private Timestamp workflowDateCreated;

    @Column(name = "workflow_datemodified")
    private Timestamp workflowDateModified;

    @Column(name = "workflow_status")
    private String workflowStatus;

    @Column(name = "workflow_comments")
    private String workflowComment;

}
