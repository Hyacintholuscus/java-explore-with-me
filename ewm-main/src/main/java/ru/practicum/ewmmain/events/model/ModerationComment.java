package ru.practicum.ewmmain.events.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.Hibernate;
import ru.practicum.ewmmain.events.enums.CommentStatus;

import java.time.LocalDateTime;
import java.util.Objects;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Generated
@Entity
@Table(name = "moderation_comments")
public class ModerationComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String text;
    @Column(name = "event_id")
    private Long eventId;
    @Enumerated(EnumType.STRING)
    private CommentStatus status;
    private LocalDateTime created;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        ModerationComment comment = (ModerationComment) o;
        return Objects.equals(id, comment.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "ModerationComment{" +
                "id=" + id +
                ", text='" + text + '\'' +
                ", eventId=" + eventId +
                ", status=" + status +
                ", created=" + created +
                '}';
    }
}
