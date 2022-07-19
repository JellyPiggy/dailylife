package cn.itcast.hotel.pojo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author 晓蝈
 * @version 1.0
 */
@Data
@NoArgsConstructor
public class PageResult {
    private Long total;
    private List<HotelDoc> hotels;

    public PageResult(Long total, List<HotelDoc> hotelDocs) {
        this.total = total;
        this.hotels = hotelDocs;
    }
}
