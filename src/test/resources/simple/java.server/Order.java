import java.util.Objects;

public class Order {
    private Integer id;
    private Integer petId;
    private Integer quantity;
    private Boolean complete;

    public Order() {
    }

    public Order(Integer id, Integer petId, Integer quantity, Boolean complete) {
        this.id = id;
        this.petId = petId;
        this.quantity = quantity;
        this.complete = complete;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPetId() {
        return petId;
    }

    public void setPetId(Integer petId) {
        this.petId = petId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Boolean getComplete() {
        return complete;
    }

    public void setComplete(Boolean complete) {
        this.complete = complete;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order that = (Order) o;
        return Objects.equals(id, that.id) && Objects.equals(petId, that.petId) && Objects.equals(quantity, that.quantity) && Objects.equals(complete, that.complete);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, petId, quantity, complete);
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", petId=" + petId +
                ", quantity=" + quantity +
                ", complete=" + complete +
                '}';
    }
}
