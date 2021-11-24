package br.com.caelum.clines.api.users;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class UserForm {

	    @NotBlank
	    private String name;

	    @NotBlank @Email
	    private String email;

	    @NotBlank
	    private String password;

	    UserForm(String name, String email, String password) {
	        this.name = name;
	        this.email = email;
	        this.password = password;
	    }

}
