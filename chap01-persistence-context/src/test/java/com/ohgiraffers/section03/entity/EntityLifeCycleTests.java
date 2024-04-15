package com.ohgiraffers.section03.entity;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

public class EntityLifeCycleTests {
    private EntityLifeCycle lifeCycle;

    @BeforeEach
    void setup() {
        this.lifeCycle = new EntityLifeCycle();

    }

    @DisplayName("비영속 테스트")
    @ParameterizedTest
    @ValueSource(ints = {1,2})
    void testTransient(int menuCode) {
        //when
        Menu foundMenu = lifeCycle.findMenuByMenuCode(menuCode);

        Menu newMenu = new Menu(
                foundMenu.getMenuCode(),
                foundMenu.getMenuName(),
                foundMenu.getMenuPrice(),
                foundMenu.getCategoryCode(),
                foundMenu.getOrderableStatus()
        );

        EntityManager entityManager = lifeCycle.getManagerInstance();


        //then
        assertNotEquals(foundMenu, newMenu);
        assertTrue(entityManager.contains(foundMenu)); //foundMenu는 영속성 컨텍스트에서 관리 되는 영속 상태의 객체
        assertFalse(entityManager.contains(newMenu)); // newMenu는 영속성 컨텍스트에서 관리 되지 않는 비영속 상태의 객체

    }

    @DisplayName("다른 엔터티 매니저가 관리하는 엔터티의 영속성 테스트")
    @ParameterizedTest
    @ValueSource(ints = {28,30})
    void testManagedOtherEntityManager(int menuCode) {
        //when
        Menu menu1 = lifeCycle.findMenuByMenuCode(menuCode);
        Menu menu2 = lifeCycle.findMenuByMenuCode(menuCode);

        //then
        assertNotEquals(menu1, menu2);

    }
    @DisplayName("같은 엔터티 매니저가 관리하는 엔터티의 영속성 테스트")
    @ParameterizedTest
    @ValueSource(ints = {28,30})
    void testManagedSameEntityManager(int menuCode) {
        //given
        EntityManager manager = EntityManagerGenerator.getInstance();


        //when
        Menu menu1 = manager.find(Menu.class, menuCode);
        Menu menu2 = manager.find(Menu.class, menuCode);


        //then
        assertNotEquals(menu1, menu2);

    }

    @DisplayName("준영속화 detach 테스트")
    @ParameterizedTest
    @CsvSource({"11, 1000", "12,3000"})
    void testDetachEntity(int menuCode, int menuPrice) {
        EntityManager entityManager = EntityManagerGenerator.getInstance();
        EntityTransaction entityTransaction = entityManager.getTransaction();

        //when
        entityTransaction.begin();
        Menu foundMenu = entityManager.find(Menu.class, menuCode);

        //detach : 특정 엔터티만 준영속 상태 (영속성 컨텍스트가 관리하던 객체를 관리하지 않는 상태)로 만든다.
        entityManager.detach(foundMenu);
        foundMenu.setMenuPrice(menuPrice);

        //flush : 영속성 컨텍스트의 상태를 DB로 내보낸다. commit하지 않은 상태이므로 rollback 가능하다.
        // 내보내기 => 커밋보다 먼저 실행되며, 플러쉬만 하면 커밋한 상태는 아니어서 롤백가능.
        entityManager.flush();

        //then
        assertNotEquals(menuPrice, entityManager.find(Menu.class, menuCode).getMenuPrice());
        entityTransaction.rollback();

    }


    @DisplayName("준영속화 detach 한 후 영속화 테스트")
    @ParameterizedTest
    @CsvSource({"11, 1000", "12,3000"})
    void testDetachAndMerge(int menuCode, int menuPrice) {
        EntityManager entityManager = EntityManagerGenerator.getInstance();
        EntityTransaction entityTransaction = entityManager.getTransaction();

        //when
        entityTransaction.begin();
        Menu foundMenu = entityManager.find(Menu.class, menuCode);

        //detach : 특정 엔터티만 준영속 상태 (영속성 컨텍스트가 관리하던 객체를 관리하지 않는 상태)로 만든다.
        entityManager.detach(foundMenu);
        foundMenu.setMenuPrice(menuPrice);
        //merge : 파라미터로 넘어온 준영속 엔터티 객체의 식별자 값으로 1차 캐시에서 엔티티 객체를 조회한다.
        //(없으면 DB에서 조회하여 1차 캐시에 저장한다)
        //조회한 영속 엔터티 객체에 준영속 상태의 엔터티 객체의 값을 병합 한 뒤 영속 엔터티 객체를 반환한다.
        // 혹은 조회 할 수 없는 데이터라면 새로 생성해서 병합한다.
        entityManager.merge(foundMenu);
        entityManager.flush();


        //then
        assertEquals(menuPrice, entityManager.find(Menu.class, menuCode).getMenuPrice());
        entityTransaction.rollback();

    }

    @DisplayName("detach 후 merge한 데이터 update 테스트")
    @ParameterizedTest
    @CsvSource({"11, 하양민트초코죽", "12, 까만 탕후루죽"})
    void testMergeUpdate(int menuCode, String menuName) {
        //given
        EntityManager entityManager = EntityManagerGenerator.getInstance();
        Menu foundMenu = entityManager.find(Menu.class, menuCode);
        entityManager.detach(foundMenu);

        //when
        foundMenu.setMenuName(menuName);
        Menu refoundMenu = entityManager.find(Menu.class, menuCode);

        entityManager.merge(foundMenu);


        //then
        assertEquals(menuName, refoundMenu.getMenuName());
    }

    @DisplayName("detach 후 merge한 데이터 save 테스트")
    @Test
    void testMergeSave() {
        //given
        EntityManager entityManager = EntityManagerGenerator.getInstance();
        EntityTransaction entityTransaction = entityManager.getTransaction();
        Menu foundMenu = entityManager.find(Menu.class, 20);
        entityManager.detach(foundMenu);

        //when
        entityTransaction.begin();
        foundMenu.setMenuName("치약맛 초코 아이스크림");
        foundMenu.setMenuCode(999);

        entityManager.merge(foundMenu);
        entityTransaction.commit();

        //then
        assertEquals("치약맛 초코 아이스크림", entityManager.find(Menu.class, 999).getMenuName());
    }

}
