package com.wc.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wc.mapper.UserFavoriteMarketMapper;
import com.wc.domain.UserFavoriteMarket;
import com.wc.service.UserFavoriteMarketService;
@Service
public class UserFavoriteMarketServiceImpl extends ServiceImpl<UserFavoriteMarketMapper, UserFavoriteMarket> implements UserFavoriteMarketService{

}
