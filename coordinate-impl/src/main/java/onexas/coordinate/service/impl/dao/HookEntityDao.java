package onexas.coordinate.service.impl.dao;

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
import onexas.coordinate.data.CoordinateEntityManageConfiguration;
import onexas.coordinate.data.util.Daos;
import onexas.coordinate.model.HookFilter;
import onexas.coordinate.service.impl.entity.HookEntity;

/**
 * 
 * @author Dennis Chen
 *
 */
@Component(Env.NS_BEAN + "HookEntityDao")
public class HookEntityDao {

	@PersistenceContext(unitName = CoordinateEntityManageConfiguration.PERSISTENT_UNIT)
	private EntityManager em;

	public Page<HookEntity> findAll(HookFilter filter, Pageable pageable) {

		int offset = (int) pageable.getOffset();
		int pageSize = pageable.getPageSize();
		Sort sort = pageable.getSort();

		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<HookEntity> selectQuery = builder.createQuery(HookEntity.class);
		CriteriaQuery<Long> countQuery = builder.createQuery(Long.class);

		Root<HookEntity> selectRoot = selectQuery.from(HookEntity.class);
		Root<HookEntity> countRoot = countQuery.from(HookEntity.class);

		selectQuery.select(selectRoot);
		countQuery.select(builder.count(countRoot));

		List<Predicate> selectPredicates = new LinkedList<>();
		List<Predicate> countPredicates = new LinkedList<>();

		Boolean containing = filter.getStrContaining();
		Boolean ignorecase = filter.getStrIgnoreCase();

		String uid = filter.getUid();
		String zone = filter.getZone();
		String subjectType = filter.getSubjectType();
		String subjectUid = filter.getSubjectUid();
		String ownerType = filter.getOwnerType();
		String ownerUid = filter.getOwnerUid();
		for (String[] attr : new String[][] { { "uid", uid }, { "zone", zone }, { "subjectType", subjectType },
				{ "subjectUid", subjectUid }, { "ownerType", ownerType }, { "ownerUid", ownerUid }}) {
			if (attr[1] != null) {
				selectPredicates
						.add(Daos.buildStringPredicate(builder, selectRoot, attr[0], attr[1], containing, ignorecase));
				countPredicates
						.add(Daos.buildStringPredicate(builder, countRoot, attr[0], attr[1], containing, ignorecase));
			}
		}

		Boolean matchAny = filter.getMatchAny();

		Predicate selectConditionPredicate = null;
		Predicate countConditionPredicate = null;

		if (selectPredicates.size() > 0) {
			if (Boolean.TRUE.equals(matchAny)) {
				selectConditionPredicate = builder.or(selectPredicates.toArray(new Predicate[selectPredicates.size()]));
				countConditionPredicate = builder.or(countPredicates.toArray(new Predicate[countPredicates.size()]));
			} else {
				selectConditionPredicate = builder
						.and(selectPredicates.toArray(new Predicate[selectPredicates.size()]));
				countConditionPredicate = builder.and(countPredicates.toArray(new Predicate[countPredicates.size()]));
			}
		}

		if (selectConditionPredicate != null) {
			selectQuery.where(selectConditionPredicate);
			countQuery.where(countConditionPredicate);
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

		TypedQuery<HookEntity> query = em.createQuery(selectQuery);
		query.setFirstResult(offset);
		query.setMaxResults(pageSize);

		List<HookEntity> content = new LinkedList<>(query.getResultList());

		Number count = (Number) em.createQuery(countQuery).getSingleResult();
		return new PageImpl<HookEntity>(content, pageable, count.longValue());
	}
}