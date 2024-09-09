package com.example.demo.entity;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Cart")
public class ChartEntiy {

	    @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    @Column(name = "cart_item_id")
	    private int cartItemId;

	    @ManyToOne
	    @JoinColumn(name = "user_id", referencedColumnName = "user_id", foreignKey = @ForeignKey(name = "Cart_ibfk_1"))
	    private UsersEntity user;

	    @ManyToOne
	    @JoinColumn(name = "product_id", referencedColumnName = "product_id", foreignKey = @ForeignKey(name = "Cart_ibfk_2"))
	    private ProductEntity product;

	    @Column(name = "quantity", nullable = false)
	    private int quantity;

	    @Column(name = "created_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	    private LocalDateTime createdAt;

	    // Getters and Setters

	    public int getCartItemId() {
	        return cartItemId;
	    }

	    public void setCartItemId(int cartItemId) {
	        this.cartItemId = cartItemId;
	    }

	    public UsersEntity getUser() {
	        return user;
	    }

	    public void setUser(UsersEntity user) {
	        this.user = user;
	    }

	    public ProductEntity getProduct() {
	        return product;
	    }

	    public void setProduct(ProductEntity product) {
	        this.product = product;
	    }

	    public int getQuantity() {
	        return quantity;
	    }

	    public void setQuantity(int quantity) {
	        this.quantity = quantity;
	    }

	    public LocalDateTime getCreatedAt() {
	        return createdAt;
	    }

	    public void setCreatedAt(LocalDateTime createdAt) {
	        this.createdAt = createdAt;
	    }

}
