package com.ohgiraffers.section02.crud;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

public class EntityManagerCRUD {
    private EntityManager entityManager;

    /*1. 특정 코드로 조회하는 기능*/

    public Menu findMenuByMenuCode(int menuCode) {
        entityManager = EntityManagerGenerator.getInstance();

        return entityManager.find(Menu.class, menuCode);
    }

    /*2. 메뉴 데이터 저장하는 기능*/
    public Long saveAndReturnAllCount(Menu newMenu) {
        entityManager = EntityManagerGenerator.getInstance();
        EntityTransaction entityTransaction = entityManager.getTransaction();
        entityTransaction.begin();
        entityManager.persist(newMenu);
        entityTransaction.commit();
        return getCount(entityManager);
    }

    private Long getCount(EntityManager entityManager) {
        //JPQL 문법 -> 나중에 별도 챕터에서 다룰 예정
        return entityManager.createQuery("SELECT COUNT(*) FROM Section02Menu", Long.class).getSingleResult();
    }

    /*3. 메뉴 데이터 수정*/
    public Menu modifyMenuName(int menuCode, String menuName) {
        entityManager = EntityManagerGenerator.getInstance();
        Menu foundMenu = entityManager.find(Menu.class, menuCode);
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();

        foundMenu.setMenuName(menuName);
        transaction.commit();

        return foundMenu;

    }

    /*4. 메뉴 삭제 */
    public Long removeAndReturnAllCount(int menuCode) {
        entityManager = EntityManagerGenerator.getInstance();
        Menu foundMenu = EntityManager.find(Menu.class, menuCode);

        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();

        entityManager.remove(foundMenu);
        transaction.remove(foundMenu);
        transaction.commit();

        return

    }

}

