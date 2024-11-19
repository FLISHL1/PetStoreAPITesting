package ru.flish1.testtaskpetshop.entity;

import lombok.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Pet {
    private Long id;
    private Category category;
    private String name;
    private List<String> photoUrls;
    private List<Tag> tags;
    private Status status;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pet pet = (Pet) o;
        return Objects.equals(id, pet.id) && Objects.equals(category, pet.category) && Objects.equals(name, pet.name) && Objects.equals(photoUrls, pet.photoUrls) && Objects.equals(tags, pet.tags) && status == pet.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, category, name, photoUrls, tags, status);
    }
}
