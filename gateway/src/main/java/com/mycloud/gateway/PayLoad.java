package com.mycloud.gateway;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@ToString
@Getter
@Setter
@NoArgsConstructor
public class PayLoad {

    private String name;
    private List<String> list;
}
