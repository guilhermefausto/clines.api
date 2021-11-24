package br.com.caelum.clines.api.users;

import static java.util.stream.Collectors.toList;

import java.util.List;

import javax.validation.Valid;

import org.springframework.stereotype.Service;

import br.com.caelum.clines.shared.exceptions.AircraftModelNotFoundException;
import br.com.caelum.clines.shared.exceptions.ResourceAlreadyExistsException;
import br.com.caelum.clines.shared.exceptions.ResourceNotFoundException;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserService {
	
    private final UserRepository repository;
    private final UserViewMapper viewMapper;
    private final UserFormMapper formMapper;
    
	public List<UserView> listAllUsers() {
		return repository.findAll().stream().map(viewMapper::map).collect(toList());
	}

	public UserView showUserById(Long id) {
        var user = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Cannot find user"));

        return viewMapper.map(user);
	}

	public Long createUserBy(@Valid UserForm form) {
	    repository.findByEmail(form.getEmail()).ifPresent(aircraft -> {
	            throw new ResourceAlreadyExistsException("User already exists");
	    });
	
	    var user = formMapper.map(form);
	
	    repository.save(user);
	
	    return user.getId();
	}

}
