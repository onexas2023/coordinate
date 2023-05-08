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
import onexas.coordinate.model.UserFilter;
import onexas.coordinate.service.impl.entity.UserEntity;

/**
 * 
 * @author Dennis Chen
 *
 */
@Component(Env.NS_BEAN + "UserEntityDao")
public class UserEntityDao {

	@PersistenceContext(unitName = CoordinateEntityManageConfiguration.PERSISTENT_UNIT)
	private EntityManager em;

	public Page<UserEntity> findAll(UserFilter filter, Boolean disabled, Pageable pageable) {

		int offset = (int) pageable.getOffset();
		int pageSize = pageable.getPageSize();
		Sort sort = pageable.getSort();

		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<UserEntity> selectQuery = builder.createQuery(UserEntity.class);
		CriteriaQuery<Long> countQuery = builder.createQuery(Long.class);

		Root<UserEntity> selectRoot = selectQuery.from(UserEntity.class);
		Root<UserEntity> countRoot = countQuery.from(UserEntity.class);

		selectQuery.select(selectRoot);
		countQuery.select(builder.count(countRoot));

		List<Predicate> selectPredicates = new LinkedList<>();
		List<Predicate> countPredicates = new LinkedList<>();

		Boolean containing = filter.getStrContaining();
		Boolean ignorecase = filter.getStrIgnoreCase();

		String account = filter.getAccount();
		String displayName = filter.getDisplayName();
		String domain = filter.getDomain();
		String email = filter.getEmail();
		for (String[] attr : new String[][] { { "account", account }, { "displayName", displayName },
				{ "domain", domain }, { "email", email } }) {
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
		
		Predicate selectDisabledPredicate = null;
		Predicate countDisabledPredicate = null;
		
		if (selectPredicates.size() > 0) {
			if (Boolean.TRUE.equals(matchAny)) {
				selectConditionPredicate = builder.or(selectPredicates.toArray(new Predicate[selectPredicates.size()]));
				countConditionPredicate = builder.or(countPredicates.toArray(new Predicate[countPredicates.size()]));
			} else {
				selectConditionPredicate = builder.and(selectPredicates.toArray(new Predicate[selectPredicates.size()]));
				countConditionPredicate = builder.and(countPredicates.toArray(new Predicate[countPredicates.size()]));
			}
		}
		
		if (disabled != null) {
			selectDisabledPredicate = builder.equal(selectRoot.<Boolean>get("disabled"), disabled);
			countDisabledPredicate = builder.equal(countRoot.<Boolean>get("disabled"), disabled);
		}
		
		if (selectConditionPredicate!=null && selectDisabledPredicate!=null) {
			selectQuery.where(builder.and(selectConditionPredicate, selectDisabledPredicate));
			countQuery.where(builder.and(countConditionPredicate, countDisabledPredicate));
		}else if(selectConditionPredicate!=null) {
			selectQuery.where(selectConditionPredicate);
			countQuery.where(countConditionPredicate);
		}else if(selectDisabledPredicate!=null) {
			selectQuery.where(selectDisabledPredicate);
			countQuery.where(countDisabledPredicate);
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

		TypedQuery<UserEntity> query = em.createQuery(selectQuery);
		query.setFirstResult(offset);
		query.setMaxResults(pageSize);

		List<UserEntity> content = new LinkedList<>(query.getResultList());

		Number count = (Number) em.createQuery(countQuery).getSingleResult();
		return new PageImpl<UserEntity>(content, pageable, count.longValue());
	}
}