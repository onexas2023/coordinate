package onexas.coordinate.common.model;

/**
 * 
 * @author Dennis Chen
 *
 */

public class IntBetween {
	Integer from;
	Integer to;

	
	
	public IntBetween() {
	}
	
	public IntBetween(Integer from, Integer to) {
		this.from = from;
		this.to = to;
	}

	public Integer getFrom() {
		return from;
	}

	public void setFrom(Integer from) {
		this.from = from;
	}

	public Integer getTo() {
		return to;
	}

	public void setTo(Integer to) {
		this.to = to;
	}

	public IntBetween normalize() {
		if (from != null && to != null) {
			return new IntBetween().withFrom(Math.min(from, to)).withTo(Math.max(from, to));
		}
		return new IntBetween().withFrom(from).withTo(to);
	}

	public IntBetween withFrom(Integer from) {
		this.from = from;
		return this;
	}

	public IntBetween withTo(Integer to) {
		this.to = to;
		return this;
	}

}
