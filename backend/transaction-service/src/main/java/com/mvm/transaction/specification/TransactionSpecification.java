package com.mvm.transaction.specification;

import com.mvm.transaction.model.Transaction;
import com.mvm.transaction.model.TransactionType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public final class TransactionSpecification {

    private TransactionSpecification() {}

    public static Specification<Transaction> byUserId(Long userId) {
        return (root, query, cb) -> cb.equal(root.get("userId"), userId);
    }

    public static Specification<Transaction> byType(TransactionType type) {
        return (root, query, cb) -> cb.equal(root.get("type"), type);
    }

    public static Specification<Transaction> byDescription(String description) {
        return (root, query, cb) -> {
            if (description == null || description.isBlank()) {
                return cb.conjunction();
            }
            return cb.like(cb.lower(root.get("description")), "%" + description.toLowerCase() + "%");
        };
    }

    public static Specification<Transaction> byCategory(String category) {
        return (root, query, cb) -> {
            if (category == null || category.isBlank()) {
                return cb.conjunction();
            }
            return cb.equal(root.get("category"), category);
        };
    }

    public static Specification<Transaction> byDateRange(LocalDate startDate, LocalDate endDate) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (startDate != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("date"), startDate));
            }
            if (endDate != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("date"), endDate));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<Transaction> byAmountRange(BigDecimal minAmount, BigDecimal maxAmount) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (minAmount != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("amount"), minAmount));
            }
            if (maxAmount != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("amount"), maxAmount));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<Transaction> byApplicated(Boolean applicated) {
        return (root, query, cb) -> {
            if (applicated == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get("applicated"), applicated);
        };
    }

    public static Specification<Transaction> combine(Specification<Transaction>... specs) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            for (Specification<Transaction> spec : specs) {
                if (spec != null) {
                    predicates.add(spec.toPredicate(root, query, cb));
                }
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}