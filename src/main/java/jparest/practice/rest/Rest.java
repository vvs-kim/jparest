package jparest.practice.rest;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
@Setter
public class Rest {

    @Id
    @GeneratedValue
    @Column(name = "rest_id")
    private Long id;
    private String restname;
}