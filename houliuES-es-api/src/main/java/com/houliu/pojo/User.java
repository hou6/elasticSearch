package com.houliu.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * @author houliu
 * @create 2020-04-09 16:21
 */

@AllArgsConstructor
@NoArgsConstructor
@Data
@Component
public class User {

    private String name;
    private int age;

}
