package am.ik.surveys.tsid;

import java.sql.Types;
import java.time.Instant;

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

	default SqlParameterValue toBytesSqlParameterValue() {
		return new SqlParameterValue(Types.BINARY, value().toBytes());
	}

}
