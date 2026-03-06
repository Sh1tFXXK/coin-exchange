package com.wc.service.impl;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wc.mapper.ForexAccountDetailMapper;
import com.wc.domain.ForexAccountDetail;
import com.wc.service.ForexAccountDetailService;

@Service
public class ForexAccountDetailServiceImpl extends ServiceImpl<ForexAccountDetailMapper, ForexAccountDetail> implements ForexAccountDetailService {

}

