package com.ersted.springapp.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "file")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
public class File extends BaseEntity {
    @Column(name = "file_name")
    private String fileName;

    @Column(name = "uuid")
    private String uuid;

    @Column(name = "location")
    private String location;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private Status status;

    @ToString.Exclude
    @OneToMany(mappedBy = "file", fetch = FetchType.LAZY)
    private List<Event> events;
}
