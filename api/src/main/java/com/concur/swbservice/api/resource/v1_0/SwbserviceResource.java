package com.concur.swbservice.api.resource.v1_0;

import com.concur.swbservice.api.model.v1_0.SwbserviceModel;
import io.swagger.annotations.ApiOperation;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rx.Observable;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * Created by mtalbot on 11/08/2015.
 */
@RequestMapping("/Swbservice/v1.0/")
@ExposesResourceFor(SwbserviceModel.class)
@RestController
public interface SwbserviceResource {

    @RequestMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
    @ApiOperation(code = 200, value = "listSwbservices")
    Observable<ResponseEntity<Resources<Resource<SwbserviceModel>>>> list();

    @RequestMapping(value = "/view", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
    @ApiOperation(code = 200, value = "viewSwbservices")
    Observable<Collection<Link>> view();

    @RequestMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
    @ApiOperation(code = 200, value = "getSwbservice")
    Observable<ResponseEntity<Resource<SwbserviceModel>>> get(@PathVariable("id") UUID id);

    @RequestMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.PUT)
    @ApiOperation(code = 202, value = "saveSwbservice")
    Observable<ResponseEntity<Resource<SwbserviceModel>>> save(@PathVariable("id") UUID id, @RequestBody @Valid SwbserviceModel entity);

    @ApiOperation(code = 200, value = "deleteSwbservice")
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    ResponseEntity<Boolean> delete(@PathVariable("id") UUID id);
}
