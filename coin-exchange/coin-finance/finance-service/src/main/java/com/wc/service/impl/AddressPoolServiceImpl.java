package com.wc.service.impl;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wc.domain.AddressPool;
import com.wc.mapper.AddressPoolMapper;
import com.wc.service.AddressPoolService;
@Service
public class AddressPoolServiceImpl extends ServiceImpl<AddressPoolMapper, AddressPool> implements AddressPoolService{

}
