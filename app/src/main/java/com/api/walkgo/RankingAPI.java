package com.api.walkgo;

import com.api.walkgo.models.RankingEntry;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface RankingAPI {

    @GET("api/ranking/semana")
    Call<List<RankingEntry>> GetRankingSemana();
}