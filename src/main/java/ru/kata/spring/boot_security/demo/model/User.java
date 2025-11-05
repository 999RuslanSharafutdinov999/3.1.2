package ru.kata.spring.boot_security.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.Length;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Имя обязательно для заполнения")
    @Pattern(regexp = "^[a-zA-Zа-яА-ЯёЁ\\s\\-]+$",
            message = "Имя может содержать только буквы, пробелы и дефисы")
    @Length(min = 2, max = 50,
            message = "Имя должно содержать от 2 до 50 символов")
    @Column(name = "name", nullable = false)
    private String name;

    @NotBlank(message = "Фамилия обязательна для заполнения")
    @Pattern(regexp = "^[a-zA-Zа-яА-ЯёЁ\\s\\-]+$",
            message = "Фамилия может содержать только буквы, пробелы и дефисы")
    @Length(min = 2, max = 50,
            message = "Фамилия должна содержать от 2 до 50 символов")
    @Column(name = "lastname", nullable = false)
    private String lastname;

    @NotNull(message = "Количество полных лет обязательно")
    @Min(value = 18, message = "Полных лет должно быть не менее 18")
    @Max(value = 100, message = "Полных лет должно быть не более 100")
    @Column(name = "age", nullable = false)
    private Integer age;

    @NotBlank(message = "Имя пользователя обязательно")
    @Column(name = "username", unique = true, nullable = false)
    private String username;

    @NotBlank(message = "Email обязательно")
    @Email(message = "Email не валиден")
    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @NotBlank(message = "Пароль обязателен")
    @Column(name = "password", nullable = false)
    private String password;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    public User() {
    }

    public User(String name, String lastname, Integer age, String username, String email, String password) {
        this.name = name;
        this.lastname = lastname;
        this.age = age;
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLastname() { return lastname; }
    public void setLastname(String lastname) { this.lastname = lastname; }

    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Set<Role> getRoles() { return roles; }
    public void setRoles(Set<Role> roles) { this.roles = roles; }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }

    public String getRolesString() {
        return roles.stream()
                .map(Role::toString)
                .reduce((r1, r2) -> r1 + ", " + r2)
                .orElse("No roles");
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", lastname='" + lastname + '\'' +
                ", age=" + age +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", roles=" + roles +
                '}';
    }
}