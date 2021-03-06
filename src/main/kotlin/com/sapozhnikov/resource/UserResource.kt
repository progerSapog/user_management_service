package com.sapozhnikov.resource

import com.sapozhnikov.mapper.IUserMapper
import com.sapozhnikov.model.dao.UserDAO
import com.sapozhnikov.model.domain.CreateUser
import com.sapozhnikov.model.domain.UpdateUser
import com.sapozhnikov.model.domain.User
import com.sapozhnikov.model.dao.UserEntity
import io.dropwizard.jersey.params.IntParam
import io.dropwizard.validation.OneOf
import io.swagger.annotations.*
import org.hibernate.validator.constraints.Range
import org.jdbi.v3.core.statement.UnableToExecuteStatementException
import java.util.*
import javax.validation.Valid
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

/**
 * Controller for working with the user model
 **/
@Path("/user")
@Api(
    value = "user",
    description = "Rest API for user operations",
    tags = ["User API"]
)
@Produces(MediaType.APPLICATION_JSON)
class UserResource(
    private val userMapperImpl: IUserMapper,
    private val userDao: UserDAO,
) {
    /**
     * Route to create a new user
     * @param newUser - data of the created user
     * @return created user
     * */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(
        value = "creating a new user",
        notes = "creates a new user based on valid data",
        response = User::class
    )
    @ApiResponses(
        value = [
            ApiResponse(code = 200, message = "Ok"),
            ApiResponse(code = 409, message = "Conflict")
        ]
    )
    fun createNewUser(@Valid newUser: CreateUser): Response {
        val userEntity = userMapperImpl.mapToUserEntity(UUID.randomUUID(), newUser, java.time.LocalDate.now())

        try {
            userDao.insertUser(userEntity)
        } catch (exception: UnableToExecuteStatementException) {
            return Response.status(Response.Status.CONFLICT).build()
        }

        return Response.ok(userMapperImpl.mapToUserModel(userEntity)).build()
    }

    /**
     * Route to get all users
     * @return list of all users
     * */
    @GET
    @ApiOperation(
        value = "gets all users",
        notes = "gets all users from database",
        response = User::class,
        responseContainer = "List")
    @ApiResponse(code = 200, message = "Ok")
    fun getAllUsers(
        @QueryParam("limit")
        @Range(min = 0, max = 100)
        @DefaultValue("25")
        limit: IntParam,

        @QueryParam("offset")
        @DefaultValue("0")
        @Range(min = 0, max = 99)
        offset: IntParam,

        @QueryParam("orderBy")
        @OneOf(
            value = ["id", "first_name", "last_name", "age", "login", "email", "registration_date"],
            ignoreCase = false,
            ignoreWhitespace = false
        )
        @DefaultValue("id")
        orderBy: String,

        @QueryParam("sort")
        @OneOf(
            value = ["ASC", "DESC"],
            ignoreCase = false,
            ignoreWhitespace = false
        )
        @DefaultValue("ASC")
        sort: String
    ): Response {
        val userList: List<UserEntity> = userDao.findAllUser(limit.get(), offset.get(), orderBy, sort)

        return Response.ok(
            userList.map { user -> userMapperImpl.mapToUserModel(user) }
        ).build()
    }

    /**
     * Route to get user by id
     * @param id - user id to find
     * @return found user or 404 not found request
     *         if user does not found
     * */
    @GET
    @Path("/{id}")
    @ApiOperation(
        value = "get user",
        notes = "get user by id",
        response = User::class,
    )
    @ApiResponses(
        value = [
            ApiResponse(code = 200, message = "Ok"),
            ApiResponse(code = 404, message = "User is not found")
        ]
    )
    fun getUser(@ApiParam(value = "user id to get", required = true) @PathParam("id") id: UUID): Response {
        val user = userDao.findUserById(id)

        return if (user.isEmpty) {
            Response.status(Response.Status.NOT_FOUND).build()
        }
        else Response.ok(userMapperImpl.mapToUserModel(user.get())).build()
    }

    /**
     * Route to change user data
     * @param id - user id to change
     * @param userToUpdate - changed user data
     * @return changed user or 404 not found request
     *         if user does not found
     * */
    @PUT
    @Path("{id}")
    @ApiOperation(
        value = "updating user data",
        notes = "update user data by passed id",
        response = User::class,
    )
    @ApiResponses(
        value = [
            ApiResponse(code = 200, message = "Ok"),
            ApiResponse(code = 404, message = "User is not found"),
            ApiResponse(code = 409, message = "Conflict")
        ]
    )
    fun updateUser(@ApiParam(value = "user id to update", required = true) @PathParam("id") id: UUID, @Valid userToUpdate: UpdateUser): Response {
        val userEntityOpt = userDao.findUserById(id)

        if (userEntityOpt.isEmpty) {
            return Response.status(Response.Status.NOT_FOUND).build()
        }

        val userEntity = userMapperImpl.mapToUserEntity(
            userEntityOpt.get().id,
            userToUpdate,
            userEntityOpt.get().registrationDate
        )
        try {
            userDao.updateUser(userEntity)
        }
        catch (exception: UnableToExecuteStatementException) {
            return Response.status(Response.Status.CONFLICT).build()
        }

        return Response.ok(userMapperImpl.mapToUserModel(userEntity)).build()
    }

    /**
     * Route to delete a user
     * @param id - user id to delete
     * @return deleted user or 404 not found request
     *         if user does not found
     * */
    @DELETE
    @ApiOperation(
        value = "deleting user",
        notes = "delete user by id",
        response = User::class,
    )
    @ApiResponses(
        value = [
            ApiResponse(code = 200, message = "Ok"),
            ApiResponse(code = 404, message = "User is not found"),
        ]
    )
    @Path("{id}")
    fun deleteUser(@ApiParam(value = "user id to delete", required = true) @PathParam("id") id: UUID): Response {
        val userEntityOpt = userDao.findUserById(id)

        if (userEntityOpt.isEmpty) {
            return Response.status(Response.Status.NOT_FOUND).build()
        }

        userDao.deleteById(id)
        return Response.ok(userMapperImpl.mapToUserModel(userEntityOpt.get())).build()
    }
}