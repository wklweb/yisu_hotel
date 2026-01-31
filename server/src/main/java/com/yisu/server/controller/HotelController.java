package com.yisu.server.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yisu.server.common.Result;
import com.yisu.server.entity.Hotel;
import com.yisu.server.entity.RoomType;
import com.yisu.server.service.HotelService;
import com.yisu.server.service.RoomTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/hotel")
@CrossOrigin
public class HotelController {

    @Autowired
    private HotelService hotelService;
    
    @Autowired
    private RoomTypeService roomTypeService;

    // Public: List Hotels (Search)
    @GetMapping("/list")
    public Result<Page<Hotel>> list(@RequestParam(defaultValue = "1") Integer pageNum,
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
        return Result.success(hotelService.page(page, queryWrapper));
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
        if (hotel.getId() == null) {
            hotel.setStatus(0); // Default to auditing
        }
        hotelService.saveOrUpdate(hotel);
        return Result.success();
    }

    // Merchant: List My Hotels
    @GetMapping("/my-list")
    public Result<Page<Hotel>> myList(@RequestParam(defaultValue = "1") Integer pageNum,
                                      @RequestParam(defaultValue = "10") Integer pageSize,
                                      @RequestParam Integer merchantId) {
        Page<Hotel> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Hotel> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("merchant_id", merchantId);
        return Result.success(hotelService.page(page, queryWrapper));
    }

    // Admin: List All Hotels (for audit)
    @GetMapping("/admin/list")
    public Result<Page<Hotel>> adminList(@RequestParam(defaultValue = "1") Integer pageNum,
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
        return Result.success(hotelService.page(page, queryWrapper));
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
