package org.shanzhaozhen.multipledatasourcejpa.entity.primary;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "sys_user")
@NoArgsConstructor
@AllArgsConstructor
public class SysUser extends BaseBean {

    private static final long serialVersionUID = 3064727069207896868L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @JsonIgnore
    @Column(nullable = false)
    private String password;

    private String nickname;

    private String fullName;

    private Integer sex;

    private Date birthday;

    private String headImg;

    private String email;

    private String phoneNumber;

    private String address;

    private String introduction;

}
