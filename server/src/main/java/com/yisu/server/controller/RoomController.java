package com.yisu.server.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yisu.server.common.Result;
import com.yisu.server.entity.Hotel;
import com.yisu.server.entity.RoomType;
import com.yisu.server.service.HotelService;
import com.yisu.server.service.RoomTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/room")
@CrossOrigin
public class RoomController {

    @Autowired
    private RoomTypeService roomTypeService;

    @Autowired
    private HotelService hotelService;

    @GetMapping("/list/{hotelId}")
    public Result<List<RoomType>> list(@PathVariable Integer hotelId) {
        return Result.success(roomTypeService.list(new QueryWrapper<RoomType>().eq("hotel_id", hotelId)));
    }

    @PostMapping("/save")
    public Result<Object> save(@RequestBody RoomType roomType) {
        roomTypeService.saveOrUpdate(roomType);
        refreshHotelMinPrice(roomType.getHotelId());
        return Result.success();
    }
    
    @DeleteMapping("/{id}")
    public Result<Object> delete(@PathVariable Integer id) {
        RoomType roomType = roomTypeService.getById(id);
        roomTypeService.removeById(id);
        if (roomType != null) {
            refreshHotelMinPrice(roomType.getHotelId());
        }
        return Result.success();
    }

    /**
     * 计算并回写酒店最低起步价：取该酒店“上架(status=1)”且“库存>0”的房型最低价。
     * 若无符合条件房型，则回写为 0。
     */
    private void refreshHotelMinPrice(Integer hotelId) {
        if (hotelId == null) {
            return;
        }
        Object minObj = roomTypeService.getBaseMapper().selectObjs(
                new QueryWrapper<RoomType>()
                        .select("min(price) as min_price")
                        .eq("hotel_id", hotelId)
                        .eq("status", 1)
                        .gt("stock", 0)
        ).stream().findFirst().orElse(null);

        BigDecimal minPrice = BigDecimal.ZERO;
        if (minObj instanceof BigDecimal) {
            minPrice = (BigDecimal) minObj;
        } else if (minObj != null) {
            try {
                minPrice = new BigDecimal(minObj.toString());
            } catch (Exception ignored) {
            }
        }

        Hotel hotel = new Hotel();
        hotel.setId(hotelId);
        hotel.setMinPrice(minPrice);
        hotelService.updateById(hotel);
    }
}
