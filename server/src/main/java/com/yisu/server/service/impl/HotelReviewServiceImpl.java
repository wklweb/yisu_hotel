package com.yisu.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yisu.server.entity.HotelReview;
import com.yisu.server.mapper.HotelReviewMapper;
import com.yisu.server.service.HotelReviewService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HotelReviewServiceImpl extends ServiceImpl<HotelReviewMapper, HotelReview> implements HotelReviewService {

    @Override
    public IPage<HotelReview> getReviewsByHotelId(Integer hotelId, Integer pageNum, Integer pageSize) {
        Page<HotelReview> page = new Page<>(pageNum, pageSize);
        QueryWrapper<HotelReview> wrapper = new QueryWrapper<>();
        wrapper.eq("hotel_id", hotelId);
        wrapper.orderByDesc("create_time");
        return page(page, wrapper);
    }

    @Override
    public Integer getReviewCountByHotelId(Integer hotelId) {
        QueryWrapper<HotelReview> wrapper = new QueryWrapper<>();
        wrapper.eq("hotel_id", hotelId);
        return Math.toIntExact(count(wrapper));
    }

    @Override
    public Double getAverageRatingByHotelId(Integer hotelId) {
        QueryWrapper<HotelReview> wrapper = new QueryWrapper<>();
        wrapper.eq("hotel_id", hotelId);
        List<HotelReview> reviews = list(wrapper);
        if (reviews.isEmpty()) {
            return 0.0;
        }
        double sum = reviews.stream().mapToInt(HotelReview::getRating).sum();
        return sum / reviews.size();
    }
}
