package api;

import model.auth.AuthRequest;
import model.auth.AuthResponse;
import model.booking.*;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

public interface RestfulBookerApi {

    ///Auth
    @POST("auth")
    Call<AuthResponse> createToken(@Body AuthRequest body);

    /// Booking
    @GET("booking")
    Call<List<GetBookingIDsResponse>> getBookingIDs(@Query("firstname") String firstname,
                                                    @Query("lastname") String lastname,
                                                    @Query("checkin") String checkin,
                                                    @Query("checkout") String checkout);

    @GET("booking/{id}")
    Call<BookingResponse> getBooking(@Header("Accept") String accept,
                                     @Path("id") int id);

    @POST("booking")
    Call<CreateResponse> createBooking(@Header("Content-Type") String contentType,
                                       @Header("Accept") String accept,
                                       @Body CreateUpdateRequest body
    );

    @PUT("booking/{id}")
    Call<BookingResponse> updateBookingToken(@Header("Content-Type") String contentType,
                                        @Header("Accept") String accept,
                                        @Header("Cookie") String token,
                                        @Path("id") int id,
                                        @Body CreateUpdateRequest body);

    @PUT("booking/{id}")
    Call<BookingResponse> updateBookingAuth(@Header("Content-Type") String contentType,
                                             @Header("Accept") String accept,
                                             @Header("Authorization") String auth,
                                             @Path("id") int id,
                                             @Body CreateUpdateRequest body);

    @PATCH("booking/{id}")
    Call<BookingResponse> partialUpdateBookingToken(@Header("Content-Type") String contentType,
                                            @Header("Accept") String accept,
                                            @Header("Cookie") String token,
                                            @Path("id") int id,
                                            @Body PartialUpdateRequest body);

    @PATCH("booking/{id}")
    Call<BookingResponse> partialUpdateBookingAuth(@Header("Content-Type") String contentType,
                                            @Header("Accept") String accept,
                                            @Header("Authorization") String auth,
                                            @Path("id") int id,
                                            @Body PartialUpdateRequest body);

    @DELETE("booking/{id}")
    Call<ResponseBody> deleteBookingToken(@Header("Cookie") String token,
                                          @Path("id") int id);

    @DELETE("booking/{id}")
    Call<ResponseBody> deleteBookingAuth(@Header("Authorization") String auth,
                                         @Path("id") int id);

    @GET("ping")
    Call<ResponseBody> ping();
}
