package com.emil.linksy_user.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MediaRequest {
    private Long id;
    private byte[] fileBytes;
}
