package com.luma.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.luma.system.domain.entity.SysTenant;
import com.luma.system.mapper.SysTenantMapper;
import com.luma.system.service.SysTenantService;
import org.springframework.stereotype.Service;

/**
 * @author 刘靖
 */
@Service
public class SysTenantServiceImpl extends ServiceImpl<SysTenantMapper, SysTenant> implements SysTenantService {
}
