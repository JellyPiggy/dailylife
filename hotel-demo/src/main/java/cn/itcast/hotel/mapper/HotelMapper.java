package cn.itcast.hotel.mapper;

import cn.itcast.hotel.pojo.Hotel;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

//@Mapper  启动类配置了包扫描
public interface HotelMapper extends BaseMapper<Hotel> {
}
