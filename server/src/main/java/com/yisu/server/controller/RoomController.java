package com.yisu.server.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yisu.server.common.Result;
import com.yisu.server.entity.RoomType;
import com.yisu.server.service.RoomTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/room")
@CrossOrigin
public class RoomController {

    @Autowired
    private RoomTypeService roomTypeService;

    @GetMapping("/list/{hotelId}")
    public Result<List<RoomType>> list(@PathVariable Integer hotelId) {
        return Result.success(roomTypeService.list(new QueryWrapper<RoomType>().eq("hotel_id", hotelId)));
    }

    @PostMapping("/save")
    public Result<Object> save(@RequestBody RoomType roomType) {
        roomTypeService.saveOrUpdate(roomType);
        return Result.success();
    }
    
    @DeleteMapping("/{id}")
    public Result<Object> delete(@PathVariable Integer id) {
        roomTypeService.removeById(id);
        return Result.success();
    }
}
