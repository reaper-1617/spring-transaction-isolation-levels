package com.gerasimchuk.transactionisolationlevels.domain;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "author")
    private String author;

    @Column(name = "content")
    private String content;

}
