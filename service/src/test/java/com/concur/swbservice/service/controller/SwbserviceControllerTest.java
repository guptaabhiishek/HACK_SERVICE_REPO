package com.concur.swbservice.service.controller;

import com.concur.swbservice.api.model.v1_0.SwbserviceModel;
import com.concur.swbservice.service.component.RxObservableHandlerMethodReturnValueHandler;
import com.concur.swbservice.service.repository.CrudRepository;
import org.easymock.Capture;
import org.easymock.Mock;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import rx.Observable;

import java.util.Collection;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;
import static org.hamcrest.Matchers.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Created by mtalbot on 17/08/2015.
 */
public class SwbserviceControllerTest {

    private static String[] uuids = new String[]{
            "939efaaf-8746-4fe4-ac50-bbd3427d5bba",
            "82e874d3-990d-4036-aa01-c4414806a3b5",
            "b9cf95e1-d7e3-4430-ac98-b1a10f0ed2b7",
            "1c7a10a4-8d72-479b-bdc7-de16b6261464",
            "c65304cd-8469-409f-bcd8-abe9711b68ec"
    };

    private MockMvc mockMvc;
    private SwbserviceController controller;
    private CrudRepository<UUID, SwbserviceModel> mockDAO;

    @Before
    public void setup() {
        mockDAO = createStrictMock(CrudRepository.class);
        controller = new SwbserviceController(mockDAO);
        this.mockMvc = MockMvcBuilders.
                standaloneSetup(this.controller).
                setCustomReturnValueHandlers(
                        new RxObservableHandlerMethodReturnValueHandler()
                ).
                build();
    }

    private SwbserviceModel genTest(int ID, String date) {
        return new SwbserviceModel(
                UUID.fromString(uuids[ID]),
                "Test" + Integer.toString(ID + 1),
                DateTime.parse(date),
                (long) (ID + 1)
        );
    }

    @Test
    public void testList() throws Exception {
        expect(
                mockDAO.
                        findAll()
        ).
                andReturn(
                        Observable.
                                just(
                                        genTest(0, "1982-01-30T00:00:00.000Z"),
                                        genTest(1, "1945-06-12T14:31:19.056Z"),
                                        genTest(2, "1982-01-30T00:00:00.000Z"),
                                        genTest(3, "1982-01-30T00:00:00.000Z"),
                                        genTest(4, "1982-01-30T00:00:00.000Z")
                                )
                );

        replay(mockDAO);

        MvcResult result = mockMvc.
                perform(
                        get("/Swbservice/v1.0/").
                                accept(MediaType.APPLICATION_JSON)
                ).
                andExpect(status().isOk()).
                andExpect(request().asyncStarted()).
                andReturn();


        mockMvc.
                perform(asyncDispatch(result)).
                andExpect(status().isOk()).
                andExpect(jsonPath("$.content[*]", hasSize(5))).
                andExpect(jsonPath("$.content[*].id", hasItem(uuids[0]))).
                andExpect(jsonPath("$.content[*].id", hasItem(uuids[1]))).
                andExpect(jsonPath("$.content[*].id", hasItem(uuids[2]))).
                andExpect(jsonPath("$.content[*].id", hasItem(uuids[3]))).
                andExpect(jsonPath("$.content[*].id", hasItem(uuids[4]))).
                andExpect(jsonPath("$.content[*].name", hasItem("Test1"))).
                andExpect(jsonPath("$.content[*].name", hasItem("Test2"))).
                andExpect(jsonPath("$.content[*].name", hasItem("Test3"))).
                andExpect(jsonPath("$.content[*].name", hasItem("Test4"))).
                andExpect(jsonPath("$.content[*].name", hasItem("Test5")));

        verify(mockDAO);
    }

    @Test
    public void testGetOK() throws Exception {
        Capture<UUID> id = Capture.newInstance();
        expect(
                mockDAO.
                        findById(capture(id))
        ).andAnswer(() ->
                        Observable.
                                just(
                                        genTest(0, "1945-06-12T14:31:19.056Z")
                                )
        );

        replay(mockDAO);

        MvcResult result = mockMvc.
                perform(
                        get("/Swbservice/v1.0/" + uuids[0]).
                                accept(MediaType.APPLICATION_JSON)
                ).
                andExpect(status().isOk()).
                andExpect(request().asyncStarted()).
                andReturn();

        mockMvc.
                perform(asyncDispatch(result)).
                andExpect(status().isOk()).
                andExpect(jsonPath("$.id", equalTo(uuids[0]))).
                andExpect(jsonPath("$.name", equalTo("Test1"))).
                andExpect(jsonPath("$.links[*]", hasSize(1)));

        assertEquals(uuids[0], id.
                        getValue().
                        toString()
        );

        verify(mockDAO);
    }

