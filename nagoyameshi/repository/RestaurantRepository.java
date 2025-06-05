package com.example.nagoyameshi.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.nagoyameshi.entity.Restaurant;

public interface RestaurantRepository extends JpaRepository<Restaurant, Integer> {
    public Page<Restaurant> findByNameLike(String keyword, Pageable pageable);
    public Restaurant findFirstByOrderByIdDesc();
    public Page<Restaurant> findAllByOrderByCreatedAtDesc(Pageable pageable);
    public Page<Restaurant> findAllByOrderByLowestPriceAsc(Pageable pageable);

    // 指定されたキーワードを店舗名または住所またはカテゴリ名に含む店舗を作成日時が新しい順に並べ替え、ページングされた状態で取得する
    @Query("SELECT DISTINCT r FROM Restaurant r " +
           "LEFT JOIN r.categoriesRestaurants cr " +
           "WHERE r.name LIKE %:name% " +
           "OR r.address LIKE %:address% " +
           "OR cr.category.name LIKE %:categoryName% " +
           "ORDER BY r.createdAt DESC")
    public Page<Restaurant> findByNameLikeOrAddressLikeOrCategoryNameLikeOrderByCreatedAtDesc(@Param("name") String nameKeyword,
                                                                                              @Param("address") String addressKeyword,
                                                                                              @Param("categoryName") String categoryNameKeyword,
                                                                                              Pageable pageable);

    // 指定されたキーワードを店舗名または住所またはカテゴリ名に含む店舗を最低価格が安い順に並べ替え、ページングされた状態で取得する
    @Query("SELECT DISTINCT r FROM Restaurant r " +
           "LEFT JOIN r.categoriesRestaurants cr " +
           "WHERE r.name LIKE %:name% " +
           "OR r.address LIKE %:address% " +
           "OR cr.category.name LIKE %:categoryName% " +
           "ORDER BY r.lowestPrice ASC")
    public Page<Restaurant> findByNameLikeOrAddressLikeOrCategoryNameLikeOrderByLowestPriceAsc(@Param("name") String nameKeyword,
                                                                                               @Param("address") String addressKeyword,
                                                                                               @Param("categoryName") String categoryNameKeyword,
                                                                                               Pageable pageable);

    // 指定されたidのカテゴリが設定された店舗を作成日時が新しい順に並べ替え、ページングされた状態で取得する
    @Query("SELECT r FROM Restaurant r " +
           "INNER JOIN r.categoriesRestaurants cr " +
           "WHERE cr.category.id = :categoryId " +
           "ORDER BY r.createdAt DESC")
    public Page<Restaurant> findByCategoryIdOrderByCreatedAtDesc(@Param("categoryId") Integer categoryId, Pageable pageable);

    // 指定されたidのカテゴリが設定された店舗を最低価格が安い順に並べ替え、ページングされた状態で取得する
    @Query("SELECT r FROM Restaurant r " +
           "INNER JOIN r.categoriesRestaurants cr " +
           "WHERE cr.category.id = :categoryId " +
           "ORDER BY r.lowestPrice ASC")
    public Page<Restaurant> findByCategoryIdOrderByLowestPriceAsc(@Param("categoryId") Integer categoryId, Pageable pageable);

    public Page<Restaurant> findByLowestPriceLessThanEqualOrderByCreatedAtDesc(Integer price, Pageable pageable);
    public Page<Restaurant> findByLowestPriceLessThanEqualOrderByLowestPriceAsc(Integer price, Pageable pageable);    
}