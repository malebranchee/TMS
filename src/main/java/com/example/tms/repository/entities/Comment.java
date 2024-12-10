package com.example.tms.repository.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;


@Entity
@Getter
@Setter
@Table(name = "comments")
@SequenceGenerator(name = "comment_seq", sequenceName = "comment_id_seq", allocationSize = 1)
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "comment_seq")
    @Column(name = "id")
    Long id;

    @Getter
    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private User authorOfComment;

    @ManyToMany(mappedBy = "comments")
    private List<Task> tasks;

    @NotBlank
    @Getter
    @Setter
    @Column(name = "text")
    private String text;

    @Getter
    @Column(name = "date")
    private LocalDateTime date;

    protected Comment(){}

    public Comment(User authorOfComment, String text)
    {
        this.authorOfComment = authorOfComment;
        this.text = text;
        this.date = LocalDateTime.now();
    }

    @Override
    public String toString()
    {
        return String.format("%s at %s : %s\n", authorOfComment.getNickname(), date.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.SHORT)), getText());
    }
}
