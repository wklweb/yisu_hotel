package com.yisu.server.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yisu.server.common.Result;
import com.yisu.server.entity.Hotel;
import com.yisu.server.entity.RoomType;
import com.yisu.server.service.HotelService;
import com.yisu.server.service.RoomTypeService;
import com.yisu.server.service.HotelReviewService;
import com.yisu.server.service.HotelFavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/hotel")
@CrossOrigin
public class HotelController {

    @Autowired
    private HotelService hotelService;
    
    @Autowired
    private RoomTypeService roomTypeService;
    
    @Autowired
    private HotelReviewService reviewService;
    
    @Autowired
    private HotelFavoriteService favoriteService;

    // Public: List Hotels (Search) - for Android
    @GetMapping("/list")
    public Result<Page<Map<String, Object>>> list(@RequestParam(defaultValue = "1") Integer pageNum,
                                    @RequestParam(defaultValue = "10") Integer pageSize,
                                    @RequestParam(required = false) String name,
                                    @RequestParam(required = false) String city,
                                    @RequestParam(required = false) String starRating) {
        Page<Hotel> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Hotel> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", 1); // Only published
        if (StrUtil.isNotBlank(name)) {
            queryWrapper.like("name", name);
        }
        if (StrUtil.isNotBlank(city)) {
            queryWrapper.eq("city", city);
        }
        if (StrUtil.isNotBlank(starRating)) {
            queryWrapper.eq("star_rating", starRating);
        }
        Page<Hotel> hotelPage = hotelService.page(page, queryWrapper);
        
        // 转换为包含统计信息的Map列表（供Android使用）
        List<Map<String, Object>> hotelList = hotelPage.getRecords().stream().map(hotel -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", hotel.getId());
            map.put("name", hotel.getName());
            map.put("nameEn", hotel.getNameEn());
            map.put("address", hotel.getAddress());
            map.put("city", hotel.getCity());
            map.put("starRating", hotel.getStarRating());
            map.put("description", hotel.getDescription());
            map.put("surroundings", hotel.getSurroundings());
            map.put("promotionInfo", hotel.getPromotionInfo());
            map.put("facilities", hotel.getFacilities());
            map.put("tags", hotel.getTags());
            map.put("openDate", hotel.getOpenDate());
            map.put("status", hotel.getStatus());
            map.put("merchantId", hotel.getMerchantId());
            map.put("images", hotel.getImages());
            map.put("coverImage", hotel.getCoverImage());
            map.put("minPrice", hotel.getMinPrice());
            map.put("rejectReason", hotel.getRejectReason());
            // 添加点评数量和收藏量（供Android端使用）
            map.put("reviewCount", reviewService.getReviewCountByHotelId(hotel.getId()));
            map.put("favoriteCount", favoriteService.getFavoriteCountByHotelId(hotel.getId()));
            return map;
        }).collect(Collectors.toList());
        
