package com.kse.wmsv2.oc_cs_003.entity;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Builder
@Getter
@Setter
@Data
@Entity
@Table(name = "CM_USER")
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {

    @Id
    @GeneratedValue
    @Column(name = "USERCD", length = 20)
    private String userCd;

    @Column(name = "PASSWORD", length = 20)
    private String password;

    @Column(name = "STARTDATE")
    private String startDate;

    @Column(name = "ENDDATE")
    private String endDate;

    @Column(name = "PREPASSWORD", length = 20)
    private String prePassword;
    @Column(name = "UERNAME", length = 60)
    private String userName;
    @Column(name = "USERNAMESYLLABARY", length = 60)
    private String userNameSyllabary;
    @Column(name = "USERNAMEENG", length = 60)
    private String userNameEng;
    @Column(name = "USERMANAGEMENTAUTHORITYCD", length = 5)
    private String userManagementAuthorityCd;
    @Column(name = "USERAUTHORITYCD", length = 5)
    private String userAuthorityCd;
    @Column(name = "USERCOMPANYCD", length = 20)
    private String userCompanyCd;
    @Column(name = "DEPARTMENTCD", length = 20)
    private String departmentCd;
    @Column(name = "REGUSERCD", length = 20)
    private String regUserCd;
    @Column(name = "REGDATE")
    private String regDate;
    @Column(name = "UPDATEUSERCD", length = 20)
    private String updateUserCd;
    @Column(name = "UPDATEDATE")
    private String updateDate;
    @Column(name = "USERAUTHORITYGROUPCD", length = 20)
    private String userAuthorityGroupCd;
    @Column(name = "PRINTUSERCOMPANYNM", length = 60)
    private String printUserCompanyNm;
    @Column(name = "PRINTUSERCOMPANYCD", length = 20)
    private String printUserCompanyCd;
    @Column(name = "PRINTUSERNAME", length = 60)
    private String printUserNm;

    @ElementCollection(fetch = FetchType.EAGER)
    @Builder.Default
    private List<String> roles = new ArrayList<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return userName;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}

