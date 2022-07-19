package cn.itcast.hotel.web;

import cn.itcast.hotel.pojo.HotelDoc;
import cn.itcast.hotel.pojo.PageResult;
import cn.itcast.hotel.pojo.RequestParams;
import cn.itcast.hotel.service.IHotelService;
import cn.itcast.hotel.service.impl.HotelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author 晓蝈
 * @version 1.0
 */
@RestController
@RequestMapping("/hotel")
public class HotelController {

    /*
    @Autowired
    private IHotelService hotelService;

    @PostMapping("/list")
    public PageResult list(@RequestBody RequestParams params) {
        return hotelService.search(params);
    }

    @PostMapping("/filters")
    public Map<String, List<String>> filters(@RequestBody RequestParams params) {
        return hotelService.filters(params);
    }

    @GetMapping("suggestion")
    public List<String> getSuggestions(@RequestParam("key") String prefix) {
        return hotelService.getSuggestions(prefix);
    }
     */


    @Autowired
    private IHotelService hotelService;

    @PostMapping("/list")
    public PageResult list(@RequestBody RequestParams params) {
        return hotelService.search(params);
    }

    @PostMapping("/filters")
    public Map<String, List<String>> filter(@RequestBody RequestParams params) {
        return hotelService.filters(params);
    }

    @GetMapping("suggestion")
    public List<String> getSuggestion(@RequestParam("key") String prefix) {
        return hotelService.getSuggestion(prefix);
    }
}
