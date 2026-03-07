import java.util.Objects;

public class Pet {
    private Integer id;
    private String name;
    private Category category;
    private String status;

    public Pet() {
    }

    public Pet(Integer id, String name, Category category, String status) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.status = status;
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

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pet that = (Pet) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(category, that.category) && Objects.equals(status, that.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, category, status);
    }

    @Override
    public String toString() {
        return "Pet{" +
                "id=" + id +
                ", name=" + name +
                ", category=" + category +
                ", status=" + status +
                '}';
    }
}
