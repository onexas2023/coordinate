package onexas.coordinate.common.model;

/**
 * 
 * @author Dennis Chen
 *
 */

public class DoubleBetween {
	Double from;
	Double to;

	public Double getFrom() {
		return from;
	}

	public void setFrom(Double from) {
		this.from = from;
	}

	public Double getTo() {
		return to;
	}

	public void setTo(Double to) {
		this.to = to;
	}

	public DoubleBetween normalize() {
		if (from != null && to != null) {
			return new DoubleBetween().withFrom(Math.min(from, to)).withTo(Math.max(from, to));
		}
		return new DoubleBetween().withFrom(from).withTo(to);
	}

	public DoubleBetween withFrom(Double from) {
		this.from = from;
		return this;
	}

	public DoubleBetween withTo(Double to) {
		this.to = to;
		return this;
	}

}
