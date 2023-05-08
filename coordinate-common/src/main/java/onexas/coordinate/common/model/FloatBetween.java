package onexas.coordinate.common.model;

/**
 * 
 * @author Dennis Chen
 *
 */

public class FloatBetween {
	Float from;
	Float to;
	
	

	public FloatBetween() {
	}

	public FloatBetween(Float from, Float to) {
		this.from = from;
		this.to = to;
	}

	
	public Float getFrom() {
		return from;
	}

	public void setFrom(Float from) {
		this.from = from;
	}

	public Float getTo() {
		return to;
	}

	public void setTo(Float to) {
		this.to = to;
	}

	public FloatBetween normalize() {
		if (from != null && to != null) {
			return new FloatBetween().withFrom(Math.min(from, to)).withTo(Math.max(from, to));
		}
		return new FloatBetween().withFrom(from).withTo(to);
	}

	public FloatBetween withFrom(Float from) {
		this.from = from;
		return this;
	}

	public FloatBetween withTo(Float to) {
		this.to = to;
		return this;
	}

}
