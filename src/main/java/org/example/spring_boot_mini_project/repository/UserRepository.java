package org.example.spring_boot_mini_project.repository;

import org.apache.ibatis.annotations.*;


import org.example.spring_boot_mini_project.model.User;
import org.example.spring_boot_mini_project.model.dto.request.AppUserRequest;
import org.example.spring_boot_mini_project.model.dto.request.PasswordRequest;
import org.example.spring_boot_mini_project.typehandler.UUIDTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.example.spring_boot_mini_project.model.User;
import org.example.spring_boot_mini_project.model.dto.request.AppUserRequest;



import java.util.UUID;

@Mapper
public interface UserRepository {

//    @Results(id ="userMapping", value = {
//            @Result(property = "userId", column = "user_id",typeHandler = UUIDTypeHandler.class),
//            @Result(property = "email", column = "email"),
//            @Result(property = "password", column = "password"),
//            @Result(property = "profileImage", column = "profile_image"),
//    )

    @Select("""
           SELECT * FROM users
           WHERE user_id = #{id}::UUID
           """)
    @Results(id = "UserMapping",value ={
            @Result(property = "profileImage", column = "profile_image"),
            @Result(property = "userId", column = "user_id",typeHandler = UUIDTypeHandler.class)

    })
    User findById(UUID id);
    @Select("""
           SELECT * FROM users
           WHERE email = #{email}
           """)

    @ResultMap("userMapping")



    User findByEmail(String email);

    @Select("""
           INSERT INTO users (email, password, profile_image)
           VALUES ( #{u.email}, #{u.password}, #{u.profileImage})
           Returning *
           """)

    @ResultMap("UserMapping")

    User insert(@Param("u") AppUserRequest User);

    @Update("""
    UPDATE users
    SET password = #{u.password}
    WHERE email= #{email}
    """)
    void newPassword(@Param("u")PasswordRequest passwordRequest, String email);
}
