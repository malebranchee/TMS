package com.example.tms.repository.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;


@Entity
@Data
@Table(name = "comments")
@SequenceGenerator(name = "comment_seq", sequenceName = "comment_id_seq", allocationSize = 1)
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "comment_seq")
    @Column(name = "id")
    Long id;

    @Getter
    @ManyToOne
    @JoinTable(
            name = "comments_x_authors",
            joinColumns = @JoinColumn(name = "comment_id"),
            inverseJoinColumns = @JoinColumn(name = "author_id")
    )
    private User authorOfComment;

    @ManyToOne
    private Task task;

    @NotBlank
    @Getter
    @Setter
    @Column(name = "text")
    private String text;

    @Getter
    @Column(name = "date")
    private LocalDate date;

    protected Comment(){}

    public Comment(User authorOfComment, String text)
    {
        this.authorOfComment = authorOfComment;
        this.text = text;
        this.date = LocalDate.now();
    }

    @Override
    public String toString()
    {
        return String.format("%s at %s : %s", authorOfComment.getNickname(), getDate().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.SHORT)), getText());
    }
}