    @Test
    public void testGetNotFound() throws Exception {
        Capture<UUID> id = Capture.newInstance();
        expect(
                mockDAO.
                        findById(capture(id))
        ).andAnswer(() ->
                        Observable.
                                empty()
        );

        replay(mockDAO);

        MvcResult result = mockMvc.
                perform(
                        get("/Swbservice/v1.0/" + uuids[1]).
                                accept(MediaType.APPLICATION_JSON)
                ).
                andExpect(status().isOk()).
                andExpect(request().asyncStarted()).
                andReturn();

        mockMvc.
                perform(asyncDispatch(result)).
                andExpect(status().isNotFound());

        assertEquals(uuids[1], id.
                        getValue().
                        toString()
        );

        verify(mockDAO);
    }

    @Test
    public void testSaveValid() throws Exception {
        Capture<UUID> id = Capture.newInstance();
        Capture<SwbserviceModel> model = Capture.newInstance();
        expect(
                mockDAO.
                        save(capture(id), capture(model))
        ).andAnswer(() ->
                        Observable.
                                just(
                                        model.getValue()
                                )
        );

        replay(mockDAO);

        MvcResult result = mockMvc.
                perform(
                        put("/Swbservice/v1.0/" + uuids[0]).
                                accept(MediaType.APPLICATION_JSON).
                                contentType(MediaType.APPLICATION_JSON).
                                content("{\"id\": \"" + uuids[0] + "\",\"name\": \"Test1\",\"occurs\": \"1982-01-30T00:00:00.000Z\",\"frequency\": 1}")
                ).
                andExpect(status().isOk()).
                andExpect(request().asyncStarted()).
                andReturn();

        mockMvc.
                perform(asyncDispatch(result)).
                andExpect(status().isAccepted()).
                andExpect(jsonPath("$.id", equalTo(uuids[0]))).
                andExpect(jsonPath("$.name", equalTo("Test1"))).
                andExpect(jsonPath("$.links[*]", hasSize(1)));

        assertEquals(uuids[0], id.
                        getValue().
                        toString()
        );

        verify(mockDAO);
    }

    @Test
    public void testSaveInValid() throws Exception {
        testSaveInvalid("{\"id\": \"939efaaf-8746-4fe4-ac50-bbd3427d5bba\",\"name\": \"Test1\",\"occurs\": \"1982-01-30T00:00:00.000Z\",\"frequency\": -1}");
        testSaveInvalid("{\"id\": \"939efaaf-8746-4fe4-ac50-bbd3427d5bba\",\"name\": \"Test1\",\"occurs\": \"1982-01-30T00:00:00.000Z\",\"frequency\": 101}");
        testSaveInvalid("{\"id\": \"939efaaf-8746-4fe4-ac50-bbd3427d5bba\",\"name\": \"\",\"occurs\": \"1982-01-30T00:00:00.000Z\",\"frequency\": 1}");
        testSaveInvalid("{\"id\": \"939efaaf-8746-4fe4-ac50-bbd3427d5bba\",\"name\": \"Test1\",\"occurs\": \"\",\"frequency\": 1}");
    }

    private void testSaveInvalid(String content) throws Exception {
        mockMvc.
                perform(
                        put("/Swbservice/v1.0/" + uuids[0]).
                                accept(MediaType.APPLICATION_JSON).
                                contentType(MediaType.APPLICATION_JSON).
                                content(content)
                ).
                andExpect(status().isBadRequest());
    }

    @Test
    public void testDeleteOk() throws Exception {
        Capture<UUID> id = Capture.newInstance();
        expect(
                mockDAO.
                        delete(capture(id))
        ).andAnswer(() ->
                        Observable.
                                just(true)
        );

        replay(mockDAO);

        mockMvc.
                perform(
                        delete("/Swbservice/v1.0/" + uuids[2]).
                                accept(MediaType.APPLICATION_JSON)
                ).
                andExpect(status().isOk()).
                andExpect(content().string("true"));

        assertEquals(uuids[2], id.
                        getValue().
                        toString()
        );

        verify(mockDAO);
    }

    @Test
    public void testDeleteNotFound() throws Exception {
        Capture<UUID> id = Capture.newInstance();
        expect(
                mockDAO.
                        delete(capture(id))
        ).andAnswer(() ->
                        Observable.
                                just(false)
        );

        replay(mockDAO);

        mockMvc.
                perform(
                        delete("/Swbservice/v1.0/" + uuids[3]).
                                accept(MediaType.APPLICATION_JSON)
                ).
                andExpect(status().isNotFound()).
                andExpect(content().string("false"));

        assertEquals(uuids[3], id.
                        getValue().
                        toString()
        );

        verify(mockDAO);
    }
}