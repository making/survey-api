package am.ik.surveys.tsid;

import java.time.Instant;

import com.github.f4b6a3.tsid.Tsid;

public interface TsidHolder {

	Tsid value();

	default Instant asInstant() {
		return value().getInstant();
	}

	default String asString() {
		return value().toString();
	}

	default long asLong() {
		return value().toLong();
	}

}
