package onexas.coordinate.common.model;

/**
 * 
 * @author Dennis Chen
 *
 */

public class LongBetween {
	Long from;
	Long to;

	public LongBetween() {
	}

	public LongBetween(Long from, Long to) {
		super();
		this.from = from;
		this.to = to;
	}

	public Long getFrom() {
		return from;
	}

	public void setFrom(Long from) {
		this.from = from;
	}

	public Long getTo() {
		return to;
	}

	public void setTo(Long to) {
		this.to = to;
	}

	public LongBetween normalize() {
		if (from != null && to != null) {
			return new LongBetween().withFrom(Math.min(from, to)).withTo(Math.max(from, to));
		}
		return new LongBetween().withFrom(from).withTo(to);
	}

	public LongBetween withFrom(Long from) {
		this.from = from;
		return this;
	}

	public LongBetween withTo(Long to) {
		this.to = to;
		return this;
	}

}
