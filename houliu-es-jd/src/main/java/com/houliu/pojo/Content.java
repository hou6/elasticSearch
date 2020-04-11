package com.houliu.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author houliu
 * @create 2020-04-10 22:30
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Content {

    private String title;
    private String img;
    private String price;

}
