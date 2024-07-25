package com.example.samuraitravel.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.samuraitravel.entity.Favorite;
import com.example.samuraitravel.entity.House;
import com.example.samuraitravel.entity.Review;
import com.example.samuraitravel.entity.User;
import com.example.samuraitravel.form.ReservationInputForm;
import com.example.samuraitravel.security.UserDetailsImpl;
import com.example.samuraitravel.service.FavoriteService;
import com.example.samuraitravel.service.HouseService;
import com.example.samuraitravel.service.ReviewService;

@Controller
@RequestMapping("/houses")
public class HouseController {
    private final HouseService houseService;
    private final ReviewService reviewService;
    private final FavoriteService favoriteService;

    public HouseController(HouseService houseService, ReviewService reviewService, FavoriteService favoriteService) {
        this.houseService = houseService;
        this.reviewService = reviewService;
        this.favoriteService = favoriteService;
    }

    @GetMapping
    public String index(@RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "area", required = false) String area,
            @RequestParam(name = "price", required = false) Integer price,
            @RequestParam(name = "order", required = false) String order,
            @PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.ASC) Pageable pageable,
            Model model) {
        Page<House> housePage;

        if (keyword != null && !keyword.isEmpty()) {
            if (order != null && order.equals("priceAsc")) {
                housePage = houseService.findHousesByNameLikeOrAddressLikeOrderByPriceAsc(keyword, keyword, pageable);
            } else {
                housePage = houseService.findHousesByNameLikeOrAddressLikeOrderByCreatedAtDesc(keyword, keyword,
                        pageable);
            }
        } else if (area != null && !area.isEmpty()) {
            if (order != null && order.equals("priceAsc")) {
                housePage = houseService.findHousesByAddressLikeOrderByPriceAsc(area, pageable);
            } else {
                housePage = houseService.findHousesByAddressLikeOrderByCreatedAtDesc(area, pageable);
            }
        } else if (price != null) {
            if (order != null && order.equals("priceAsc")) {
                housePage = houseService.findHousesByPriceLessThanEqualOrderByPriceAsc(price, pageable);
            } else {
                housePage = houseService.findHousesByPriceLessThanEqualOrderByCreatedAtDesc(price, pageable);
            }
        } else {
            if (order != null && order.equals("priceAsc")) {
                housePage = houseService.findAllHousesByOrderByPriceAsc(pageable);
            } else {
                housePage = houseService.findAllHousesByOrderByCreatedAtDesc(pageable);
            }
        }

        model.addAttribute("housePage", housePage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("area", area);
        model.addAttribute("price", price);
        model.addAttribute("order", order);

        return "houses/index";
    }

    @GetMapping("/{id}")
    public String show(@PathVariable(name = "id") Integer id,
            @AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
            RedirectAttributes redirectAttributes,
            Model model) {
        Optional<House> optionalHouse = houseService.findHouseById(id);

        if (optionalHouse.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "民宿が存在しません。");
            return "redirect:/houses";
        }

        House house = optionalHouse.get();
        boolean hasUserAlreadyReviewed = false;
        Favorite favorite = null;
        boolean isFavorite = false;

        if (userDetailsImpl != null) {
            User user = userDetailsImpl.getUser();
            hasUserAlreadyReviewed = reviewService.hasUserAlreadyReviewed(house, user);
            isFavorite = favoriteService.isFavorite(house, user);

            if (isFavorite) {
                favorite = favoriteService.findFavoriteByHouseAndUser(house, user);
            }
        }

        List<Review> newReviews = reviewService.findTop6ReviewsByHouseOrderByCreatedAtDesc(house);
        long totalReviewCount = reviewService.countReviewsByHouse(house);

        model.addAttribute("house", house);
        model.addAttribute("reservationInputForm", new ReservationInputForm());
        model.addAttribute("hasUserAlreadyReviewed", hasUserAlreadyReviewed);
        model.addAttribute("newReviews", newReviews);
        model.addAttribute("totalReviewCount", totalReviewCount);
        model.addAttribute("favorite", favorite);
        model.addAttribute("isFavorite", isFavorite);

        return "houses/show";
    }
}
