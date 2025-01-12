package com.emil.linksy_user.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OptionResponse {
    private Long optionId;
    private String optionText;
    private Long selectedCount;
}
