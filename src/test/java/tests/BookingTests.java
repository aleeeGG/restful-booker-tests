package tests;

import api.RestfulBookerApi;
import api.RetrofitClient;
import data.BookingData;
import model.booking.*;
import okhttp3.Credentials;
import okhttp3.ResponseBody;
import org.junit.jupiter.api.Test;
import retrofit2.Response;
import steps.AuthSteps;
import steps.BookingSteps;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class BookingTests extends BaseTest {
    protected final String CONTENT_TYPE = "application/json";
    protected final String ACCEPT = "application/json";
    protected final String LOGIN = "admin";
    protected final String PASS = "password123";
    protected final String WRONG = "wrong";
    protected final String UPFIRST = "UpdatedFirst";
    protected final String UPLAST = "UpdatedLast";

    private final RestfulBookerApi api =
            RetrofitClient.getClient(
                    RestfulBookerApi.class,
                    config.baseUrl()
            );

    /// getBookingIDs
    @Test
    void getBookingIDs200() throws Exception {
        BookingSteps bookingSteps = new BookingSteps(api);

        Response<List<GetBookingIDsResponse>> response = bookingSteps.getBookingListIds(null,
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
        String name = "Vlad";
        BookingData BookingData = new BookingData();
        BookingSteps bookingSteps = new BookingSteps(api);
        bookingSteps.create(BookingData.defaultBooking());


        Response<List<GetBookingIDsResponse>> response =
               bookingSteps.getBookingListIds(
                        name,
                        null,
                        null,
                        null);

        int id = response.body().get(0).getBookingid();

        BookingResponse booking =
                bookingSteps.getBookingById(id);

        assertThat(response.code()).isEqualTo(200);
        assertThat(response.body()).isNotNull();
        assertThat(response.body()).isNotEmpty();
        assertThat(booking.getFirstname()).isEqualTo(name);
    }

    @Test
    void getBookingIDsNotFound() throws Exception {
        BookingSteps bookingSteps = new BookingSteps(api);

        Response<List<GetBookingIDsResponse>> response =
               bookingSteps.getBookingListIds(
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
        BookingSteps bookingSteps = new BookingSteps(api);
        bookingSteps.create(BookingData.defaultBooking());

        Response<List<GetBookingIDsResponse>> response =
               bookingSteps.getBookingListIds(
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
        BookingSteps bookingSteps = new BookingSteps(api);

        CreateResponse created =
                bookingSteps.create(BookingData.defaultBooking());

        BookingResponse booking =
               bookingSteps.getBookingById(created.getBookingid());

        assertThat(booking.getFirstname())
                .isEqualTo(created.getBooking().getFirstname());

        assertThat(booking.getLastname())
                .isEqualTo(created.getBooking().getLastname());

        assertThat(booking.getTotalPrice())
                .isEqualTo(created.getBooking().getTotalPrice());

        assertThat(booking.getDepositPaid())
                .isEqualTo(created.getBooking().getDepositPaid());

        assertThat(booking.getBookingdates().getCheckin())
                .isEqualTo(created.getBooking().getBookingdates().getCheckin());

        assertThat(booking.getBookingdates().getCheckout())
                .isEqualTo(created.getBooking().getBookingdates().getCheckout());


        assertThat(booking.getAdditionalNeeds())
                .isEqualTo(created.getBooking().getAdditionalNeeds());
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
        BookingSteps bookingSteps = new BookingSteps(api);
        CreateResponse created =
               bookingSteps.create(BookingData.defaultBooking());

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
        AuthSteps authSteps = new AuthSteps(api);
        String token = authSteps.createToken(LOGIN, PASS).body().getToken();

        BookingData BookingData = new BookingData();
        BookingSteps bookingSteps = new BookingSteps(api);
        CreateResponse created =
               bookingSteps.create(BookingData.defaultBooking());

        CreateUpdateRequest body =
                BookingData.updateBooking();

        Response<BookingResponse> response =
                api.updateBookingToken(
                        CONTENT_TYPE,
                        ACCEPT,
                        "token=" + token,
                        created.getBookingid(),
                        body).execute();

        assertThat(response.code()).isIn(200, 222);
        assertThat(response.body()).isNotNull();

        assertThat(response.body().getFirstname())
                .isEqualTo(body.getFirstname());

        assertThat(response.body().getLastname())
                .isEqualTo(body.getLastname());

        assertThat(response.body().getTotalPrice())
                .isEqualTo(body.getTotalprice());

        assertThat(response.body().getDepositPaid())
                .isEqualTo(body.getDepositpaid());

        assertThat(response.body().getBookingdates().getCheckin())
                .isEqualTo(body.getBookingdates().getCheckin());

        assertThat(response.body().getBookingdates().getCheckout())
                .isEqualTo(body.getBookingdates().getCheckout());

        assertThat(response.body().getAdditionalNeeds())
                .isEqualTo(body.getAdditionalneeds());

    }

    @Test
    void updateWrongPass403Token() throws Exception {
        AuthSteps authSteps = new AuthSteps(api);
        String token = authSteps.createToken(LOGIN, WRONG).body().getToken();

        BookingData BookingData = new BookingData();
        BookingSteps bookingSteps = new BookingSteps(api);
        CreateResponse created =
               bookingSteps.create(BookingData.defaultBooking());

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
        AuthSteps authSteps = new AuthSteps(api);
        String token = authSteps.createToken(WRONG, PASS).body().getToken();

        BookingData BookingData = new BookingData();
        BookingSteps bookingSteps = new BookingSteps(api);
        CreateResponse created =
               bookingSteps.create(BookingData.defaultBooking());

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
        BookingSteps bookingSteps = new BookingSteps(api);
        CreateResponse created =
               bookingSteps.create(BookingData.defaultBooking());

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
        BookingSteps bookingSteps = new BookingSteps(api);
        CreateResponse created = bookingSteps.create(BookingData.defaultBooking());

        CreateUpdateRequest body =
                BookingData.updateBooking();

        Response<BookingResponse> response =
                api.updateBookingAuth(
                        CONTENT_TYPE,
                        ACCEPT,
                        auth,
                        created.getBookingid(),
                        body).execute();

        assertThat(response.code()).isIn(200, 222);
        assertThat(response.body()).isNotNull();

        assertThat(response.body().getFirstname())
                .isEqualTo(body.getFirstname());

        assertThat(response.body().getLastname())
                .isEqualTo(body.getLastname());

        assertThat(response.body().getTotalPrice())
                .isEqualTo(body.getTotalprice());

        assertThat(response.body().getDepositPaid())
                .isEqualTo(body.getDepositpaid());

        assertThat(response.body().getBookingdates().getCheckin())
                .isEqualTo(body.getBookingdates().getCheckin());

        assertThat(response.body().getBookingdates().getCheckout())
                .isEqualTo(body.getBookingdates().getCheckout());

        assertThat(response.body().getAdditionalNeeds())
                .isEqualTo(body.getAdditionalneeds());
    }

    @Test
    void updateWrongPass403Auth() throws Exception {

        String auth =
                Credentials.basic(LOGIN, WRONG);

        BookingData BookingData = new BookingData();
        BookingSteps bookingSteps = new BookingSteps(api);
        CreateResponse created =
               bookingSteps.create(BookingData.defaultBooking());

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
        BookingSteps bookingSteps = new BookingSteps(api);
        CreateResponse created =
               bookingSteps.create(BookingData.defaultBooking());

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
        BookingSteps bookingSteps = new BookingSteps(api);
        CreateResponse created =
               bookingSteps.create(BookingData.defaultBooking());

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
        AuthSteps authSteps = new AuthSteps(api);
        String token = authSteps.createToken(LOGIN, PASS).body().getToken();

        BookingData BookingData = new BookingData();
        BookingSteps bookingSteps = new BookingSteps(api);
        CreateResponse created =
               bookingSteps.create(BookingData.defaultBooking());

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
        AuthSteps authSteps = new AuthSteps(api);
        String token = authSteps.createToken(LOGIN, PASS).body().getToken();

        BookingData bookingData = new BookingData();
        BookingSteps bookingSteps = new BookingSteps(api);
        CreateResponse created =
               bookingSteps.create(bookingData.defaultBooking());

        PartialUpdateRequest body = new PartialUpdateRequest();
        body.setFirstname(UPFIRST);
        body.setLastname(UPLAST);

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
        AuthSteps authSteps = new AuthSteps(api);
        String token = authSteps.createToken(LOGIN, WRONG).body().getToken();

        BookingData bookingData = new BookingData();
        BookingSteps bookingSteps = new BookingSteps(api);
        CreateResponse created =
               bookingSteps.create(bookingData.defaultBooking());

        PartialUpdateRequest body = new PartialUpdateRequest();
        body.setFirstname(UPFIRST);
        body.setLastname(UPLAST);

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
        AuthSteps authSteps = new AuthSteps(api);
        String token = authSteps.createToken(WRONG, PASS).body().getToken();

        BookingData bookingData = new BookingData();
        BookingSteps bookingSteps = new BookingSteps(api);
        CreateResponse created =
               bookingSteps.create(bookingData.defaultBooking());

        PartialUpdateRequest body = new PartialUpdateRequest();
        body.setFirstname(UPFIRST);

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
        BookingSteps bookingSteps = new BookingSteps(api);
        CreateResponse created =
               bookingSteps.create(bookingData.defaultBooking());

        PartialUpdateRequest body = new PartialUpdateRequest();
        body.setFirstname(UPFIRST);

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
        BookingSteps bookingSteps = new BookingSteps(api);
        CreateResponse created =
               bookingSteps.create(bookingData.defaultBooking());

        PartialUpdateRequest body = new PartialUpdateRequest();
        body.setFirstname(UPFIRST);
        body.setLastname(UPLAST);

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
        BookingSteps bookingSteps = new BookingSteps(api);
        CreateResponse created =
               bookingSteps.create(bookingData.defaultBooking());

        PartialUpdateRequest body = new PartialUpdateRequest();
        body.setFirstname(UPFIRST);

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
        BookingSteps bookingSteps = new BookingSteps(api);
        CreateResponse created =
               bookingSteps.create(bookingData.defaultBooking());

        PartialUpdateRequest body = new PartialUpdateRequest();
        body.setFirstname(UPFIRST);

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
        BookingSteps bookingSteps = new BookingSteps(api);
        CreateResponse created =
               bookingSteps.create(bookingData.defaultBooking());

        PartialUpdateRequest body = new PartialUpdateRequest();
        body.setFirstname(UPFIRST);

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
        AuthSteps authSteps = new AuthSteps(api);
        String token = authSteps.createToken(LOGIN, PASS).body().getToken();

        BookingData bookingData = new BookingData();
        BookingSteps bookingSteps = new BookingSteps(api);
        CreateResponse created =
               bookingSteps.create(bookingData.defaultBooking());

        PartialUpdateRequest body = new PartialUpdateRequest();
        body.setFirstname(UPFIRST);

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
        body.setFirstname(UPFIRST);

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
        AuthSteps authSteps = new AuthSteps(api);
        String token = authSteps.createToken(LOGIN, PASS).body().getToken();

        BookingData bookingData = new BookingData();
        BookingSteps bookingSteps = new BookingSteps(api);
        CreateResponse created =
                bookingSteps.create(bookingData.defaultBooking());

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
        AuthSteps authSteps = new AuthSteps(api);
        String token = authSteps.createToken(LOGIN, WRONG).body().getToken();

        BookingData BookingData = new BookingData();
        BookingSteps bookingSteps = new BookingSteps(api);
        CreateResponse created =
                bookingSteps.create(BookingData.defaultBooking());

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
        AuthSteps authSteps = new AuthSteps(api);
        String token = authSteps.createToken(WRONG, PASS).body().getToken();

        BookingData bookingData = new BookingData();
        BookingSteps bookingSteps = new BookingSteps(api);
        CreateResponse created =
                bookingSteps.create(bookingData.defaultBooking());

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
        AuthSteps authSteps = new AuthSteps(api);
        String token = authSteps.createToken(LOGIN, PASS).body().getToken();

        BookingData BookingData = new BookingData();
        BookingSteps bookingSteps = new BookingSteps(api);
        CreateResponse created =
                bookingSteps.create(BookingData.defaultBooking());

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
        BookingSteps bookingSteps = new BookingSteps(api);
        CreateResponse created =
                bookingSteps.create(BookingData.defaultBooking());

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
        BookingSteps bookingSteps = new BookingSteps(api);
        CreateResponse created =
                bookingSteps.create(BookingData.defaultBooking());

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
        BookingSteps bookingSteps = new BookingSteps(api);
        CreateResponse created =
                bookingSteps.create(BookingData.defaultBooking());

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
        BookingSteps bookingSteps = new BookingSteps(api);
        CreateResponse created =
               bookingSteps.create(BookingData.defaultBooking());

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