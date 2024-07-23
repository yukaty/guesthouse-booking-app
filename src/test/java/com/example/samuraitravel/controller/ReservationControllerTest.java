package com.example.samuraitravel.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.example.samuraitravel.dto.ReservationDTO;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ReservationControllerTest {
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    public void 未ログインの場合は会員用の予約一覧ページからログインページにリダイレクトする() throws Exception {
        mockMvc.perform(get("/reservations"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    @WithUserDetails("taro.samurai@example.com")
    public void ログイン済みの場合は会員用の予約一覧ページが正しく表示される() throws Exception {
        mockMvc.perform(get("/reservations"))
                .andExpect(status().isOk())
                .andExpect(view().name("reservations/index"));
    }

    @Test
    public void 未ログインの場合は予約フォームを送信せずにログインページにリダイレクトする() throws Exception {
        mockMvc.perform(post("/houses/1/reservations/input").with(csrf())
                .param("checkinDate", "2024-04-01")
                .param("checkoutDate", "2024-04-02")
                .param("numberOfPeople", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    @WithUserDetails("taro.samurai@example.com")
    public void ログイン済みの場合は予約フォームの送信後に予約内容の確認ページにリダイレクトする() throws Exception {
        mockMvc.perform(post("/houses/1/reservations/input").with(csrf())
                .param("checkinDate", "2024-04-01")
                .param("checkoutDate", "2024-04-02")
                .param("numberOfPeople", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/reservations/confirm"));
    }

    @Test
    public void 未ログインの場合は予約内容の確認ページからログインページにリダイレクトする() throws Exception {
        // セッションを作成し、ReservationDTOオブジェクトを保存する
        MockHttpSession mockHttpSession = new MockHttpSession();
        ReservationDTO reservationDTO = new ReservationDTO(1, LocalDate.parse("2024-04-01"),
                LocalDate.parse("2024-04-02"), 1, 6000);
        mockHttpSession.setAttribute("reservationDTO", reservationDTO);

        // 作成したセッションを使用してHTTPリクエストを送信する
        mockMvc.perform(get("/reservations/confirm").session(mockHttpSession))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    @WithUserDetails("taro.samurai@example.com")
    public void ログイン済みの場合は予約内容の確認ページが正しく表示される() throws Exception {
        // セッションを作成し、ReservationDTOオブジェクトを保存する
        MockHttpSession mockHttpSession = new MockHttpSession();
        ReservationDTO reservationDTO = new ReservationDTO(1, LocalDate.parse("2024-04-01"),
                LocalDate.parse("2024-04-02"), 1, 6000);
        mockHttpSession.setAttribute("reservationDTO", reservationDTO);

        // 作成したセッションを使用してHTTPリクエストを送信する
        mockMvc.perform(get("/reservations/confirm").session(mockHttpSession))
                .andExpect(status().isOk())
                .andExpect(view().name("reservations/confirm"));
    }


}
