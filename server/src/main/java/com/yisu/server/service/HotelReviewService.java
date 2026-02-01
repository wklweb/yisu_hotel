package com.yisu.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yisu.server.entity.HotelReview;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

public interface HotelReviewService extends IService<HotelReview> {
    IPage<HotelReview> getReviewsByHotelId(Integer hotelId, Integer pageNum, Integer pageSize);
    Integer getReviewCountByHotelId(Integer hotelId);
    Double getAverageRatingByHotelId(Integer hotelId);
}
