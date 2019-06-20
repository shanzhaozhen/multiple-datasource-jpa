package org.shanzhaozhen.multipledatasourcejpa.config.datasource;

import org.shanzhaozhen.multipledatasourcejpa.utils.UserDetailsUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;

import java.util.Optional;

/**
 * 配置自动生成创建人或修改人id
 */
@Configuration
public class MyAuditorAware implements AuditorAware<Integer> {

    @Override
    public Optional<Integer> getCurrentAuditor() {
        Integer id = UserDetailsUtils.getSysUserId();
        return Optional.ofNullable(id);
    }

}
