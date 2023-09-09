package am.ik.surveys.config;

import am.ik.surveys.tsid.TsidGenerator;
import io.hypersistence.tsid.TSID;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class TsidConfig {

	@Bean
	public TsidGenerator tsidFactory() {
		return TSID.Factory::getTsid;
	}

}
