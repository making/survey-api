package am.ik.surveys.tsid;

import java.sql.Types;
import java.time.Instant;
import java.util.HexFormat;

import io.hypersistence.tsid.TSID;

import org.springframework.jdbc.core.SqlParameterValue;

public interface TsidHolder {

	TSID value();

	default Instant asInstant() {
		return value().getInstant();
	}

	default String asString() {
		return value().toString();
	}

	default byte[] asBytes() {
		return value().toBytes();
	}

	default String formatHex() {
		return HexFormat.of().formatHex(asBytes());
	}

	default String toByteaLiteral() {
		return "E'\\\\x%s'".formatted(formatHex());
	}

	default SqlParameterValue toBytesSqlParameterValue() {
		return new SqlParameterValue(Types.BINARY, asBytes());
	}

}
