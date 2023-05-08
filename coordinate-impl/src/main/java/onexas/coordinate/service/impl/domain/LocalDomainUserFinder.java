package onexas.coordinate.service.impl.domain;
/**
 * 
 * @author Dennis Chen
 *
 */

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

import onexas.coordinate.common.app.AppContext;
import onexas.coordinate.common.err.NotFoundException;
import onexas.coordinate.common.lang.Strings;
import onexas.coordinate.common.model.ListPage;
import onexas.coordinate.data.util.Fields;
import onexas.coordinate.model.DomainConfig;
import onexas.coordinate.model.DomainUser;
import onexas.coordinate.model.DomainUserFilter;
import onexas.coordinate.service.domain.DomainUserFinder;
import onexas.coordinate.service.impl.dao.UserEntityRepo;
import onexas.coordinate.service.impl.entity.UserEntity;

/**
 * 
 * @author Dennis Chen
 *
 */
public class LocalDomainUserFinder implements DomainUserFinder {
	String domainCode;
	DomainConfig domainConfig;

	public LocalDomainUserFinder(String domainCode, DomainConfig domainConfig) {
		this.domainCode = domainCode;
		this.domainConfig = domainConfig;
	}

	@Override
	public ListPage<DomainUser> list(DomainUserFilter filter) {
		UserEntityRepo userRepo = AppContext.getBean(UserEntityRepo.class);
		List<DomainUser> list = new LinkedList<>();
		List<UserEntity> entityList = new LinkedList<>();

		Direction direction = filter != null && Boolean.TRUE.equals(filter.getSortDesc()) ? Direction.DESC
				: Direction.ASC;

		Sort sort = Sort.by(direction, "account");
		Integer pageIndex = filter == null || filter.getPageIndex() == null ? 0 : filter.getPageIndex();
		Integer pageSize = filter == null || filter.getPageSize() == null ? Integer.MAX_VALUE : filter.getPageSize();

		String criteria = null;

		if (filter != null) {
			String sortField = filter.getSortField();

			if (sortField != null) {
				Fields.checkFieldsIn(new String[] { sortField },
						new String[] { "account", "email", "displayName", "createdDateTime" });
				sort = Sort.by(direction, sortField);
			}

			criteria = filter.getCriteria();
		}
		if (Strings.isBlank(criteria)) {
			criteria = "%";
		} else {
			criteria = "%" + criteria + "%";
		}

		Pageable pageable = PageRequest.of(pageIndex, pageSize, sort);

		Page<UserEntity> page = userRepo.findAllByCriteriaDomain(criteria, domainCode, pageable);
		entityList.addAll(page.getContent());
		Integer pageTotal = page.getTotalPages();
		Long itemTotal = page.getTotalElements();

		for (UserEntity entity : entityList) {
			DomainUser du = new DomainUser();
			du.setIdentity(entity.getUid());
			du.setAccount(entity.getAccount());
			du.setDisplayName(entity.getDisplayName());
			du.setEmail(entity.getEmail());
			list.add(du);
		}
		return new ListPage<>(list, pageIndex, pageSize == null ? list.size() : pageSize, pageTotal, itemTotal);
	}

	@Override
	public DomainUser get(String identity) {
		DomainUser u = find(identity);
		if (u == null) {
			throw new NotFoundException("user {}#{} not found", identity, domainCode);
		}
		return u;
	}

	@Override
	public DomainUser find(String identity) {
		UserEntityRepo userRepo = AppContext.getBean(UserEntityRepo.class);
		Optional<UserEntity> o = userRepo.findById(identity);
		if (o.isPresent()) {
			UserEntity e = o.get();
			if (domainCode.equals(e.getDomain())) {
				return new DomainUser().withIdentity(e.getUid()).withAccount(e.getAccount()).withDomain(domainCode)
						.withDisplayName(e.getDisplayName()).withEmail(e.getEmail());
			}
		}
		return null;
	}

	@Override
	public DomainUser findByAccount(String account) {
		UserEntityRepo userRepo = AppContext.getBean(UserEntityRepo.class);
		Optional<UserEntity> o = userRepo.findByAccountDomain(account, domainCode);
		if (o.isPresent()) {
			UserEntity e = o.get();
			return new DomainUser().withIdentity(e.getUid()).withAccount(e.getAccount()).withDomain(domainCode)
					.withDisplayName(e.getDisplayName()).withEmail(e.getEmail());

		}
		return null;
	}

}
