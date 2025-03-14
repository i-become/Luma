package com.luma.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.luma.system.domain.entity.ClientSysUser;
import com.luma.system.mapper.ClientSysUserMapper;
import com.luma.system.service.ClientSysUserService;
import org.springframework.stereotype.Service;

@Service
public class ClientSysUserServiceImpl extends ServiceImpl<ClientSysUserMapper, ClientSysUser> implements ClientSysUserService {

}
