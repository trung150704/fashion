package com.example.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.entity.Order;
import com.example.entity.User;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
	Optional<Order> findById(Integer id);
	@Query("SELECT o FROM Order o WHERE o.orderCode = :orderCode AND o.recipient_phone = :recipientPhone")
	Optional<Order> findByOrderCodeAndRecipientPhone(@Param("orderCode") String orderCode,
			@Param("recipientPhone") String recipientPhone);
	List<Order> findByUser(User user);
	void deleteById(Integer id);
	
		//theo thang
		@Query("SELECT FUNCTION('MONTH', o.order_date), SUM(o.total_price) " +
	           "FROM Order o " +
	           "WHERE o.status = 'delivered' " +
	           "GROUP BY FUNCTION('MONTH', o.order_date) " +
	           "ORDER BY FUNCTION('MONTH', o.order_date)")
	    List<Object[]> getMonthlyRevenue();

	    // Doanh thu theo ngày
	    @Query("SELECT FUNCTION('DATE', o.order_date), SUM(o.total_price) " +
	           "FROM Order o " +
	           "WHERE o.status = 'delivered' " +
	           "GROUP BY FUNCTION('DATE', o.order_date) " +
	           "ORDER BY FUNCTION('DATE', o.order_date)")
	    List<Object[]> getDailyRevenue();
}