        Page<Map<String, Object>> resultPage = new Page<>(hotelPage.getCurrent(), hotelPage.getSize(), hotelPage.getTotal());
        resultPage.setRecords(hotelList);
        return Result.success(resultPage);
    }

    // Public: Hotel Detail
    @GetMapping("/detail/{id}")
    public Result<Map<String, Object>> detail(@PathVariable Integer id) {
        Hotel hotel = hotelService.getById(id);
        List<RoomType> rooms = roomTypeService.list(new QueryWrapper<RoomType>().eq("hotel_id", id));
        Map<String, Object> map = new HashMap<>();
        map.put("hotel", hotel);
        map.put("rooms", rooms);
        return Result.success(map);
    }

    // Merchant: Add/Update Hotel
    @PostMapping("/save")
    public Result<Object> save(@RequestBody Hotel hotel) {
        // Any edit or new save by merchant resets status to Auditing (0)
        // Unless it is a status toggle to OFFLINE (2), which we might allow merchant to do directly?
        // But requirement says "edit/publish needs audit".
        // If merchant sets status to 2 (Offline), it's fine. 
        // If merchant sets status to 1 (Online) -> Must go to 0 (Audit).
        // If merchant updates info -> Must go to 0 (Audit).
        
        // We can check the previous status if needed, but simple logic:
        // If explicitly setting to Offline (2), allow it.
        // Otherwise (status 0, 1, or null), force to 0.
        
        if (hotel.getStatus() != null && hotel.getStatus() == 2) {
             // Keep it as 2 (Offline)
        } else {
             hotel.setStatus(0); // Force to Auditing
        }
        
        hotelService.saveOrUpdate(hotel);
        return Result.success();
    }

    // Merchant: List My Hotels
    @GetMapping("/my-list")
    public Result<Page<Map<String, Object>>> myList(@RequestParam(defaultValue = "1") Integer pageNum,
                                      @RequestParam(defaultValue = "10") Integer pageSize,
                                      @RequestParam Integer merchantId) {
        Page<Hotel> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Hotel> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("merchant_id", merchantId);
        Page<Hotel> hotelPage = hotelService.page(page, queryWrapper);
        
        // 转换为包含统计信息的Map列表
        List<Map<String, Object>> hotelList = hotelPage.getRecords().stream().map(hotel -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", hotel.getId());
            map.put("name", hotel.getName());
            map.put("nameEn", hotel.getNameEn());
            map.put("address", hotel.getAddress());
            map.put("city", hotel.getCity());
            map.put("starRating", hotel.getStarRating());
            map.put("description", hotel.getDescription());
            map.put("surroundings", hotel.getSurroundings());
            map.put("promotionInfo", hotel.getPromotionInfo());
            map.put("facilities", hotel.getFacilities());
            map.put("tags", hotel.getTags());
            map.put("openDate", hotel.getOpenDate());
            map.put("status", hotel.getStatus());
            map.put("merchantId", hotel.getMerchantId());
            map.put("images", hotel.getImages());
            map.put("coverImage", hotel.getCoverImage());
            map.put("minPrice", hotel.getMinPrice());
            map.put("rejectReason", hotel.getRejectReason());
            // 添加点评数量和收藏量
            map.put("reviewCount", reviewService.getReviewCountByHotelId(hotel.getId()));
            map.put("favoriteCount", favoriteService.getFavoriteCountByHotelId(hotel.getId()));
            return map;
        }).collect(Collectors.toList());
        
        Page<Map<String, Object>> resultPage = new Page<>(hotelPage.getCurrent(), hotelPage.getSize(), hotelPage.getTotal());
        resultPage.setRecords(hotelList);
        return Result.success(resultPage);
    }

    // Admin: List All Hotels (for audit)
    @GetMapping("/admin/list")
    public Result<Page<Map<String, Object>>> adminList(@RequestParam(defaultValue = "1") Integer pageNum,
                                         @RequestParam(defaultValue = "10") Integer pageSize,
                                         @RequestParam(required = false) Integer status,
                                         @RequestParam(required = false) String name) {
        Page<Hotel> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Hotel> queryWrapper = new QueryWrapper<>();
        if (status != null) {
            queryWrapper.eq("status", status);
        }
        if (StrUtil.isNotBlank(name)) {
            queryWrapper.like("name", name);
        }
        Page<Hotel> hotelPage = hotelService.page(page, queryWrapper);
        
        // 转换为包含统计信息的Map列表
        List<Map<String, Object>> hotelList = hotelPage.getRecords().stream().map(hotel -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", hotel.getId());
            map.put("name", hotel.getName());
            map.put("nameEn", hotel.getNameEn());
            map.put("address", hotel.getAddress());
            map.put("city", hotel.getCity());
            map.put("starRating", hotel.getStarRating());
            map.put("description", hotel.getDescription());
            map.put("surroundings", hotel.getSurroundings());
            map.put("promotionInfo", hotel.getPromotionInfo());
            map.put("facilities", hotel.getFacilities());
            map.put("tags", hotel.getTags());
            map.put("openDate", hotel.getOpenDate());
            map.put("status", hotel.getStatus());
            map.put("merchantId", hotel.getMerchantId());
            map.put("images", hotel.getImages());
            map.put("coverImage", hotel.getCoverImage());
            map.put("minPrice", hotel.getMinPrice());
            map.put("rejectReason", hotel.getRejectReason());
            // 添加点评数量和收藏量
            map.put("reviewCount", reviewService.getReviewCountByHotelId(hotel.getId()));
            map.put("favoriteCount", favoriteService.getFavoriteCountByHotelId(hotel.getId()));
            return map;
        }).collect(Collectors.toList());
        
        Page<Map<String, Object>> resultPage = new Page<>(hotelPage.getCurrent(), hotelPage.getSize(), hotelPage.getTotal());
        resultPage.setRecords(hotelList);
        return Result.success(resultPage);
    }

    // Admin: Audit Hotel
    @PostMapping("/audit")
    public Result<Object> audit(@RequestBody Hotel hotel) {
        // expect hotel.id and hotel.status
        Hotel dbHotel = hotelService.getById(hotel.getId());
        dbHotel.setStatus(hotel.getStatus());
        if(hotel.getStatus() == 3) {
            dbHotel.setRejectReason(hotel.getRejectReason());
        }
        hotelService.updateById(dbHotel);
        return Result.success();
    }
}
