package onexas.coordinate.service.impl.dao;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import onexas.coordinate.common.app.Env;
import onexas.coordinate.common.model.LongBetween;
import onexas.coordinate.data.CoordinateEntityManageConfiguration;
import onexas.coordinate.data.util.Daos;
import onexas.coordinate.model.LogFilter;
import onexas.coordinate.service.impl.entity.LogEntity;

/**
 * 
 * @author Dennis Chen
 *
 */
@Component(Env.NS_BEAN + "LogEntityDao")
public class LogEntityDao {

	@PersistenceContext(unitName = CoordinateEntityManageConfiguration.PERSISTENT_UNIT)
	private EntityManager em;

	public Page<LogEntity> findAll(LogFilter filter, Pageable pageable) {

		int offset = (int) pageable.getOffset();
		int pageSize = pageable.getPageSize();
		Sort sort = pageable.getSort();

		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<LogEntity> selectQuery = builder.createQuery(LogEntity.class);
		CriteriaQuery<Long> countQuery = builder.createQuery(Long.class);

		Root<LogEntity> selectRoot = selectQuery.from(LogEntity.class);
		Root<LogEntity> countRoot = countQuery.from(LogEntity.class);

		selectQuery.select(selectRoot);
		countQuery.select(builder.count(countRoot));

		List<Predicate> selectPredicates = new LinkedList<>();
		List<Predicate> countPredicates = new LinkedList<>();

		Integer levelGe = filter.getLevelGe();
		if (levelGe != null) {
			selectPredicates.add(builder.ge(selectRoot.<Integer>get("level"), levelGe));
			countPredicates.add(builder.ge(countRoot.<Integer>get("level"), levelGe));
		}

		LongBetween createdDateTimeBetween = filter.getCreatedDateTimeBetween();
		if (createdDateTimeBetween != null) {
			createdDateTimeBetween = createdDateTimeBetween.normalize();
			Long from = createdDateTimeBetween.getFrom();
			Long to = createdDateTimeBetween.getTo();

			List<Predicate> selectAnd = new ArrayList<Predicate>(2);
			List<Predicate> countAnd = new ArrayList<Predicate>(2);

			if (from != null) {
				selectAnd.add(builder.ge(selectRoot.<Long>get("createdDateTime"), from));
				countAnd.add(builder.ge(countRoot.<Long>get("createdDateTime"), from));
			}
			if (to != null) {
				selectAnd.add(builder.le(selectRoot.<Long>get("createdDateTime"), to));
				countAnd.add(builder.le(countRoot.<Long>get("createdDateTime"), to));
			}

			if (selectAnd.size() == 2) {
				// make and
				selectPredicates.add(builder.and(selectAnd.toArray(new Predicate[selectAnd.size()])));
				countPredicates.add(builder.and(countAnd.toArray(new Predicate[countAnd.size()])));
			} else if (selectAnd.size() == 1) {
				// just add to root, doesn't need to and
				selectPredicates.add(selectAnd.get(0));
				countPredicates.add(countAnd.get(0));
			}

		}

		Boolean containing = filter.getStrContaining();
		Boolean ignorecase = filter.getStrIgnoreCase();

		String subjectUid = filter.getSubjectUid();
		String subjectType = filter.getSubjectType();
		String objUid = filter.getObjUid();
		String objType = filter.getObjType();
		String requestUid = filter.getRequestUid();
		String reporter = filter.getReporter();
		for (String[] attr : new String[][] { { "subjectUid", subjectUid }, { "subjectType", subjectType },
				{ "objUid", objUid }, { "objType", objType }, { "requestUid", requestUid },
				{ "reporter", reporter } }) {
			if (attr[1] != null) {
				selectPredicates
						.add(Daos.buildStringPredicate(builder, selectRoot, attr[0], attr[1], containing, ignorecase));
				countPredicates
						.add(Daos.buildStringPredicate(builder, countRoot, attr[0], attr[1], containing, ignorecase));
			}
		}

		Boolean matchAny = filter.getMatchAny();
		if (selectPredicates.size() > 0) {
			if (Boolean.TRUE.equals(matchAny)) {
				selectQuery.where(builder.or(selectPredicates.toArray(new Predicate[selectPredicates.size()])));
				countQuery.where(builder.or(countPredicates.toArray(new Predicate[countPredicates.size()])));
			} else {
				selectQuery.where(builder.and(selectPredicates.toArray(new Predicate[selectPredicates.size()])));
				countQuery.where(builder.and(countPredicates.toArray(new Predicate[countPredicates.size()])));
			}
		}

		List<Order> orders = new LinkedList<Order>();
		sort.forEach(new Consumer<org.springframework.data.domain.Sort.Order>() {
			@Override
			public void accept(org.springframework.data.domain.Sort.Order order) {
				orders.add(order.isAscending() ? builder.asc(selectRoot.get(order.getProperty()))
						: builder.desc(selectRoot.get(order.getProperty())));
			}
		});

		if (orders.size() > 0) {
			selectQuery.orderBy(orders);
		}

		TypedQuery<LogEntity> query = em.createQuery(selectQuery);
		query.setFirstResult(offset);
		query.setMaxResults(pageSize);

		List<LogEntity> content = new LinkedList<>(query.getResultList());

		Number count = (Number) em.createQuery(countQuery).getSingleResult();
		return new PageImpl<LogEntity>(content, pageable, count.longValue());
	}
}