package ru.practicum.ewmmain.events.storage;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.query.QueryUtils;
import ru.practicum.ewmmain.events.model.EventState;
import ru.practicum.ewmmain.events.model.Event;
import ru.practicum.ewmmain.events.params.EventAdminSearchParam;
import ru.practicum.ewmmain.events.params.EventPublicSearchParam;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

public class CustomizedEventStorageImpl implements CustomizedEventStorage {
    @PersistenceContext
    private EntityManager entityManager;
    private final Sort defaultSort = Sort.by(Sort.Direction.ASC, "id");

    @Override
    public List<Event> searchEventsForAdmin(EventAdminSearchParam param) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Event> query = criteriaBuilder.createQuery(Event.class);
        Root<Event> root = query.from(Event.class);
        Pageable pageable = param.getPageable();
        query.select(root).where(buildAdminPredicates(criteriaBuilder, root, param))
                .orderBy(QueryUtils.toOrders(pageable.getSortOr(defaultSort), root, criteriaBuilder));
        return entityManager.createQuery(query)
                .setFirstResult(pageable.getPageNumber())
                .setMaxResults(pageable.getPageSize())
                .getResultList();
    }

    @Override
    public List<Event> searchPublicEvents(EventPublicSearchParam param) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Event> query = criteriaBuilder.createQuery(Event.class);
        Root<Event> root = query.from(Event.class);
        query.select(root)
                .where(buildPublicPredicate(criteriaBuilder, root, param))
                .orderBy(QueryUtils.toOrders(defaultSort, root, criteriaBuilder));

        return entityManager.createQuery(query)
                .setFirstResult(param.getFrom())
                .setMaxResults(param.getSize())
                .getResultList();
    }

    private Predicate[] buildPublicPredicate(CriteriaBuilder cb,
                                             Root<Event> root,
                                             EventPublicSearchParam param) {
        List<Predicate> predicates = new ArrayList<>();
        var initiator = (Join<Object, Object>)root.fetch("initiator");
        var category = (Join<Object, Object>)root.fetch("category");

        // Только опубликованные event
        predicates.add(cb.equal(root.get("state"), EventState.PUBLISHED));

        // Поиск в соответствии с текстом в аннотации или описании
        Predicate annotationText = cb.like(cb.lower(root.get("annotation")),
                "%" + param.getText().toLowerCase() + "%");
        Predicate descriptionText = cb.like(cb.lower(root.get("description")),
                "%" + param.getText().toLowerCase() + "%");
        predicates.add(cb.or(annotationText, descriptionText));
        if (param.getCategoriesId() != null && !param.getCategoriesId().isEmpty()
                && !(param.getCategoriesId().size() == 1 && param.getCategoriesId().contains(0L))) {
            predicates.add(cb.in(root.get("category").get("id")).value(param.getCategoriesId()));
        }
        if (param.getPaid() != null) {
            predicates.add(cb.equal(root.get("paid"), param.getPaid()));
        }
        if (param.getStart() != null) {
            predicates.add(cb.greaterThan(root.get("eventDate"), param.getStart()));
        }
        if (param.getEnd() != null) {
            predicates.add(cb.lessThan(root.get("eventDate"), param.getEnd()));
        }
        if (param.isOnlyAvailable()) {
            predicates.add(cb.lt(root.get("confirmedRequests"), root.get("participantLimit")));
        }

        return predicates.toArray(new Predicate[0]);
    }

    private Predicate[] buildAdminPredicates(CriteriaBuilder criteriaBuilder,
                                             Root<Event> root,
                                             EventAdminSearchParam param) {
        List<Predicate> predicates = new ArrayList<>();
        var initiator = (Join<Object, Object>)root.fetch("initiator");
        var category = (Join<Object, Object>)root.fetch("category");

        if (param.getUsersId() != null && !param.getUsersId().isEmpty()
                && !(param.getUsersId().size() == 1 && param.getUsersId().contains(0L))) {
            predicates.add(criteriaBuilder.in(root.get("initiator").get("id")).value(param.getUsersId()));
        }
        if (param.getStates() != null && !param.getStates().isEmpty()) {
            predicates.add(criteriaBuilder.in(root.get("state")).value(param.getStates()));
        }
        if (param.getCategoriesId() != null && !param.getCategoriesId().isEmpty()
                && !(param.getCategoriesId().size() == 1 && param.getCategoriesId().contains(0L))) {
            predicates.add(criteriaBuilder.in(root.get("category").get("id")).value(param.getCategoriesId()));
        }
        if (param.getStart() != null) {
            predicates.add(criteriaBuilder.greaterThan(root.get("eventDate"), param.getStart()));
        }
        if (param.getEnd() != null) {
            predicates.add(criteriaBuilder.lessThan(root.get("eventDate"), param.getEnd()));
        }

        return predicates.toArray(new Predicate[0]);
    }
}
