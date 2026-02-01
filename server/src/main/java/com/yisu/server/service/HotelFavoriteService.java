package com.yisu.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yisu.server.entity.HotelFavorite;

public interface HotelFavoriteService extends IService<HotelFavorite> {
    boolean toggleFavorite(Integer hotelId, Integer userId);
    boolean isFavorited(Integer hotelId, Integer userId);
    Integer getFavoriteCountByHotelId(Integer hotelId);
}
