package onexas.coordinate.service.impl.dao;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import onexas.coordinate.common.app.Env;
import onexas.coordinate.data.CoordinateEntityManageConfiguration;
import onexas.coordinate.service.impl.entity.RoleUserRelationEntity;
import onexas.coordinate.service.impl.entity.UserEntity;

/**
 * 
 * @author Dennis Chen
 *
 */
@Component(Env.NS_BEAN + "RoleUserRelationDao")
public class RoleUserRelationEntityDao {

	@PersistenceContext(unitName = CoordinateEntityManageConfiguration.PERSISTENT_UNIT)
	private EntityManager em;

	public Page<UserEntity> findAllUsersByRoleUid(String roleUid, Pageable pageable) {

		int offset = (int) pageable.getOffset();
		int pageSize = pageable.getPageSize();
		Sort sort = pageable.getSort();

		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<UserEntity> selectQuery = builder.createQuery(UserEntity.class);
		CriteriaQuery<Long> countQuery = builder.createQuery(Long.class);

		Root<RoleUserRelationEntity> selectRoot = selectQuery.from(RoleUserRelationEntity.class);
		Root<RoleUserRelationEntity> countRoot = countQuery.from(RoleUserRelationEntity.class);

		Join<RoleUserRelationEntity, UserEntity> selectUser = selectRoot.join("user");
		Join<RoleUserRelationEntity, UserEntity> countUser = countRoot.join("user");

		selectQuery.distinct(true).select(selectUser);
		countQuery.select(builder.countDistinct(countUser));

		selectQuery.where(builder.equal(selectRoot.<String>get("roleUid"), roleUid));
		countQuery.where(builder.equal(countRoot.<String>get("roleUid"), roleUid));

		List<Order> orders = new LinkedList<Order>();
		sort.forEach(new Consumer<org.springframework.data.domain.Sort.Order>() {
			@Override
			public void accept(org.springframework.data.domain.Sort.Order order) {
				orders.add(order.isAscending() ? builder.asc(selectUser.get(order.getProperty()))
						: builder.desc(selectUser.get(order.getProperty())));
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