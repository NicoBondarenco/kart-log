package org.kart.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.FetchType.EAGER;
import static lombok.AccessLevel.PRIVATE;
import static org.hibernate.annotations.FetchMode.SUBSELECT;

@Entity
@Table(name = "kart_log")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = PRIVATE)
public class KartLog implements Serializable {

    @Id
    @Column(name = "id", nullable = false, length = 36)
    private UUID id;

    @Column(name = "name", nullable = false, length = 250)
    private String name;

    @Column(name = "ocurred", nullable = false)
    private OffsetDateTime ocurred;

    @OneToMany(mappedBy = "kartLog", cascade = ALL, fetch = EAGER)
    @Fetch(SUBSELECT)
    private List<KartLogItem> items;

}
