package com.emil.linksy_user.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EditMessageResponse {
    private Long id;
    private String text;
}