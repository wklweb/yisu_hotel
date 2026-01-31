package com.yisu.server.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yisu.server.entity.RoomType;
import com.yisu.server.mapper.RoomTypeMapper;
import com.yisu.server.service.RoomTypeService;
import org.springframework.stereotype.Service;

@Service
public class RoomTypeServiceImpl extends ServiceImpl<RoomTypeMapper, RoomType> implements RoomTypeService {
}
