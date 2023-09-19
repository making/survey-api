package am.ik.surveys.tsid;

import java.time.Instant;

import io.hypersistence.tsid.TSID;

public interface TsidHolder {

	TSID value();

	default Instant asInstant() {
		return value().getInstant();
	}

	default long asLong() {
		return value().toLong();
	}

	default String asString() {
		return value().toString();
	}

}
