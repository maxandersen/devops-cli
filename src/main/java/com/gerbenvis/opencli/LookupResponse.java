package com.gerbenvis.opencli;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LookupResponse {

    private String ip;
    private String cname;
}
