package com.example.nagoyameshi.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.nagoyameshi.entity.User;

public interface UserRepository extends JpaRepository<User, Integer> {

    // メールアドレスでユーザーを取得
    public User findByEmail(String email);

    // 名前またはふりがなで検索（管理画面の会員検索などに使用）
    public Page<User> findByNameLikeOrFuriganaLike(String nameKeyword, String furiganaKeyword, Pageable pageable);

    // 特定のロール名でユーザーをカウント（例：ROLE_PAID_MEMBER）
    public long countByRole_Name(String roleName);

    // 管理者を除いた無料会員のカウント
    @Query("SELECT COUNT(u) FROM User u WHERE u.role.name != 'ROLE_PAID_MEMBER'")
    public long countFreeMembers();
}
