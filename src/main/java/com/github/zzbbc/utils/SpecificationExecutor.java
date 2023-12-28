package com.gpay.merchant.report.merchantreport.repository;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.util.ObjectUtils;
import com.gpay.merchant.report.configs.repository.Compare;
import com.gpay.merchant.report.configs.repository.ConvertFunctionEnum;
import com.gpay.merchant.report.configs.repository.Operator;
import com.gpay.merchant.report.constant.RestrictionType;
import com.gpay.merchant.report.handler.ConvertFunctionHandler;
import com.gpay.merchant.report.utils.CriteriaUtils;

@SuppressWarnings(value = {"rawtypes", "unchecked"})
public interface SpecificationExecutor<T> extends JpaSpecificationExecutor<T> {
    default <K> Predicate buildPredicate(CriteriaBuilder cb, RestrictionType operator,
            Path<K> attribute, K searchValue) {
        switch (operator) {
            case EQ:
                return cb.equal(attribute, searchValue);
            case NE:
                return cb.notEqual(attribute, searchValue);
            case GE:
                return cb.greaterThanOrEqualTo((Expression) attribute, (Comparable) searchValue);
            case GT:
                return cb.greaterThanOrEqualTo((Expression) attribute, (Comparable) searchValue);
            case LE:
                return cb.lessThanOrEqualTo((Expression) attribute, (Comparable) searchValue);
            case LT:
                return cb.lessThan((Expression) attribute, (Comparable) searchValue);
            case LIKE:
                return cb.like(cb.lower((Expression) attribute),
                        "%" + searchValue.toString().toLowerCase() + "%");
            case ILIKE:
                return cb.notLike(cb.lower((Expression) attribute),
                        "%" + searchValue.toString().toLowerCase() + "%");
            case IN:
                Expression predicate = (Expression) attribute;
                predicate.in(searchValue);
                return cb.in(predicate).value(searchValue);
            default: {
                return cb.equal(attribute, searchValue);
            }
        }
    }

    default <K> Page<T> searchPageDto(K search, Pageable pageable) {
        return findAll(this.fetchDto(search), pageable);
    }

    default <K> List<T> searchDto(K search, Sort sort) {
        return findAll(this.fetchDto(search), sort);
    }

    default <K> List<T> searchDto(K search) {
        return findAll(this.fetchDto(search));
    }

    default <K> Specification<T> fetchDto(K search) {
        return (Root<T> root, CriteriaQuery<?> cq, CriteriaBuilder cb) -> {
            cq.distinct(true);

            List<Predicate> predicates = new ArrayList<>();
            if (search != null) {
                Class<?> clazz = search.getClass();
                Class<?> superClazz = clazz.getSuperclass();
                Field[] fields = clazz.getDeclaredFields();
                if (superClazz != null && !superClazz.getName().equals("java.lang.Object")) {
                    Field[] fields1 = superClazz.getDeclaredFields();
                    fields = (Field[]) ArrayUtils.addAll(fields, fields1);
                }
                for (Field field : fields) {
                    if ("serialVersionUID".equals(field.getName()))
                        continue;
                    try {
                        field.setAccessible(true);
                        Object searchValue = field.get(search);
                        Compare compare = field.getAnnotation(Compare.class);

                        if (searchValue != null) {
                            if (compare == null) {
                                predicates.add(cb.equal(root.get(field.getName()), searchValue));
                            } else {
                                if (compare.ignore()) {
                                    continue;
                                }
                                String fieldName =
                                        (ObjectUtils.isEmpty(compare.name()) ? field.getName()
                                                : compare.name());
                                // set compare
                                Object convertValue = searchValue;
                                if (!ConvertFunctionEnum.NULL.equals(compare.convertFunction())) {
                                    convertValue = ConvertFunctionHandler
                                            .get(compare.convertFunction()).apply(searchValue);
                                }

                                Operator operator = compare.operator();
                                if (Operator.IN.equals(operator)) {
                                    List<?> queryData = (List<?>) searchValue;
                                    predicates.add(root.get(fieldName).in(queryData));
                                } else if (fieldName.contains(".")) {
                                    predicates.add(buildPredicate(cb, compare.operator(),
                                            queryPathFetch(cq, root, fieldName), convertValue));
                                } else {
                                    predicates.add(buildPredicate(cb, compare.operator(),
                                            root.get(fieldName), convertValue));
                                }
                            }
                        }
                        // set order by
                        if (!ObjectUtils.isEmpty(compare)) {
                            String orderby = compare.orderby();
                            if (!ObjectUtils.isEmpty(orderby) && "DESC".equalsIgnoreCase(orderby)) {
                                cq.orderBy(cb.desc(root.get(field.getName())));
                            }
                        }
                    } catch (IllegalAccessException | IllegalArgumentException
                            | SecurityException ex) {
                        ex.printStackTrace();
                    }
                }
            }
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }

    default <K> Predicate buildPredicate(CriteriaBuilder cb, Operator operator, Path<K> attribute,
            K searchValue) {
        switch (operator) {
            case EQ:
                return cb.equal(attribute, searchValue);
            case NE:
                return cb.notEqual(attribute, searchValue);
            case GE:
                return cb.greaterThanOrEqualTo((Expression) attribute, (Comparable) searchValue);
            case GT:
                return cb.greaterThanOrEqualTo((Expression) attribute, (Comparable) searchValue);
            case LE:
                return cb.lessThanOrEqualTo((Expression) attribute, (Comparable) searchValue);
            case LT:
                return cb.lessThan((Expression) attribute, (Comparable) searchValue);
            case LIKE:
                return cb.like(cb.lower((Expression) attribute),
                        "%" + searchValue.toString().toLowerCase() + "%");
            case NOT_LIKE:
                return cb.notLike(cb.lower((Expression) attribute),
                        "%" + searchValue.toString().toLowerCase() + "%");
            case IN:
                Expression predicate = (Expression) attribute;
                predicate.in(searchValue);
                return cb.in(predicate).value(searchValue);
            default: {
                return cb.equal(attribute, searchValue);
            }
        }
    }

    private <K> Path<K> queryPathFetch(CriteriaQuery<?> cq, Root root, String fieldName) {
        String[] fieldsName = fieldName.split("\\.");
        Path<K> p = null;

        if (fieldsName.length > 1) {
            Join fetch = null;
            for (int i = 0; i < fieldsName.length - 1; i++) {
                String fieldQueryName = fieldsName[i];
                Class<?> resultType = cq.getResultType();
                if ((resultType == Long.class) || (resultType == long.class)) {
                    if (Objects.isNull(fetch)) {
                        fetch = CriteriaUtils.getOrCreateJoin(root, root, fieldQueryName,
                                From::join);
                    } else {
                        fetch = CriteriaUtils.getOrCreateJoin(root, fetch, fieldQueryName,
                                From::join);
                    }
                } else {
                    if (Objects.isNull(fetch)) {
                        fetch = CriteriaUtils.getOrCreateFetchJoin(root, root, fieldQueryName,
                                From::fetch);
                    } else {
                        fetch = CriteriaUtils.getOrCreateFetchJoin(root, fetch, fieldQueryName,
                                From::fetch);
                    }
                }
            }

            p = ((Path) fetch).get(fieldsName[fieldsName.length - 1]);
        } else {
            for (String f : fieldsName) {
                p = ObjectUtils.isEmpty(p) ? root.get(f) : p.get(f);
            }
        }

        return p;
    }
}
