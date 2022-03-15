package models

import com.fasterxml.jackson.annotation.JsonProperty
import org.hibernate.validator.constraints.Length
import org.hibernate.validator.constraints.Range
import java.time.LocalDate
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank


data class User(
    @JsonProperty("id")
    val id: Int,

    @NotBlank @Length (min = 2, max = 50)
    @JsonProperty("first_name")
    val firstName: String,

    @NotBlank
    @Length (min = 2, max = 50)
    @JsonProperty("last_name")
    val lastName: String,

    @Range(min = 16, max = 99)
    @JsonProperty("age")
    val age: Int,

    @Length (min = 4, max = 50)
    @JsonProperty("login")
    val login: String,

    @Email(regexp = "[A-Za-z0-9._%-+]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}")
    @JsonProperty("email")
    val email: String,

    @JsonProperty("registration_date")
    val registrationDate: LocalDate
)
{
    companion object {
        val usersList: MutableList<User> = mutableListOf(
            User(1,"Default", "User", 18, "defUser", "default.user@gmail.com", LocalDate.now())
        )
    }
}