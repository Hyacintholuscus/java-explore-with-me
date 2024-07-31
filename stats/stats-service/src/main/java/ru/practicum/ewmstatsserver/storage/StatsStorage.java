package ru.practicum.ewmstatsserver.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewmstatsserver.model.StatsModel;
import ru.practicum.ewmstatsserver.view.StatsView;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface StatsStorage extends JpaRepository<StatsModel, Long> {
    // Статистика по эндпоинтам без уникальных ip
    @Query("select s.app as app, s.uri as uri, count(s.ip) as hits " +
            "from StatsModel s " +
            "where uri in (:uris) " +
            "and s.timestamp >= :start " +
            "and s.timestamp <= :end " +
            "group by app, uri " +
            "order by hits desc")
    List<StatsView> getStats(@Param("start") LocalDateTime start,
                             @Param("end") LocalDateTime end,
                             @Param("uris") Collection<String> uris);

    // Статистика по эндпоинтам с уникальными ip
    @Query("select s.app as app, s.uri as uri, count(distinct s.ip) as hits " +
            "from StatsModel s " +
            "where uri in :uris " +
            "and s.timestamp >= :start " +
            "and s.timestamp <= :end " +
            "group by app, uri " +
            "order by hits desc")
    List<StatsView> getStatsWithUnique(@Param("start") LocalDateTime start,
                                       @Param("end") LocalDateTime end,
                                       @Param("uris") Collection<String> uris);

    // Вся статистика без уникальных ip
    @Query("select s.app as app, s.uri as uri, count(s.ip) as hits " +
            "from StatsModel s " +
            "where s.timestamp >= :start " +
            "and s.timestamp <= :end " +
            "group by app, uri " +
            "order by hits desc")
    List<StatsView> getStatsWithoutUris(@Param("start") LocalDateTime start,
                                        @Param("end") LocalDateTime end);

    // Вся статистика с уникальными ip
    @Query("select s.app as app, s.uri as uri, count(distinct s.ip) as hits " +
            "from StatsModel s " +
            "where s.timestamp >= :start " +
            "and s.timestamp <= :end " +
            "group by app, uri " +
            "order by hits desc")
    List<StatsView> getStatsWithoutUrisUnique(@Param("start") LocalDateTime start,
                                        @Param("end") LocalDateTime end);
}
