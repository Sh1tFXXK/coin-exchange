package com.wc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wc.domain.UserFavoriteMarket;

public interface UserFavoriteMarketService extends IService<UserFavoriteMarket>{


    /**
     * 用户取消收藏
     * @param marketId
     * @param userId
     * @return
     */
    boolean deleteUserFavoriteMarket(Long marketId, Long userId);
}
