package am.ik.surveys.organization;

import am.ik.surveys.tsid.TsidHolder;
import io.hypersistence.tsid.TSID;

/**
 * 組織ID
 *
 * @param value ID
 */
public record OrganizationId(TSID value) implements TsidHolder {
	public static OrganizationId valueOf(String s) {
		return new OrganizationId(TSID.from(s));
	}

	public static OrganizationId valueOf(long l) {
		return new OrganizationId(TSID.from(l));
	}
}
