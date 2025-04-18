package tn.esprit.evennement.dto;

import lombok.Data;

import java.util.Date;
@Data
public class EventRequest {



    private String name;
    private String description;
    private String type;

    private Date dateDeb;
    private Date dateFin;

    private Long userId;
}
