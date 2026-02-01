package com.yisu.server.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yisu.server.common.Result;
import com.yisu.server.entity.HotelReview;
import com.yisu.server.service.HotelReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/review")
@CrossOrigin
public class HotelReviewController {

    @Autowired
    private HotelReviewService reviewService;

    // 添加点评
    @PostMapping("/add")
    public Result<Object> addReview(@RequestBody HotelReview review) {
        reviewService.save(review);
        return Result.success();
    }

    // 获取酒店点评列表
    @GetMapping("/list")
    public Result<IPage<HotelReview>> getReviews(@RequestParam Integer hotelId,
                                                  @RequestParam(defaultValue = "1") Integer pageNum,
                                                  @RequestParam(defaultValue = "10") Integer pageSize) {
        IPage<HotelReview> page = reviewService.getReviewsByHotelId(hotelId, pageNum, pageSize);
        return Result.success(page);
    }

    // 获取酒店点评数量
    @GetMapping("/count")
    public Result<Integer> getReviewCount(@RequestParam Integer hotelId) {
        Integer count = reviewService.getReviewCountByHotelId(hotelId);
        return Result.success(count);
    }

    // 获取酒店平均评分
    @GetMapping("/rating")
    public Result<Double> getAverageRating(@RequestParam Integer hotelId) {
        Double rating = reviewService.getAverageRatingByHotelId(hotelId);
        return Result.success(rating);
    }
}
