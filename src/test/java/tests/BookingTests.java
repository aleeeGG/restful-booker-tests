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
                assertThat(id.getBookingid()).isPositive());
        assertThat(response.body())
                .extracting(b -> b.getBookingid())
                .doesNotHaveDuplicates();
    }

    @Test
    void getNameBookingIDs200() throws Exception {

        BookingData BookingData = new BookingData();
        create(BookingData.defaultBooking());


        Response<List<GetBookingIDsResponse>> response =
                getBookingListIds(
                        "Vlad",
                        null,
                        null,
                        null);

        int id = response.body().get(0).getBookingid();

        BookingResponse booking =
                getBookingById(id);

        assertThat(response.code()).isEqualTo(200);
        assertThat(response.body()).isNotNull();
        assertThat(response.body()).isNotEmpty();
        assertThat(booking.getFirstname()).isEqualTo("Vlad");
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
        BookingData BookingData = new BookingData();
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

        BookingData BookingData = new BookingData();

        CreateResponse created =
                create(BookingData.defaultBooking());

        BookingResponse booking =
                getBookingById(created.getBookingid());

        assertThat(booking.getFirstname())
                .isEqualTo(created.getBooking().getFirstname());

        assertThat(booking.getLastname())
                .isEqualTo(created.getBooking().getLastname());

        assertThat(booking.getTotalprice())
                .isEqualTo(created.getBooking().getTotalprice());

        assertThat(booking.getDepositpaid())
                .isEqualTo(created.getBooking().getDepositpaid());

        assertThat(booking.getBookingdates().getCheckin())
                .isEqualTo(created.getBooking().getBookingdates().getCheckin());

        assertThat(booking.getBookingdates().getCheckout())
                .isEqualTo(created.getBooking().getBookingdates().getCheckout());


        assertThat(booking.getAdditionalneeds())
                .isEqualTo(created.getBooking().getAdditionalneeds());
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

        BookingData BookingData = new BookingData();
        CreateResponse created =
                create(BookingData.defaultBooking());

        assertThat(created.getBookingid()).isPositive();
        assertThat(created.getBooking().getFirstname()).isEqualTo("Vlad");
        assertThat(created.getBooking().getLastname()).isEqualTo("Tretyakov");
    }

    @Test
    void createBookingNullFirstname() throws Exception {

        BookingData BookingData = new BookingData();
        CreateUpdateRequest body =
                BookingData.defaultBooking();

        body.setFirstname(null);

        Response<CreateResponse> response =
                api.createBooking(
                        CONTENT_TYPE,
                        ACCEPT,
                        body).execute();

        assertThat(response.code()).isIn(400, 500);
    }

    @Test
    void createBookingWrongDate() throws Exception {

        BookingData BookingData = new BookingData();
        CreateUpdateRequest body =
                BookingData.defaultBooking();

        body.setBookingdates(new BookingDates(
                "10-05-2024",
                "01-09-2024"
        ));

        Response<CreateResponse> response =
                api.createBooking(
                        CONTENT_TYPE,
                        ACCEPT,
                        body).execute();

        assertThat(response.code()).isIn(400, 500);
    }

    @Test
    void createBookingCheckoutBeforeCheckin() throws Exception {

        BookingData BookingData = new BookingData();
        CreateUpdateRequest body =
                BookingData.defaultBooking();

        body.setBookingdates(new BookingDates(
                "2024-09-01",
                "2024-05-10"
        ));

        Response<CreateResponse> response =
                api.createBooking(
                        CONTENT_TYPE,
                        ACCEPT,
                        body).execute();

        assertThat(response.code()).isIn(400, 500);
    }

    @Test
    void createBookingNegativePrice() throws Exception {

        BookingData BookingData = new BookingData();
        CreateUpdateRequest body =
                BookingData.defaultBooking();

        body.setTotalprice(-100);

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

        String token = createToken(LOGIN, PASS).body().getToken();

        BookingData BookingData = new BookingData();
        CreateResponse created =
                create(BookingData.defaultBooking());

        CreateUpdateRequest body =
                BookingData.updateBooking();

        Response<BookingResponse> response =
                api.updateBookingToken(
                        CONTENT_TYPE,
                        ACCEPT,
                        "token=" + token,
                        created.getBookingid(),
                        body).execute();

        assertThat(response.code()).isEqualTo(200);
        assertThat(response.body()).isNotNull();

        assertThat(response.body().getFirstname())
                .isEqualTo(body.getFirstname());

        assertThat(response.body().getLastname())
                .isEqualTo(body.getLastname());

        assertThat(response.body().getTotalprice())
                .isEqualTo(body.getTotalprice());

        assertThat(response.body().getDepositpaid())
                .isEqualTo(body.getDepositpaid());

        assertThat(response.body().getBookingdates().getCheckin())
                .isEqualTo(body.getBookingdates().getCheckin());

        assertThat(response.body().getBookingdates().getCheckout())
                .isEqualTo(body.getBookingdates().getCheckout());

        assertThat(response.body().getAdditionalneeds())
                .isEqualTo(body.getAdditionalneeds());

    }

    @Test
    void updateWrongPass403Token() throws Exception {

        String token = createToken(LOGIN, WRONG).body().getToken();

        BookingData BookingData = new BookingData();
        CreateResponse created =
                create(BookingData.defaultBooking());

        CreateUpdateRequest body =
                BookingData.updateBooking();

        Response<BookingResponse> response =
                api.updateBookingToken(
                        CONTENT_TYPE,
                        ACCEPT,
                        "token=" + token,
                        created.getBookingid(),
                        body).execute();

        assertThat(response.code()).isEqualTo(403);
        assertThat(response.errorBody()).isNotNull();
    }

    @Test
    void updateWrongLogin403Token() throws Exception {

        String token = createToken(WRONG, PASS).body().getToken();

        BookingData BookingData = new BookingData();
        CreateResponse created =
                create(BookingData.defaultBooking());

        CreateUpdateRequest body =
                BookingData.updateBooking();

        Response<BookingResponse> response =
                api.updateBookingToken(
                        CONTENT_TYPE,
                        ACCEPT,
                        "token=" + token,
                        created.getBookingid(),
                        body).execute();

        assertThat(response.code()).isEqualTo(403);
        assertThat(response.errorBody()).isNotNull();
    }

    @Test
    void updateNoneAuth403Token() throws Exception {

        BookingData BookingData = new BookingData();
        CreateResponse created =
                create(BookingData.defaultBooking());

        CreateUpdateRequest body =
                BookingData.updateBooking();

        Response<BookingResponse> response =
                api.updateBookingToken(
                        CONTENT_TYPE,
                        ACCEPT,
                        null,
                        created.getBookingid(),
                        body).execute();

        assertThat(response.code()).isEqualTo(403);
        assertThat(response.errorBody()).isNotNull();
    }

    @Test
    void update200Auth() throws Exception {

        String auth =
                Credentials.basic(LOGIN, PASS);

        BookingData BookingData = new BookingData();
        CreateResponse created =
                create(BookingData.defaultBooking());

        CreateUpdateRequest body =
                BookingData.updateBooking();

        Response<BookingResponse> response =
                api.updateBookingAuth(
                        CONTENT_TYPE,
                        ACCEPT,
                        auth,
                        created.getBookingid(),
                        body).execute();

        assertThat(response.code()).isEqualTo(200);
        assertThat(response.body()).isNotNull();

        assertThat(response.body().getFirstname())
                .isEqualTo(body.getFirstname());

        assertThat(response.body().getLastname())
                .isEqualTo(body.getLastname());

        assertThat(response.body().getTotalprice())
                .isEqualTo(body.getTotalprice());

        assertThat(response.body().getDepositpaid())
                .isEqualTo(body.getDepositpaid());

        assertThat(response.body().getBookingdates().getCheckin())
                .isEqualTo(body.getBookingdates().getCheckin());

        assertThat(response.body().getBookingdates().getCheckout())
                .isEqualTo(body.getBookingdates().getCheckout());

        assertThat(response.body().getAdditionalneeds())
                .isEqualTo(body.getAdditionalneeds());
    }

    @Test
    void updateWrongPass403Auth() throws Exception {

        String auth =
                Credentials.basic(LOGIN, WRONG);

        BookingData BookingData = new BookingData();
        CreateResponse created =
                create(BookingData.defaultBooking());

        CreateUpdateRequest body =
                BookingData.updateBooking();

        Response<BookingResponse> response =
                api.updateBookingAuth(
                        CONTENT_TYPE,
                        ACCEPT,
                        auth,
                        created.getBookingid(),
                        body).execute();

        assertThat(response.code()).isEqualTo(403);
        assertThat(response.errorBody()).isNotNull();
    }

    @Test
    void updateWrongLogin403Auth() throws Exception {

        String auth =
                Credentials.basic(WRONG, PASS);

        BookingData BookingData = new BookingData();
        CreateResponse created =
                create(BookingData.defaultBooking());

        CreateUpdateRequest body =
                BookingData.updateBooking();

        Response<BookingResponse> response =
                api.updateBookingAuth(
                        CONTENT_TYPE,
                        ACCEPT,
                        auth,
                        created.getBookingid(),
                        body).execute();

        assertThat(response.code()).isEqualTo(403);
        assertThat(response.errorBody()).isNotNull();
    }

    @Test
    void updateNoneAuth403Auth() throws Exception {

        BookingData BookingData = new BookingData();
        CreateResponse created =
                create(BookingData.defaultBooking());

        CreateUpdateRequest body =
                BookingData.updateBooking();

        Response<BookingResponse> response =
                api.updateBookingAuth(
                        CONTENT_TYPE,
                        ACCEPT,
                        null,
                        created.getBookingid(),
                        body).execute();

        assertThat(response.code()).isEqualTo(403);
        assertThat(response.errorBody()).isNotNull();
    }

    @Test
    void updateWrongID405Token() throws Exception {

        String token = createToken(LOGIN, PASS).body().getToken();

        BookingData BookingData = new BookingData();
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

        BookingData BookingData = new BookingData();
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

        String token = createToken(LOGIN, PASS).body().getToken();

        BookingData bookingData = new BookingData();
        CreateResponse created =
                create(bookingData.defaultBooking());

        PartialUpdateRequest body = new PartialUpdateRequest();
        body.setFirstname("UpdatedFirst");
        body.setLastname("UpdatedLast");

        Response<BookingResponse> response =
                api.partialUpdateBookingToken(
                        CONTENT_TYPE,
                        ACCEPT,
                        "token=" + token,
                        created.getBookingid(),
                        body).execute();

        assertThat(response.code()).isEqualTo(200);
        assertThat(response.body()).isNotNull();

        assertThat(response.body().getFirstname())
                .isEqualTo(body.getFirstname());

        assertThat(response.body().getLastname())
                .isEqualTo(body.getLastname());
    }

    @Test
    void partialUpdateWrongPass403Token() throws Exception {

        String token = createToken(LOGIN, WRONG).body().getToken();

        BookingData bookingData = new BookingData();
        CreateResponse created =
                create(bookingData.defaultBooking());

        PartialUpdateRequest body = new PartialUpdateRequest();
        body.setFirstname("UpdatedFirst");
        body.setLastname("UpdatedLast");

        Response<BookingResponse> response =
                api.partialUpdateBookingToken(
                        CONTENT_TYPE,
                        ACCEPT,
                        "token=" + token,
                        created.getBookingid(),
                        body).execute();

        assertThat(response.code()).isEqualTo(403);
        assertThat(response.errorBody()).isNotNull();
    }

    @Test
    void partialUpdateWrongLogin403Token() throws Exception {

        String token = createToken(WRONG, PASS).body().getToken();

        BookingData bookingData = new BookingData();
        CreateResponse created =
                create(bookingData.defaultBooking());

        PartialUpdateRequest body = new PartialUpdateRequest();
        body.setFirstname("UpdatedFirst");

        Response<BookingResponse> response =
                api.partialUpdateBookingToken(
                        CONTENT_TYPE,
                        ACCEPT,
                        "token=" + token,
                        created.getBookingid(),
                        body).execute();

        assertThat(response.code()).isEqualTo(403);
        assertThat(response.errorBody()).isNotNull();
    }

    @Test
    void partialUpdateNoneAuth403Token() throws Exception {

        BookingData bookingData = new BookingData();
        CreateResponse created =
                create(bookingData.defaultBooking());

        PartialUpdateRequest body = new PartialUpdateRequest();
        body.setFirstname("UpdatedFirst");

        Response<BookingResponse> response =
                api.partialUpdateBookingToken(
                        CONTENT_TYPE,
                        ACCEPT,
                        null,
                        created.getBookingid(),
                        body).execute();

        assertThat(response.code()).isEqualTo(403);
        assertThat(response.errorBody()).isNotNull();
    }

    @Test
    void partialUpdate200Auth() throws Exception {

        String auth =
                Credentials.basic(LOGIN, PASS);

        BookingData bookingData = new BookingData();
        CreateResponse created =
                create(bookingData.defaultBooking());

        PartialUpdateRequest body = new PartialUpdateRequest();
        body.setFirstname("UpdatedFirst");
        body.setLastname("UpdatedLast");

        Response<BookingResponse> response =
                api.partialUpdateBookingAuth(
                        CONTENT_TYPE,
                        ACCEPT,
                        auth,
                        created.getBookingid(),
                        body).execute();

        assertThat(response.code()).isEqualTo(200);
        assertThat(response.body()).isNotNull();

        assertThat(response.body().getFirstname())
                .isEqualTo(body.getFirstname());

        assertThat(response.body().getLastname())
                .isEqualTo(body.getLastname());

    }

    @Test
    void partialUpdateWrongPass403Auth() throws Exception {

        String auth =
                Credentials.basic(LOGIN, WRONG);

        BookingData bookingData = new BookingData();
        CreateResponse created =
                create(bookingData.defaultBooking());

        PartialUpdateRequest body = new PartialUpdateRequest();
        body.setFirstname("UpdatedFirst");

        Response<BookingResponse> response =
                api.partialUpdateBookingAuth(
                        CONTENT_TYPE,
                        ACCEPT,
                        auth,
                        created.getBookingid(),
                        body).execute();

        assertThat(response.code()).isEqualTo(403);
        assertThat(response.errorBody()).isNotNull();
    }

    @Test
    void partialUpdateWrongLogin403Auth() throws Exception {

        String auth =
                Credentials.basic(WRONG, PASS);

        BookingData bookingData = new BookingData();
        CreateResponse created =
                create(bookingData.defaultBooking());

        PartialUpdateRequest body = new PartialUpdateRequest();
        body.setFirstname("UpdatedFirst");

        Response<BookingResponse> response =
                api.partialUpdateBookingAuth(
                        CONTENT_TYPE,
                        ACCEPT,
                        auth,
                        created.getBookingid(),
                        body).execute();

        assertThat(response.code()).isEqualTo(403);
        assertThat(response.errorBody()).isNotNull();
    }

    @Test
    void partialUpdateNoneAuth403Auth() throws Exception {

        BookingData bookingData = new BookingData();
        CreateResponse created =
                create(bookingData.defaultBooking());

        PartialUpdateRequest body = new PartialUpdateRequest();
        body.setFirstname("UpdatedFirst");

        Response<BookingResponse> response =
                api.partialUpdateBookingAuth(
                        CONTENT_TYPE,
                        ACCEPT,
                        null,
                        created.getBookingid(),
                        body).execute();

        assertThat(response.code()).isEqualTo(403);
        assertThat(response.errorBody()).isNotNull();
    }

    @Test
    void partialUpdateWrongID405Token() throws Exception {

        String token = createToken(LOGIN, PASS).body().getToken();

        BookingData bookingData = new BookingData();
        CreateResponse created =
                create(bookingData.defaultBooking());

        PartialUpdateRequest body = new PartialUpdateRequest();
        body.setFirstname("UpdatedFirst");

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
        body.setFirstname("UpdatedFirst");

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

        String token = createToken(LOGIN, PASS).body().getToken();

        BookingData bookingData = new BookingData();
        CreateResponse created =
                create(bookingData.defaultBooking());

        Response<ResponseBody> response =
                api.deleteBookingToken("token=" + token, created.getBookingid()).execute();
        assertThat(response.code()).isEqualTo(201);

        Response<BookingResponse> get =
                api.getBooking(
                        ACCEPT,
                        created.getBookingid()
                ).execute();

        assertThat(get.code()).isEqualTo(404);
    }

    @Test
    void delete403WrongPassToken() throws Exception {

        String token = createToken(LOGIN, WRONG).body().getToken();

        BookingData BookingData = new BookingData();
        CreateResponse created =
                create(BookingData.defaultBooking());

        Response<ResponseBody> response =
                api.deleteBookingToken("token=" + token, created.getBookingid()).execute();
        assertThat(response.code()).isEqualTo(403);

        Response<BookingResponse> get =
                api.getBooking(
                        ACCEPT,
                        created.getBookingid()
                ).execute();

        assertThat(get.code()).isEqualTo(200);
    }

    @Test
    void delete403WrongLoginToken() throws Exception {

        String token = createToken(WRONG, PASS).body().getToken();

        BookingData bookingData = new BookingData();
        CreateResponse created =
                create(bookingData.defaultBooking());

        Response<ResponseBody> response =
                api.deleteBookingToken("token=" + token, created.getBookingid()).execute();
        assertThat(response.code()).isEqualTo(403);

        Response<BookingResponse> get =
                api.getBooking(
                        ACCEPT,
                        created.getBookingid()
                ).execute();

        assertThat(get.code()).isEqualTo(200);
    }

    @Test
    void delete404WrongIDToken() throws Exception {

        String token = createToken(LOGIN, PASS).body().getToken();

        BookingData BookingData = new BookingData();
        CreateResponse created =
                create(BookingData.defaultBooking());

        Response<ResponseBody> response =
                api.deleteBookingToken("token=" + token, 999999).execute();
        assertThat(response.code()).isEqualTo(405);

        Response<BookingResponse> get =
                api.getBooking(
                        ACCEPT,
                        created.getBookingid()
                ).execute();

        assertThat(get.code()).isEqualTo(200);
    }

    @Test
    void delete200Auth() throws Exception {

        String auth =
                Credentials.basic(LOGIN,PASS);

        BookingData BookingData = new BookingData();
        CreateResponse created =
                create(BookingData.defaultBooking());

        Response<ResponseBody> response =
                api.deleteBookingAuth(auth, created.getBookingid()).execute();
        assertThat(response.code()).isEqualTo(201);

        Response<BookingResponse> get =
                api.getBooking(
                        ACCEPT,
                        created.getBookingid()
                ).execute();

        assertThat(get.code()).isEqualTo(404);
    }

    @Test
    void delete403WrongPassAuth() throws Exception {

        String auth =
                Credentials.basic(LOGIN,WRONG);

        BookingData BookingData = new BookingData();
        CreateResponse created =
                create(BookingData.defaultBooking());

        Response<ResponseBody> response =
                api.deleteBookingAuth(auth, created.getBookingid()).execute();
        assertThat(response.code()).isEqualTo(403);

        Response<BookingResponse> get =
                api.getBooking(
                        ACCEPT,
                        created.getBookingid()
                ).execute();

        assertThat(get.code()).isEqualTo(200);
    }

    @Test
    void delete403WrongLoginAuth() throws Exception {

        String auth =
                Credentials.basic(WRONG,PASS);

        BookingData BookingData = new BookingData();
        CreateResponse created =
                create(BookingData.defaultBooking());

        Response<ResponseBody> response =
                api.deleteBookingAuth(auth, created.getBookingid()).execute();
        assertThat(response.code()).isEqualTo(403);

        Response<BookingResponse> get =
                api.getBooking(
                        ACCEPT,
                        created.getBookingid()
                ).execute();

        assertThat(get.code()).isEqualTo(200);
    }

    @Test
    void delete404WrongIDAuth() throws Exception {

        String auth =
                Credentials.basic(LOGIN,PASS);

        BookingData BookingData = new BookingData();
        CreateResponse created =
                create(BookingData.defaultBooking());

        Response<ResponseBody> response =
                api.deleteBookingAuth(auth, 999999).execute();
        assertThat(response.code()).isEqualTo(405);

        Response<BookingResponse> get =
                api.getBooking(
                        ACCEPT,
                        created.getBookingid()
                ).execute();

        assertThat(get.code()).isEqualTo(200);
    }
}