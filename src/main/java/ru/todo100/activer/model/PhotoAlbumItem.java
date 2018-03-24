package ru.todo100.activer.model;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Set;

/**
 * @author Igor Bobko <limit-speed@yandex.ru>.
 */
@Entity
@Table(name = "photo_album")
public class PhotoAlbumItem extends Item {
    @Id
    @SequenceGenerator(name = "default_gen", sequenceName = "photo_album_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "default_gen")
    public Integer id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", nullable = false)
    private String description;

    @ManyToOne
    @NotFound(action= NotFoundAction.EXCEPTION)
    @JoinColumn(name = "photo_id")
    private PhotoItem cover;

    @NotNull
    @Column(name = "account_id", nullable = false)
    private Integer accountId;

    @OneToMany(orphanRemoval = true)
    @JoinColumn(name = "album_id")
    private Set<PhotoItem> photos;

    public Set<PhotoItem> getPhotos() {
        return photos;
    }

    public void setPhotos(Set<PhotoItem> photos) {
        this.photos = photos;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public PhotoItem getCover() {
        return cover;
    }

    public void setCover(PhotoItem cover) {
        this.cover = cover;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAccountId() {
        return accountId;
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }
}
