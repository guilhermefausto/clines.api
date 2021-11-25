package br.com.caelum.clines.api.users;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.only;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.caelum.clines.shared.domain.User;
import br.com.caelum.clines.shared.exceptions.ResourceAlreadyExistsException;
import br.com.caelum.clines.shared.exceptions.ResourceNotFoundException;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
	
	private static final Long NON_EXISTING_USER_ID = 99l;
	private static final Long USER_ID = 1l;
	private static final String USER_NAME = "Fulano";
	private static final String USER_EMAIL = "fulano@gmail.com";
	private static final String USER_PASSWORD = "123456";
	
	private static final User DEFAULT_USER = new User(USER_ID,USER_NAME,USER_EMAIL,USER_PASSWORD);
	private static final List<User> ALL_USERS = List.of(DEFAULT_USER);
	private static final UserForm USER_FORM = new UserForm(USER_NAME,USER_EMAIL,USER_PASSWORD);
	
    @Spy
    private UserViewMapper viewMapper;
    
    @Spy
    private UserFormMapper formMapper;
	
	@Mock
	private UserRepository repository;
	
    @InjectMocks
    private UserService service;
	
    @Test
    void shouldReturnSingleUserViewWhenExistingInRepository() {


        given(repository.findById(USER_ID)).willReturn(Optional.of(DEFAULT_USER));

        var userView = service.showUserById(USER_ID);

        then(repository).should(only()).findById(USER_ID);
        then(viewMapper).should(only()).map(DEFAULT_USER);
        then(formMapper).shouldHaveNoInteractions();

        assertEquals(USER_EMAIL, userView.getEmail());
        assertEquals(USER_NAME, userView.getName());
    }
    
    @Test
    void shouldThrowExceptionWhenUserIdNotExistingInRepository() {


        given(repository.findById(NON_EXISTING_USER_ID)).willReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.showUserById(NON_EXISTING_USER_ID));
        
        then(repository).should(only()).findById(NON_EXISTING_USER_ID);
        then(viewMapper).shouldHaveNoInteractions();
        then(formMapper).shouldHaveNoInteractions();
    }
    
    @Test
    void shouldReturnAListOfUserViewInRepository() {
        given(repository.findAll()).willReturn(ALL_USERS);

        var allUserViews = service.listAllUsers();

        then(repository).should(only()).findAll();
        then(viewMapper).should(only()).map(DEFAULT_USER);
        then(formMapper).shouldHaveNoInteractions();

        assertEquals(ALL_USERS.size(), allUserViews.size());

        var allUsersView = allUserViews.get(0);

        assertEquals("fulano@gmail.com", allUsersView.getEmail());
        assertEquals(USER_NAME, allUsersView.getName());
    }

    
    @Test
    void shouldReturnAnEmptyListWhenHasNoUserInRepository() {
        given(repository.findAll()).willReturn(new ArrayList<User>());

        var allUserViews = service.listAllUsers();

        assertEquals(0, allUserViews.size());

        then(repository).should(only()).findAll();
        then(viewMapper).shouldHaveNoInteractions();
        then(formMapper).shouldHaveNoInteractions();
    }
    
    @Test
    void shouldCreateAUser() {
        given(formMapper.map(USER_FORM)).willReturn(DEFAULT_USER);
        given(repository.findByEmail(USER_EMAIL)).willReturn(Optional.empty());

        var createdUserId = service.createUserBy(USER_FORM);

        then(formMapper).should(only()).map(USER_FORM);
        then(repository).should().findByEmail(USER_EMAIL);
        then(repository).should().save(DEFAULT_USER);

        assertEquals(USER_ID, createdUserId);
    }

    @Test
    public void shouldThrowResourceAlreadyExistsIfUserAlreadyExists() {
        given(repository.findByEmail(USER_EMAIL)).willReturn(Optional.of(DEFAULT_USER));

        assertThrows(ResourceAlreadyExistsException.class,
                () -> service.createUserBy(USER_FORM)
        );
    }
}
