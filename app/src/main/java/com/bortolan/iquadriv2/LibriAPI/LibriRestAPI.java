package com.bortolan.iquadriv2.LibriAPI;

import com.bortolan.iquadriv2.Interfaces.Libri.Announcement;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface LibriRestAPI {

    @GET("/announcements/{city}")
    Observable<List<Announcement>> getAnnouncements(@Path("city") String city);
}
