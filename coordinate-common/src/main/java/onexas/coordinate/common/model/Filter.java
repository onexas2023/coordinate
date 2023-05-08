package onexas.coordinate.common.model;

/**
 * 
 * @author Dennis Chen
 *
 */

public class Filter extends Sort {

	private static final long serialVersionUID = 1L;

	protected Boolean matchAny;

	protected Boolean strContaining;

	protected Boolean strIgnoreCase;

	public Boolean getMatchAny() {
		return matchAny;
	}

	public void setMatchAny(Boolean matchAny) {
		this.matchAny = matchAny;
	}

	public Boolean getStrIgnoreCase() {
		return strIgnoreCase;
	}

	public Boolean getStrContaining() {
		return strContaining;
	}

	public void setStrContaining(Boolean strContaining) {
		this.strContaining = strContaining;
	}

	public void setStrIgnoreCase(Boolean strIgnoreCase) {
		this.strIgnoreCase = strIgnoreCase;
	}

	public Filter withMatchAny(Boolean matchAny) {
		this.matchAny = matchAny;
		return this;
	}

	public Filter withStrContaining(Boolean strContaining) {
		this.strContaining = strContaining;
		return this;
	}

	public Filter withStrIgnoreCase(Boolean strIgnoreCase) {
		this.strIgnoreCase = strIgnoreCase;
		return this;
	}

	public Filter withSortDesc(Boolean sortDesc) {
		this.sortDesc = sortDesc;
		return this;
	}

	public Filter withSortField(String sortField) {
		this.sortField = sortField;
		return this;
	}

}