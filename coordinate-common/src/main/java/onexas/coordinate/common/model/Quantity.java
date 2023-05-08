package onexas.coordinate.common.model;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Objects;

import org.apache.commons.lang3.ObjectUtils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import io.swagger.v3.oas.annotations.media.Schema;
import onexas.coordinate.common.util.QuantityFormatter;

/**
 * code from
 * /io/kubernetes/client-java-api/6.0.1/client-java-api-6.0.1-sources.jar
 * 
 * @author Dennis Chen
 *
 */
@JsonAdapter(Quantity.QuantityAdapter.class)
@JsonSerialize(using = Quantity.QuantitySerializer.class)
@JsonDeserialize(using = Quantity.QuantityDeserializer.class)
@Schema(type = "string")
public class Quantity {

	private final BigDecimal number;
	private Format format;

	public enum Format {
		DECIMAL_EXPONENT(10), DECIMAL_SI(10), BINARY_SI(2);

		private int base;

		Format(final int base) {
			this.base = base;
		}

		public int getBase() {
			return base;
		}
	}

	public Quantity(final BigDecimal number, final Format format) {
		this.number = number;
		this.format = format;
	}

	public Quantity(final String value) {
		final Quantity quantity = fromString(value);
		this.number = quantity.number;
		this.format = quantity.format;
	}

	public BigDecimal getNumber() {
		return number;
	}

	public Format getFormat() {
		return format;
	}

	public static Quantity fromString(final String value) {
		return new QuantityFormatter().parse(value);
	}

	public String toSuffixedString() {
		return new QuantityFormatter().format(this);
	}

	@Override
	public String toString() {
		return "Quantity{" + "number=" + number + ", format=" + format + '}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		Quantity otherQuantity = (Quantity) o;

		return ObjectUtils.compare(this.number, otherQuantity.number) == 0
				&& Objects.equals(this.format, otherQuantity.format);
	}

	public static class QuantityAdapter extends TypeAdapter<Quantity> {
		@Override
		public void write(JsonWriter jsonWriter, Quantity quantity) throws IOException {
			jsonWriter.value(quantity != null ? quantity.toSuffixedString() : null);
		}

		@Override
		public Quantity read(JsonReader jsonReader) throws IOException {
			String value = jsonReader.nextString();
			return value == null ? null : Quantity.fromString(value);
		}
	}

	public static class QuantitySerializer extends StdSerializer<Quantity> {
		private static final long serialVersionUID = 1L;

		public QuantitySerializer() {
			this(null);
		}

		public QuantitySerializer(Class<Quantity> t) {
			super(t);
		}

		@Override
		public void serialize(Quantity quantity, JsonGenerator gen, SerializerProvider arg2)
				throws IOException, JsonProcessingException {
			if (quantity == null) {
				gen.writeNull();
			} else {
				gen.writeString(quantity.toSuffixedString());
			}
		}
	}

	public static class QuantityDeserializer extends StdDeserializer<Quantity> {
		private static final long serialVersionUID = 1L;

		public QuantityDeserializer() {
			this(null);
		}

		public QuantityDeserializer(Class<Quantity> vc) {
			super(vc);
		}

		@Override
		public Quantity deserialize(JsonParser jsonparser, DeserializationContext context) throws IOException {
			String value = jsonparser.getText();
			return value == null ? null : Quantity.fromString(value);
		}
	}
}
