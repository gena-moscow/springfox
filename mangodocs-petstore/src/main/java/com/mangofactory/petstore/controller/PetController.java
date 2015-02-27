/**
 *  Copyright 2014 Reverb Technologies, Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.mangofactory.petstore.controller;

import com.mangofactory.petstore.Responses;
import com.mangofactory.petstore.model.Pet;
import com.mangofactory.petstore.repository.MapBackedRepository;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import static com.mangofactory.petstore.model.Pets.*;
import static org.springframework.http.MediaType.*;
import static org.springframework.web.bind.annotation.RequestMethod.*;

@Controller
@RequestMapping(value = "/api/pet", produces = {APPLICATION_JSON_VALUE, APPLICATION_XML_VALUE})
@Api(value = "/pet", description = "Operations about pets")
public class PetController {

  PetRepository petData = new PetRepository();

  static class PetRepository extends MapBackedRepository<Long, Pet> {
    public List<Pet> findPetByStatus(String status) {
      return where(statusIs(status));
    }

    public List<Pet> findPetByTags(String tags) {
      return where(tagsContain(tags));
    }
  }

  @RequestMapping(value = "/{petId}", method = GET)
  @ApiOperation(
          value = "Find pet by ID", notes = "Returns a pet when ID < 10. ID > 10 or nonintegers will simulate API " +
          "error conditions",
          response = Pet.class)
  @ApiResponses(value = {
          @ApiResponse(code = 400, message = "Invalid ID supplied"),
          @ApiResponse(code = 404, message = "Pet not found")}
  )
  public ResponseEntity<Pet> getPetById(
          @ApiParam(value = "ID of pet that needs to be fetched", allowableValues = "range[1,5]", required = true)
          @PathVariable("petId") String petId)
          throws NotFoundException {
    Pet pet = petData.get(Long.valueOf(petId));
    if (null != pet) {
      return Responses.ok(pet);
    } else {
      throw new NotFoundException(404, "Pet not found");
    }
  }

  @RequestMapping(method = POST)
  @ApiOperation(value = "Add a new pet to the store")
  @ApiResponses(value = {@ApiResponse(code = 405, message = "Invalid input")})
  public ResponseEntity<String> addPet(
          @ApiParam(value = "Pet object that needs to be added to the store", required = true) Pet pet) {
    petData.add(pet);
    return Responses.ok("SUCCESS");
  }

  @RequestMapping(method = PUT)
  @ApiOperation(value = "Update an existing pet")
  @ApiResponses(value = {@ApiResponse(code = 400, message = "Invalid ID supplied"),
          @ApiResponse(code = 404, message = "Pet not found"),
          @ApiResponse(code = 405, message = "Validation exception")})
  public ResponseEntity<String> updatePet(
          @ApiParam(value = "Pet object that needs to be added to the store", required = true) Pet pet) {
    petData.add(pet);
    return Responses.ok("SUCCESS");
  }

  @RequestMapping(value = "/findByStatus", method = GET)
  @ApiOperation(
          value = "Finds Pets by status",
          notes = "Multiple status values can be provided with comma seperated strings",
          response = Pet.class,
          responseContainer = "List")
  @ApiResponses(value = {@ApiResponse(code = 400, message = "Invalid status value")})
  public ResponseEntity<List<Pet>> findPetsByStatus(
          @ApiParam(value = "Status values that need to be considered for filter",
                  required = true,
                  defaultValue = "available",
                  allowableValues = "available,pending,sold",
                  allowMultiple = true)
          @RequestParam("status") String status) {
    return Responses.ok(petData.findPetByStatus(status));
  }

  @RequestMapping(value = "/findByTags", method = GET)
  @ApiOperation(
          value = "Finds Pets by tags",
          notes = "Muliple tags can be provided with comma seperated strings. Use tag1, tag2, tag3 for testing.",
          response = Pet.class,
          responseContainer = "List")
  @ApiResponses(value = {@ApiResponse(code = 400, message = "Invalid tag value")})
  @Deprecated
  public ResponseEntity<List<Pet>> findPetsByTags(
          @ApiParam(
                  value = "Tags to filter by",
                  required = true,
                  allowMultiple = true)
          @RequestParam("tags") String tags) {
    return Responses.ok(petData.findPetByTags(tags));
  }
}
