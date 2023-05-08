package onexas.coordinate.service.impl.dao;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import onexas.coordinate.common.app.Env;
import onexas.coordinate.common.util.ValueII;
import onexas.coordinate.data.CoordinateEntityManageConfiguration;
import onexas.coordinate.data.util.Daos;
import onexas.coordinate.model.OrganizationUserFilter;
import onexas.coordinate.model.OrganizationUserRelationType;
import onexas.coordinate.service.impl.entity.OrganizationUserRelationEntity;
import onexas.coordinate.service.impl.entity.UserEntity;

/**
 * 
 * @author Dennis Chen
 *
 */
@Component(Env.NS_BEAN + "OrganizationUserRelationDao")
public class OrganizationUserRelationEntityDao {

	@PersistenceContext(unitName = CoordinateEntityManageConfiguration.PERSISTENT_UNIT)
	private EntityManager em;

	public Page<ValueII<UserEntity, OrganizationUserRelationType>> findAllUsersByOrganizationUid(String organizationUid,
			OrganizationUserFilter filter, Pageable pageable) {

		int offset = (int) pageable.getOffset();
		int pageSize = pageable.getPageSize();
		Sort sort = pageable.getSort();

		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<Tuple> selectQuery = builder.createQuery(Tuple.class);
		CriteriaQuery<Long> countQuery = builder.createQuery(Long.class);

		Root<OrganizationUserRelationEntity> selectRoot = selectQuery.from(OrganizationUserRelationEntity.class);
		Root<OrganizationUserRelationEntity> countRoot = countQuery.from(OrganizationUserRelationEntity.class);

		Join<OrganizationUserRelationEntity, UserEntity> selectUser = selectRoot.join("user");
		Join<OrganizationUserRelationEntity, UserEntity> countUser = countRoot.join("user");

		selectQuery.distinct(true).multiselect(selectRoot, selectUser);
		countQuery.select(builder.countDistinct(countUser));

		List<Predicate> selectPredicates = new LinkedList<>();
		List<Predicate> countPredicates = new LinkedList<>();

		Boolean containing = filter.getStrContaining();
		Boolean ignorecase = filter.getStrIgnoreCase();

		String account = filter.getAccount();
		String displayName = filter.getDisplayName();
		String email = filter.getEmail();
		for (String[] attr : new String[][] { { "account", account }, { "displayName", displayName },
				{ "email", email } }) {
			if (attr[1] != null) {
				selectPredicates
						.add(Daos.buildStringPredicate(builder, selectUser, attr[0], attr[1], containing, ignorecase));
				countPredicates
						.add(Daos.buildStringPredicate(builder, countUser, attr[0], attr[1], containing, ignorecase));
			}
		}

		Predicate selectOrganizationPredicate = builder.equal(selectRoot.<String>get("organizationUid"),
				organizationUid);
		Predicate countOrganizationPredicate = builder.equal(countRoot.<String>get("organizationUid"), organizationUid);

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
			selectQuery.where(builder.and(selectOrganizationPredicate, selectConditionPredicate));
			countQuery.where(builder.and(countOrganizationPredicate, countConditionPredicate));
		} else {
			selectQuery.where(selectOrganizationPredicate);
			countQuery.where(countOrganizationPredicate);
		}

		List<Order> selectOrders = new LinkedList<Order>();
		sort.forEach(new Consumer<org.springframework.data.domain.Sort.Order>() {
			@Override
			public void accept(org.springframework.data.domain.Sort.Order order) {
				selectOrders.add(order.isAscending() ? builder.asc(selectUser.get(order.getProperty()))
						: builder.desc(selectUser.get(order.getProperty())));
			}
		});

		if (selectOrders.size() > 0) {
			selectQuery.orderBy(selectOrders);
		}

		TypedQuery<Tuple> selectTypeQuery = em.createQuery(selectQuery);
		selectTypeQuery.setFirstResult(offset);
		selectTypeQuery.setMaxResults(pageSize);

		List<Tuple> entityContent = selectTypeQuery.getResultList();

		Number count = (Number) em.createQuery(countQuery).getSingleResult();

		List<ValueII<UserEntity, OrganizationUserRelationType>> content = new LinkedList<>();
		for (Tuple t : entityContent) {

			content.add(new ValueII<UserEntity, OrganizationUserRelationType>((UserEntity) t.get(1),
					((OrganizationUserRelationEntity) t.get(0)).getType()));
		}

		return new PageImpl<ValueII<UserEntity, OrganizationUserRelationType>>(content, pageable, count.longValue());
	}

}