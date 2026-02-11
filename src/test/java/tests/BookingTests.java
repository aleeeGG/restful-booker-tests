package tests;

import data.BookingData;
import model.booking.*;
import okhttp3.Credentials;
import okhttp3.ResponseBody;
import org.junit.jupiter.api.Test;
import retrofit2.Response;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class BookingTests extends BaseTest {
    protected final String CONTENT_TYPE = "application/json";
    protected final String ACCEPT = "application/json";
    protected final String LOGIN = "admin";
    protected final String PASS = "password123";
    protected final String WRONG = "wrong";

    /// getBookingIDs
    @Test
    void getBookingIDs200() throws Exception {
        Response<List<GetBookingIDsResponse>> response = getBookingListIds(null,
                null,
                null,
                null);

        assertThat(response.code()).isEqualTo(200);
        assertThat(response.body()).isNotNull();
        assertThat(response.body()).isNotEmpty();
        response.body().forEach(id ->
                assertThat(id.bookingid).isPositive());
        assertThat(response.body())
                .extracting(b -> b.bookingid)
                .doesNotHaveDuplicates();
    }

    @Test
    void getNameBookingIDs200() throws Exception {
        create(BookingData.defaultBooking());

        Response<List<GetBookingIDsResponse>> response =
                getBookingListIds(
                        "Vlad",
                        null,
                        null,
                        null);

        int id = response.body().get(0).bookingid;

        BookingResponse booking =
                getBookingById(id);

        assertThat(response.code()).isEqualTo(200);
        assertThat(response.body()).isNotNull();
        assertThat(response.body()).isNotEmpty();
        assertThat(booking.firstname).isEqualTo("Vlad");
    }

    @Test
    void getBookingIDsNotFound() throws Exception {

        Response<List<GetBookingIDsResponse>> response =
                getBookingListIds(
                        "NonExistingName",
                        null,
                        null,
                        null);

        assertThat(response.code()).isEqualTo(200);
        assertThat(response.body()).isEmpty();
    }

    @Test
    void getFullBookingIDs200() throws Exception {

        create(BookingData.defaultBooking());

        Response<List<GetBookingIDsResponse>> response =
                getBookingListIds(
                        "Vlad",
                        "Tretyakov",
                        "2024-05-10",
                        "2024-09-01");

        assertThat(response.code()).isEqualTo(200);
        assertThat(response.body()).isNotNull();
        assertThat(response.body()).isNotEmpty();
    }

    /// getBooking
    @Test
    void createThenGet() throws Exception {

        CreateResponse created =
                create(BookingData.defaultBooking());

        BookingResponse booking =
                getBookingById(created.bookingid);

        assertThat(booking.firstname)
                .isEqualTo(created.booking.firstname);

        assertThat(booking.lastname)
                .isEqualTo(created.booking.lastname);

        assertThat(booking.totalprice)
                .isEqualTo(created.booking.totalprice);

        assertThat(booking.depositpaid)
                .isEqualTo(created.booking.depositpaid);

        assertThat(booking.bookingdates.checkin)
                .isEqualTo(created.booking.bookingdates.checkin);

        assertThat(booking.bookingdates.checkout)
                .isEqualTo(created.booking.bookingdates.checkout);


        assertThat(booking.additionalneeds)
                .isEqualTo(created.booking.additionalneeds);
    }


    @Test
    void getBookingNegative() throws Exception {

        Response<BookingResponse> response =
                api.getBooking(
                        ACCEPT,
                        9999999).execute();


        assertThat(response.code()).isEqualTo(404);
        assertThat(response.errorBody()).isNotNull();
    }

    /// create
    @Test
    void createBooking200() throws Exception {

        CreateResponse created =
                create(BookingData.defaultBooking());

        assertThat(created.bookingid).isPositive();
        assertThat(created.booking.firstname).isEqualTo("Vlad");
        assertThat(created.booking.lastname).isEqualTo("Tretyakov");
    }

    @Test
    void createBookingNullFirstname() throws Exception {

        CreateUpdateRequest body =
                BookingData.defaultBooking();

        body.firstname = null;

        Response<CreateResponse> response =
                api.createBooking(
                        CONTENT_TYPE,
                        ACCEPT,
                        body).execute();

        assertThat(response.code()).isIn(400, 500);
    }

    @Test
    void createBookingWrongDate() throws Exception {

        CreateUpdateRequest body =
                BookingData.defaultBooking();

        body.bookingdates =
                new BookingDates(
                        "10-05-2024",
                        "01-09-2024"
                );

        Response<CreateResponse> response =
                api.createBooking(
                        CONTENT_TYPE,
                        ACCEPT,
                        body).execute();

        assertThat(response.code()).isIn(400, 500);
    }

    @Test
    void createBookingCheckoutBeforeCheckin() throws Exception {

        CreateUpdateRequest body =
                BookingData.defaultBooking();

        body.bookingdates =
                new BookingDates(
                        "2024-09-01",
                        "2024-05-10"
                );

        Response<CreateResponse> response =
                api.createBooking(
                        CONTENT_TYPE,
                        ACCEPT,
                        body).execute();

        assertThat(response.code()).isIn(400, 500);
    }

    @Test
    void createBookingNegativePrice() throws Exception {

        CreateUpdateRequest body =
                BookingData.defaultBooking();

        body.totalprice = -100;

        Response<CreateResponse> response =
                api.createBooking(
                        CONTENT_TYPE,
                        ACCEPT,
                        body).execute();

        assertThat(response.code()).isIn(400, 500);
    }

    /// UpdateBooking
    @Test
    void update200Token() throws Exception {

        String token = createToken(LOGIN, PASS).body().token;

        CreateResponse created =
                create(BookingData.defaultBooking());

        CreateUpdateRequest body =
                BookingData.updateBooking();

        Response<BookingResponse> response =
                api.updateBookingToken(
                        CONTENT_TYPE,
                        ACCEPT,
                        "token=" + token,
                        created.bookingid,
                        body).execute();

        assertThat(response.code()).isEqualTo(200);
        assertThat(response.body()).isNotNull();

        assertThat(response.body().firstname)
                .isEqualTo(body.firstname);

        assertThat(response.body().lastname)
                .isEqualTo(body.lastname);

        assertThat(response.body().totalprice)
                .isEqualTo(body.totalprice);

        assertThat(response.body().depositpaid)
                .isEqualTo(body.depositpaid);

        assertThat(response.body().bookingdates.checkin)
                .isEqualTo(body.bookingdates.checkin);

        assertThat(response.body().bookingdates.checkout)
                .isEqualTo(body.bookingdates.checkout);

        assertThat(response.body().additionalneeds)
                .isEqualTo(body.additionalneeds);

    }

    @Test
    void updateWrongPass403Token() throws Exception {

        String token = createToken(LOGIN, WRONG).body().token;

        CreateResponse created =
                create(BookingData.defaultBooking());

        CreateUpdateRequest body =
                BookingData.updateBooking();

        Response<BookingResponse> response =
                api.updateBookingToken(
                        CONTENT_TYPE,
                        ACCEPT,
                        "token=" + token,
                        created.bookingid,
                        body).execute();

        assertThat(response.code()).isEqualTo(403);
        assertThat(response.errorBody()).isNotNull();
    }

    @Test
    void updateWrongLogin403Token() throws Exception {

        String token = createToken(WRONG, PASS).body().token;

        CreateResponse created =
                create(BookingData.defaultBooking());

        CreateUpdateRequest body =
                BookingData.updateBooking();

        Response<BookingResponse> response =
                api.updateBookingToken(
                        CONTENT_TYPE,
                        ACCEPT,
                        "token=" + token,
                        created.bookingid,
                        body).execute();

        assertThat(response.code()).isEqualTo(403);
        assertThat(response.errorBody()).isNotNull();
    }

    @Test
    void updateNoneAuth403Token() throws Exception {

        CreateResponse created =
                create(BookingData.defaultBooking());

        CreateUpdateRequest body =
                BookingData.updateBooking();

        Response<BookingResponse> response =
                api.updateBookingToken(
                        CONTENT_TYPE,
                        ACCEPT,
                        null,
                        created.bookingid,
                        body).execute();

        assertThat(response.code()).isEqualTo(403);
        assertThat(response.errorBody()).isNotNull();
    }

    @Test
    void update200Auth() throws Exception {

        String auth =
                Credentials.basic(LOGIN, PASS);

        CreateResponse created =
                create(BookingData.defaultBooking());

        CreateUpdateRequest body =
                BookingData.updateBooking();

        Response<BookingResponse> response =
                api.updateBookingAuth(
                        CONTENT_TYPE,
                        ACCEPT,
                        auth,
                        created.bookingid,
                        body).execute();

        assertThat(response.code()).isEqualTo(200);
        assertThat(response.body()).isNotNull();

        assertThat(response.body().firstname)
                .isEqualTo(body.firstname);

        assertThat(response.body().lastname)
                .isEqualTo(body.lastname);

        assertThat(response.body().totalprice)
                .isEqualTo(body.totalprice);

        assertThat(response.body().depositpaid)
                .isEqualTo(body.depositpaid);

        assertThat(response.body().bookingdates.checkin)
                .isEqualTo(body.bookingdates.checkin);

        assertThat(response.body().bookingdates.checkout)
                .isEqualTo(body.bookingdates.checkout);

        assertThat(response.body().additionalneeds)
                .isEqualTo(body.additionalneeds);
    }

    @Test
    void updateWrongPass403Auth() throws Exception {

        String auth =
                Credentials.basic(LOGIN, WRONG);

        CreateResponse created =
                create(BookingData.defaultBooking());

        CreateUpdateRequest body =
                BookingData.updateBooking();

        Response<BookingResponse> response =
                api.updateBookingAuth(
                        CONTENT_TYPE,
                        ACCEPT,
                        auth,
                        created.bookingid,
                        body).execute();

        assertThat(response.code()).isEqualTo(403);
        assertThat(response.errorBody()).isNotNull();
    }

    @Test
    void updateWrongLogin403Auth() throws Exception {

        String auth =
                Credentials.basic(WRONG, PASS);

        CreateResponse created =
                create(BookingData.defaultBooking());

        CreateUpdateRequest body =
                BookingData.updateBooking();

        Response<BookingResponse> response =
                api.updateBookingAuth(
                        CONTENT_TYPE,
                        ACCEPT,
                        auth,
                        created.bookingid,
                        body).execute();

        assertThat(response.code()).isEqualTo(403);
        assertThat(response.errorBody()).isNotNull();
    }

    @Test
    void updateNoneAuth403Auth() throws Exception {

        CreateResponse created =
                create(BookingData.defaultBooking());

        CreateUpdateRequest body =
                BookingData.updateBooking();

        Response<BookingResponse> response =
                api.updateBookingAuth(
                        CONTENT_TYPE,
                        ACCEPT,
                        null,
                        created.bookingid,
                        body).execute();

        assertThat(response.code()).isEqualTo(403);
        assertThat(response.errorBody()).isNotNull();
    }

    @Test
    void updateWrongID405Token() throws Exception {

        String token = createToken(LOGIN, PASS).body().token;

        CreateResponse created =
                create(BookingData.defaultBooking());

        CreateUpdateRequest body =
                BookingData.updateBooking();

        Response<BookingResponse> response =
                api.updateBookingToken(
                        CONTENT_TYPE,
                        ACCEPT,
                        "token=" + token,
                        999999,
                        body).execute();

        assertThat(response.code()).isEqualTo(405);
        assertThat(response.errorBody()).isNotNull();
    }

    @Test
    void updateWrongID405Auth() throws Exception {

        String auth =
                Credentials.basic(LOGIN,PASS);

        CreateUpdateRequest body =
                BookingData.updateBooking();

        Response<BookingResponse> response =
                api.updateBookingAuth(
                        CONTENT_TYPE,
                        ACCEPT,
                        auth,
                        999999,
                        body).execute();

        assertThat(response.code()).isEqualTo(405);
        assertThat(response.errorBody()).isNotNull();
    }

    /// PATCH
    @Test
    void partialUpdate200Token() throws Exception {

        String token = createToken(LOGIN, PASS).body().token;

        CreateResponse created =
                create(BookingData.defaultBooking());

        PartialUpdateRequest body = new PartialUpdateRequest();
        body.firstname = "UpdatedFirst";
        body.lastname = "UpdatedLast";

        Response<BookingResponse> response =
                api.partialUpdateBookingToken(
                        CONTENT_TYPE,
                        ACCEPT,
                        "token=" + token,
                        created.bookingid,
                        body).execute();

        assertThat(response.code()).isEqualTo(200);
        assertThat(response.body()).isNotNull();

        assertThat(response.body().firstname)
                .isEqualTo(body.firstname);

        assertThat(response.body().lastname)
                .isEqualTo(body.lastname);
    }

    @Test
    void partialUpdateWrongPass403Token() throws Exception {

        String token = createToken(LOGIN, WRONG).body().token;

        CreateResponse created =
                create(BookingData.defaultBooking());

        PartialUpdateRequest body = new PartialUpdateRequest();
        body.firstname = "UpdatedFirst";
        body.lastname = "UpdatedLast";

        Response<BookingResponse> response =
                api.partialUpdateBookingToken(
                        CONTENT_TYPE,
                        ACCEPT,
                        "token=" + token,
                        created.bookingid,
                        body).execute();

        assertThat(response.code()).isEqualTo(403);
        assertThat(response.errorBody()).isNotNull();
    }

    @Test
    void partialUpdateWrongLogin403Token() throws Exception {

        String token = createToken(WRONG, PASS).body().token;
        CreateResponse created =
                create(BookingData.defaultBooking());

        PartialUpdateRequest body = new PartialUpdateRequest();
        body.firstname = "UpdatedFirst";

        Response<BookingResponse> response =
                api.partialUpdateBookingToken(
                        CONTENT_TYPE,
                        ACCEPT,
                        "token=" + token,
                        created.bookingid,
                        body).execute();

        assertThat(response.code()).isEqualTo(403);
        assertThat(response.errorBody()).isNotNull();
    }

    @Test
    void partialUpdateNoneAuth403Token() throws Exception {

        CreateResponse created =
                create(BookingData.defaultBooking());

        PartialUpdateRequest body = new PartialUpdateRequest();
        body.firstname = "UpdatedFirst";

        Response<BookingResponse> response =
                api.partialUpdateBookingToken(
                        CONTENT_TYPE,
                        ACCEPT,
                        null,
                        created.bookingid,
                        body).execute();

        assertThat(response.code()).isEqualTo(403);
        assertThat(response.errorBody()).isNotNull();
    }

    @Test
    void partialUpdate200Auth() throws Exception {

        String auth =
                Credentials.basic(LOGIN, PASS);

        CreateResponse created =
                create(BookingData.defaultBooking());

        PartialUpdateRequest body = new PartialUpdateRequest();
        body.firstname = "UpdatedFirst";
        body.lastname = "UpdatedLast";

        Response<BookingResponse> response =
                api.partialUpdateBookingAuth(
                        CONTENT_TYPE,
                        ACCEPT,
                        auth,
                        created.bookingid,
                        body).execute();

        assertThat(response.code()).isEqualTo(200);
        assertThat(response.body()).isNotNull();

        assertThat(response.body().firstname)
                .isEqualTo(body.firstname);

        assertThat(response.body().lastname)
                .isEqualTo(body.lastname);

    }

    @Test
    void partialUpdateWrongPass403Auth() throws Exception {

        String auth =
                Credentials.basic(LOGIN, WRONG);

        CreateResponse created =
                create(BookingData.defaultBooking());

        PartialUpdateRequest body = new PartialUpdateRequest();
        body.firstname = "UpdatedFirst";

        Response<BookingResponse> response =
                api.partialUpdateBookingAuth(
                        CONTENT_TYPE,
                        ACCEPT,
                        auth,
                        created.bookingid,
                        body).execute();

        assertThat(response.code()).isEqualTo(403);
        assertThat(response.errorBody()).isNotNull();
    }

    @Test
    void partialUpdateWrongLogin403Auth() throws Exception {

        String auth =
                Credentials.basic(WRONG, PASS);

        CreateResponse created =
                create(BookingData.defaultBooking());

        PartialUpdateRequest body = new PartialUpdateRequest();
        body.firstname = "UpdatedFirst";

        Response<BookingResponse> response =
                api.partialUpdateBookingAuth(
                        CONTENT_TYPE,
                        ACCEPT,
                        auth,
                        created.bookingid,
                        body).execute();

        assertThat(response.code()).isEqualTo(403);
        assertThat(response.errorBody()).isNotNull();
    }

    @Test
    void partialUpdateNoneAuth403Auth() throws Exception {

        CreateResponse created =
                create(BookingData.defaultBooking());

        PartialUpdateRequest body = new PartialUpdateRequest();
        body.firstname = "UpdatedFirst";

        Response<BookingResponse> response =
                api.partialUpdateBookingAuth(
                        CONTENT_TYPE,
                        ACCEPT,
                        null,
                        created.bookingid,
                        body).execute();

        assertThat(response.code()).isEqualTo(403);
        assertThat(response.errorBody()).isNotNull();
    }

    @Test
    void partialUpdateWrongID405Token() throws Exception {

        String token = createToken(LOGIN, PASS).body().token;

        CreateResponse created =
                create(BookingData.defaultBooking());

        PartialUpdateRequest body = new PartialUpdateRequest();
        body.firstname = "UpdatedFirst";

        Response<BookingResponse> response =
                api.partialUpdateBookingToken(
                        CONTENT_TYPE,
                        ACCEPT,
                        "token=" + token,
                        999999,
                        body).execute();

        assertThat(response.code()).isEqualTo(405);
        assertThat(response.errorBody()).isNotNull();
    }

    @Test
    void partialUpdateWrongID405Auth() throws Exception {

        String auth =
                Credentials.basic(LOGIN,PASS);

        PartialUpdateRequest body = new PartialUpdateRequest();
        body.firstname = "UpdatedFirst";

        Response<BookingResponse> response =
                api.partialUpdateBookingAuth(
                        CONTENT_TYPE,
                        ACCEPT,
                        auth,
                        999999,
                        body).execute();

        assertThat(response.code()).isEqualTo(405);
        assertThat(response.errorBody()).isNotNull();
    }

    /// DELETE
    @Test
    void delete200Token() throws Exception {

        String token = createToken(LOGIN, PASS).body().token;

        CreateResponse created =
                create(BookingData.defaultBooking());

        Response<ResponseBody> response =
                api.deleteBookingToken("token=" + token, created.bookingid).execute();
        assertThat(response.code()).isEqualTo(201);

        Response<BookingResponse> get =
                api.getBooking(
                        ACCEPT,
                        created.bookingid
                ).execute();

        assertThat(get.code()).isEqualTo(404);
    }

    @Test
    void delete403WrongPassToken() throws Exception {

        String token = createToken(LOGIN, WRONG).body().token;

        CreateResponse created =
                create(BookingData.defaultBooking());

        Response<ResponseBody> response =
                api.deleteBookingToken("token=" + token, created.bookingid).execute();
        assertThat(response.code()).isEqualTo(403);

        Response<BookingResponse> get =
                api.getBooking(
                        ACCEPT,
                        created.bookingid
                ).execute();

        assertThat(get.code()).isEqualTo(200);
    }

    @Test
    void delete403WrongLoginToken() throws Exception {

        String token = createToken(WRONG, PASS).body().token;

        CreateResponse created =
                create(BookingData.defaultBooking());

        Response<ResponseBody> response =
                api.deleteBookingToken("token=" + token, created.bookingid).execute();
        assertThat(response.code()).isEqualTo(403);

        Response<BookingResponse> get =
                api.getBooking(
                        ACCEPT,
                        created.bookingid
                ).execute();

        assertThat(get.code()).isEqualTo(200);
    }

    @Test
    void delete404WrongIDToken() throws Exception {

        String token = createToken(LOGIN, PASS).body().token;

        CreateResponse created =
                create(BookingData.defaultBooking());

        Response<ResponseBody> response =
                api.deleteBookingToken("token=" + token, 999999).execute();
        assertThat(response.code()).isEqualTo(405);

        Response<BookingResponse> get =
                api.getBooking(
                        ACCEPT,
                        created.bookingid
                ).execute();

        assertThat(get.code()).isEqualTo(200);
    }

    @Test
    void delete200Auth() throws Exception {

        String auth =
                Credentials.basic(LOGIN,PASS);

        CreateResponse created =
                create(BookingData.defaultBooking());

        Response<ResponseBody> response =
                api.deleteBookingAuth(auth, created.bookingid).execute();
        assertThat(response.code()).isEqualTo(201);

        Response<BookingResponse> get =
                api.getBooking(
                        ACCEPT,
                        created.bookingid
                ).execute();

        assertThat(get.code()).isEqualTo(404);
    }

    @Test
    void delete403WrongPassAuth() throws Exception {

        String auth =
                Credentials.basic(LOGIN,WRONG);

        CreateResponse created =
                create(BookingData.defaultBooking());

        Response<ResponseBody> response =
                api.deleteBookingAuth(auth, created.bookingid).execute();
        assertThat(response.code()).isEqualTo(403);

        Response<BookingResponse> get =
                api.getBooking(
                        ACCEPT,
                        created.bookingid
                ).execute();

        assertThat(get.code()).isEqualTo(200);
    }

    @Test
    void delete403WrongLoginAuth() throws Exception {

        String auth =
                Credentials.basic(WRONG,PASS);

        CreateResponse created =
                create(BookingData.defaultBooking());

        Response<ResponseBody> response =
                api.deleteBookingAuth(auth, created.bookingid).execute();
        assertThat(response.code()).isEqualTo(403);

        Response<BookingResponse> get =
                api.getBooking(
                        ACCEPT,
                        created.bookingid
                ).execute();

        assertThat(get.code()).isEqualTo(200);
    }

    @Test
    void delete404WrongIDAuth() throws Exception {

        String auth =
                Credentials.basic(LOGIN,PASS);

        CreateResponse created =
                create(BookingData.defaultBooking());

        Response<ResponseBody> response =
                api.deleteBookingAuth(auth, 999999).execute();
        assertThat(response.code()).isEqualTo(405);

        Response<BookingResponse> get =
                api.getBooking(
                        ACCEPT,
                        created.bookingid
                ).execute();

        assertThat(get.code()).isEqualTo(200);
    }
}