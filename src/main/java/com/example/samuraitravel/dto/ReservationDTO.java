package com.example.samuraitravel.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
/*
 * フォームから送信されたデータを受け渡すためのオブジェクト
 * ユーザーのidは登録時にUserDetailsImpleオブジェクトから取得する
 */
@Data
@AllArgsConstructor
public class ReservationDTO {
    private Integer houseId;

    private LocalDate checkinDate;

    private LocalDate checkoutDate;

    private Integer numberOfPeople;

    private Integer amount;
}

